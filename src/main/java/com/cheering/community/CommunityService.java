package com.cheering.community;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.chatRoom.ChatRoomResponse;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final BadWordService badWordService;
    private final S3Util s3Util;

    // 커뮤니티 조회
    public CommunityResponse.CommunityDTO getCommunityById(Long communityId, User user) {
        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        Long fanCount = fanRepository.countByCommunityId(communityId);
        Optional<Fan> fan = fanRepository.findByCommunityIdAndUser(communityId, user);

        if(team.isPresent()) {
            return new CommunityResponse.CommunityDTO(team.get(), fanCount, fan.map(FanResponse.FanDTO::new).orElse(null));
        }
        else if(player.isPresent()) {
            return new CommunityResponse.CommunityDTO(player.get(), fanCount, fan.map(FanResponse.FanDTO::new).orElse(null));
        }
        return null;
    }

    // 커뮤니티 검색
    public List<CommunityResponse.CommunitySectionDTO> getCommunities(String name, Long teamId, User user) {

        name = name.replace(" ", "");

        List<Team> teams = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        if(teamId != null && name.isEmpty()) {
            Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
            players = teamPlayerRepository.findByTeam(team);
        } else if(teamId != null) {
            players = playerRepository.findByNameAndTeamId(name, teamId);
        } else if(!name.isEmpty()) {
            teams = teamRepository.findByName(name);
            players = playerRepository.findByNameOrTeamName(name);
        }

        List<CommunityResponse.CommunityDTO> teamDTOS = teams.stream().map((team) -> {
            Long fanCount = fanRepository.countByCommunityId(team.getId());
            Optional<Fan> fan = fanRepository.findByCommunityIdAndUser(team.getId(), user);
            return new CommunityResponse.CommunityDTO(team, fanCount, fan.map(FanResponse.FanDTO::new).orElse(null));
        }).toList();

        List<CommunityResponse.CommunityDTO> playerDTOS = players.stream().map((player) -> {
            Long fanCount = fanRepository.countByCommunityId(player.getId());
            Optional<Fan> fan = fanRepository.findByCommunityIdAndUser(player.getId(), user);
            return new CommunityResponse.CommunityDTO(player, fanCount, fan.map(FanResponse.FanDTO::new).orElse(null));
        }).toList();

        return List.of(new CommunityResponse.CommunitySectionDTO("teams", teamDTOS),
                new CommunityResponse.CommunitySectionDTO("players", playerDTOS));
    }
    // 커뮤니티 가입
    @Transactional
    public void joinCommunity(Long communityId, String name, User user) {
        Optional<Player> player = playerRepository.findById(communityId);
        Optional<Team> team = teamRepository.findById(communityId);

        Optional<Fan> duplicatePlayerUser = fanRepository.findByCommunityIdAndName(communityId, name);

        if(duplicatePlayerUser.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NAME);
        }

        if(badWordService.containsBadWords(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Integer count = fanRepository.countByUser(user);

        if(team.isPresent()) {
            if(team.get().getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }

            String imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/profile-image.jpg";

            Fan fan = Fan.builder()
                    .type(CommunityType.TEAM)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityId)
                    .communityOrder(count + 1)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
        if(player.isPresent()) {
            if(player.get().getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }

            String imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/profile-image.jpg";

            Fan fan = Fan.builder()
                    .type(CommunityType.PLAYER)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityId)
                    .communityOrder(count + 1)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
    }

    // 내가 가입한 커뮤니티 조회
    public List<CommunityResponse.CommunityDTO> getMyCommunities(User user) {
        List<Fan> fans = fanRepository.findByUserOrderByCommunityOrderAsc(user);

        return fans.stream().map((fan -> {
            Long fanCount = fanRepository.countByCommunityId(fan.getCommunityId());
            ChatRoom officialChatRoom = chatRoomRepository.findOfficialByCommunityId(fan.getCommunityId());
            if(fan.getType().equals(CommunityType.TEAM)) {
                Team team = teamRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
                return new CommunityResponse.CommunityDTO(team, fanCount, new FanResponse.FanDTO(fan), officialChatRoom.getId());
            } else {
                Player player = playerRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
                return new CommunityResponse.CommunityDTO(player, fanCount, new FanResponse.FanDTO(fan), officialChatRoom.getId());
            }
        })).toList();
    }

    @Transactional
    public void changeCommunityOrder(List<CommunityRequest.ChangeOrderRequest> changeOrderRequests, User user) {
        for(CommunityRequest.ChangeOrderRequest changeOrderRequest: changeOrderRequests) {
            Fan fan = fanRepository.findByCommunityIdAndUser(changeOrderRequest.communityId(), user).orElseThrow(()-> new CustomException(ExceptionCode.FAN_NOT_FOUND));

            fan.setCommunityOrder(changeOrderRequest.communityOrder());
            fanRepository.save(fan);
        }
    }

    public void joinCommunities(CommunityRequest.JoinCommunitiesRequest requestDTO, User user) {
        List<Long> communityIds = requestDTO.communityIds();

        for(int i=0; i < communityIds.size(); i++){
            String name;
            String imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/profile-image.jpg";

            do {
                name = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            } while (fanRepository.existsByName(name));

            Fan fan = Fan.builder()
                    .type(CommunityType.TEAM)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityIds.get(i))
                    .communityOrder(i+1)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
    }

    public CommunityResponse.CommunityDTO getRandomCommunity() {
        long teamCount = teamRepository.count();
        long playerCount = playerRepository.count();
        long totalCount = teamCount + playerCount;

        Random random = new Random();
        long randomNumber = random.nextLong(totalCount);

        if (randomNumber < teamCount) {
            Team team = teamRepository.findRandomTeam();
            return new CommunityResponse.CommunityDTO(team, null, null);
        } else {
            Player player = playerRepository.findRandomPlayer();
            return new CommunityResponse.CommunityDTO(player, null, null);
        }
    }
}
