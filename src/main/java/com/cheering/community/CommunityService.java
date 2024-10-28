package com.cheering.community;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final TeamPlayerRepository teamPlayerRepository;
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

    // 특정 팀 소속 커뮤니티 목록 조회
    @Transactional
    public List<CommunityResponse.CommunityDTO> getCommunitiesByTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        List<Player> players = teamPlayerRepository.findByTeam(team);

        return players.stream().map((player)-> {
            long fanCount = fanRepository.countByCommunityId(player.getId());
            Optional<Fan> fan = fanRepository.findByCommunityIdAndUser(player.getId(), user);

            return new CommunityResponse.CommunityDTO(player, fanCount, fan.map(FanResponse.FanDTO::new).orElse(null));
        }).toList();
    }

    // 커뮤니티 검색
    public List<CommunityResponse.CommunityDTO> getCommunities(String name, User user) {
        name = name.replace(" ", "");

        List<Team> teams = teamRepository.findByName(name);
        List<Player> players = playerRepository.findByNameOrTeamName(name);

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

        List<CommunityResponse.CommunityDTO> communityDTOS = new ArrayList<>(teamDTOS);
        communityDTOS.addAll(playerDTOS);

        return communityDTOS;
    }
    // 커뮤니티 가입
    @Transactional
    public void joinCommunity(Long communityId, String name, MultipartFile image, User user) {
        Optional<Player> player = playerRepository.findById(communityId);
        Optional<Team> team = teamRepository.findById(communityId);

        Optional<Fan> duplicatePlayerUser = fanRepository.findByCommunityIdAndName(communityId, name);

        if(duplicatePlayerUser.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NAME);
        }

        if(badWordService.containsBadWords(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        if(team.isPresent()) {
            if(team.get().getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }

            String imageUrl = "";
            if(image == null) {
                imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-profile.jpg";
            } else {
                imageUrl = s3Util.upload(image);
            }

            Fan fan = Fan.builder()
                    .type(CommunityType.TEAM)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityId)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
        if(player.isPresent()) {
            if(player.get().getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }

            String imageUrl = "";
            if(image == null) {
                imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-profile.jpg";
            } else {
                imageUrl = s3Util.upload(image);
            }

            Fan fan = Fan.builder()
                    .type(CommunityType.PLAYER)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityId)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
    }
}
