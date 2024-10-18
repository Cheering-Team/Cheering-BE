package com.cheering.community;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanRepository;
import com.cheering.community.relation.FanType;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.team.sport.Sport;
import com.cheering.user.Role;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final CommunityRepository communityRepository;
    private final FanRepository fanRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final BadWordService badWordService;
    private final S3Util s3Util;

    public List<CommunityResponse.CommunityDTO> getCommunities(String name, User user) {
        name = name.replace(" ", "");
        List<Community> communities = communityRepository.findByNameOrTeamName(name).stream().sorted(Comparator.comparing(community -> community.getTeam() != null ? 0 : 1)).toList();

        return communities.stream().map((community -> {
            long fanCount = fanRepository.countByCommunity(community);

            List<Team> teams = teamPlayerRepository.findByCommunity(community);

            List<TeamResponse.TeamDTO> teamDTOS = teams.stream().map((team -> {
                Optional<Community> teamCommunity = communityRepository.findByTeam(team);
                return new TeamResponse.TeamDTO(team, null, teamCommunity.map(Community::getId).orElse(null));
            })).toList();

            Optional<Fan> fan = fanRepository.findByCommunityAndUser(community, user);

            if(fan.isPresent()) {
                if(community.getTeam() != null) {
                    return new CommunityResponse.CommunityDTO(community, fanCount, fan.get(), null, community.getTeam().getLeague().getSport().getName(), community.getTeam().getLeague().getName(), Objects.equals(user.getCommunity(), community), null);
                }
                return new CommunityResponse.CommunityDTO(community, fanCount, fan.get(), teamDTOS, null, null, Objects.equals(user.getCommunity(), community), null);
            } else {
                if(community.getTeam() != null) {
                    return new CommunityResponse.CommunityDTO(community, fanCount, null, null, community.getTeam().getLeague().getSport().getName(), community.getTeam().getLeague().getName(), Objects.equals(user.getCommunity(), community), null);
                }
                return new CommunityResponse.CommunityDTO(community, fanCount, null, teamDTOS, null, null, Objects.equals(user.getCommunity(), community), null);
            }
        })).toList();
    }

    // 특정 팀, 소속 선수 커뮤니티 조회
    @Transactional
    public CommunityResponse.PlayersOfTeamDTO getCommunitiesByTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        List<Community> communities = teamPlayerRepository.findByTeam(team);
        Optional<Community> teamCommunity = communityRepository.findByTeam(team);

        League league = team.getLeague();
        Sport sport = league.getSport();

        List<CommunityResponse.CommunityDTO> communityDTOS = communities.stream().map((community)-> {
            long fanCount = fanRepository.countByCommunity(community);
            Optional<Fan> fan = fanRepository.findByCommunityAndUser(community, user);
            return fan.map(value -> new CommunityResponse.CommunityDTO(community, fanCount, value, null, null, null, null, null)).orElseGet(() -> new CommunityResponse.CommunityDTO(community, fanCount, null, null, null, null, null, null));

        }).toList();

        TeamResponse.TeamDTO teamDTO = teamCommunity.map(community -> new TeamResponse.TeamDTO(team, fanRepository.countByCommunity(community), community.getId())).orElseGet(() -> new TeamResponse.TeamDTO(team, null, null));

        return new CommunityResponse.PlayersOfTeamDTO(sport, league, teamDTO, communityDTOS);
    }

    // 특정 커뮤니티 정보 조회
    @Transactional
    public CommunityResponse.CommunityDTO getCommunityInfo(Long communityId, User user) {
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));

        long fanCount = fanRepository.countByCommunity(community);
        List<Team> teams = teamPlayerRepository.findByCommunity(community);

        List<TeamResponse.TeamDTO> teamDTOS = teams.stream().map((team -> {
            Optional<Community> teamCommunity = communityRepository.findByTeam(team);
            return new TeamResponse.TeamDTO(team, null, teamCommunity.map(Community::getId).orElse(null));
        })).toList();

        Optional<Fan> fan = fanRepository.findByCommunityAndUser(community, user);

        if(fan.isPresent()) {
            if(community.getTeam() != null) {
                return new CommunityResponse.CommunityDTO(community, fanCount, fan.get(), null, community.getTeam().getLeague().getSport().getName(), community.getTeam().getLeague().getName(), user.getCommunity() != null && user.getCommunity().getId().equals(community.getId()), null);
            }
            return new CommunityResponse.CommunityDTO(community, fanCount, fan.get(), teamDTOS, null, null, user.getCommunity() != null && user.getCommunity().getId().equals(community.getId()), null);
        } else {
            if(community.getTeam() != null) {
                return new CommunityResponse.CommunityDTO(community, fanCount, null, null, community.getTeam().getLeague().getSport().getName(), community.getTeam().getLeague().getName(), user.getCommunity() != null && user.getCommunity().getId().equals(community.getId()), null);
            }
            return new CommunityResponse.CommunityDTO(community, fanCount, null, teamDTOS, null, null, user.getCommunity() != null && user.getCommunity().getId().equals(community.getId()), null);
        }
    }

    @Transactional
    public void joinCommunity(Long communityId, String name, MultipartFile image, User user) {
        Community community = communityRepository.findById(communityId).orElseThrow(() -> new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));

        if((user.getRole().equals(Role.TEAM) || user.getRole().equals(Role.PLAYER)) && user.getCommunity().getId().equals(communityId)) {
            Fan manager = Fan.builder()
                    .type(FanType.MANAGER)
                    .community(community)
                    .user(user)
                    .name(community.getKoreanName())
                    .image(community.getImage())
                    .build();

            fanRepository.save(manager);
            community.setManager(manager);

            return;
        }

        Optional<Fan> duplicatePlayerUser = fanRepository.findByCommunityAndName(community, name);

        if(duplicatePlayerUser.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NAME);
        }

        if(badWordService.containsBadWords(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        if(community.getKoreanName().equals(name) || community.getEnglishName().equals(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        String imageUrl = "";
        if(image == null) {
            imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-profile.jpg";
        } else {
            imageUrl = s3Util.upload(image);
        }


        Fan fan = Fan.builder()
                .type(FanType.MANAGER)
                .community(community)
                .user(user)
                .name(name)
                .image(imageUrl)
                .build();

        fanRepository.save(fan);
    }

    public List<CommunityResponse.CommunityDTO> getMyCommunities(User user) {
        List<Fan> fans = fanRepository.findByUser(user).stream().sorted(Comparator.comparing(fan -> fan.getCommunity().getTeam() != null ? 0 : 1)).toList();

        return fans.stream().map((fan -> {
            List<ChatRoom> chatRoom = chatRoomRepository.findOfficialByCommunity(fan.getCommunity());
            return new CommunityResponse.CommunityDTO(fan.getCommunity(), null, fan, null, null, null, user.getCommunity() != null ? user.getCommunity().getId().equals(fan.getCommunity().getId()) : null, chatRoom.get(0).getId());
        })).toList();
    }

    public void registerCommunity(Long teamId, CommunityRequest.RegisterCommunityDTO requestDTO) {
        Team team = teamRepository.findById(teamId).orElseThrow(()->new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Community community = Community.builder()
                .type(CommunityType.PLAYER)
                .koreanName(requestDTO.koreanName())
                .englishName(requestDTO.englishName())
                .image(requestDTO.image())
                .backgroundImage(requestDTO.backgroundImage())
                .build();

        communityRepository.save(community);

        TeamPlayer teamPlayer = TeamPlayer.builder()
                .community(community)
                .team(team)
                .build();

        teamPlayerRepository.save(teamPlayer);

        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoomType.OFFICIAL)
                .name(requestDTO.koreanName())
                .image(requestDTO.image())
                .description(community.getKoreanName() + " 팬들끼리 응원해요!")
                .community(community)
                .build();

        chatRoomRepository.save(chatRoom);
    }

    public void createTeamCommunities() {
        List<Team> teams = teamRepository.findAll();

        for(Team team : teams) {
            Optional<Community> teamCommunity = communityRepository.findByTeam(team);
            if(teamCommunity.isEmpty()){
                Community community = Community.builder()
                        .type(CommunityType.TEAM)
                        .koreanName(team.getFirstName() + " " + team.getSecondName())
                        .image(team.getImage())
                        .team(team)
                        .build();

                communityRepository.save(community);

                ChatRoom chatRoom = ChatRoom.builder()
                        .type(ChatRoomType.OFFICIAL)
                        .name(community.getKoreanName())
                        .name(team.getImage())
                        .description(community.getKoreanName() + " 팬들끼리 응원해요!")
                        .community(community)
                        .build();

                chatRoomRepository.save(chatRoom);
            }
        }
    }
}
