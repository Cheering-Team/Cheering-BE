package com.cheering.community.service;

import com.cheering.community.constant.Category;
import com.cheering.community.constant.League;
import com.cheering.community.domain.Community;
import com.cheering.community.domain.UserCommunityInfo;
import com.cheering.community.domain.repository.PlayerCommunityRepository;
import com.cheering.community.domain.repository.TeamCommunityRepository;
import com.cheering.community.domain.repository.UserCommunityInfoRepository;
import com.cheering.community.dto.response.CommunityResponse;
import com.cheering.community.dto.response.FoundCommunitiesResponse;
import com.cheering.community.dto.response.UserCommunityInfoResponse;
import com.cheering.global.exception.community.DuplicatedCommunityJoinException;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.NotFoundUserException;
import com.cheering.global.util.AwsS3Util;
import com.cheering.user.domain.Player;
import com.cheering.user.domain.User;
import com.cheering.user.domain.repository.PlayerRepository;
import com.cheering.user.domain.repository.UserRepository;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserRepository userRepository;
    private final PlayerCommunityRepository playerCommunityRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;
    private final TeamCommunityRepository teamCommunityRepository;
    private final PlayerRepository playerRepository;
    private final AwsS3Util awsS3Util;

    public List<FoundCommunitiesResponse> findCommunitiesByName(String name) {
        List<Community> playerCommunities = playerCommunityRepository.findByNameContainingIgnoreCase(name);
        TeamCommunity teamCommunity = teamCommunityRepository.findByNameContainingIgnoreCase(name);

        List<FoundCommunitiesResponse> foundCommunitiesResponses = generatePlayerCommunityResponse(playerCommunities);

        List<FoundCommunitiesResponse> responseResult = new ArrayList<>(foundCommunitiesResponses);

        if (teamCommunity != null) {
            FoundCommunitiesResponse foundCommunitiesResponse = generateTeamCommunityResponse(teamCommunity);
            responseResult.add(foundCommunitiesResponse);
        }

        return responseResult;
    }

    @Transactional
    public UserCommunityInfoResponse joinCommunity(Long communityId, String nickname, MultipartFile file) {
        Authentication loginUser = SecurityContextHolder.getContext().getAuthentication();
        String loginUserId = loginUser.getName();

        User user = userRepository.findById(Long.valueOf(loginUserId)).orElseThrow(() ->
                new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));

        Community community = playerCommunityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        validateDuplicateJoinCommunity(user, community);

        try {
            String category = "community/user-community-info-profile";
            URL url = awsS3Util.uploadFile(file, category);
            UserCommunityInfo communityUser = UserCommunityInfo.builder()
                    .nickname(nickname)
                    .community(community)
                    .profileImage(url)
                    .user(user)
                    .build();

            UserCommunityInfo savedCommunityUser = userCommunityInfoRepository.save(communityUser);
            return new UserCommunityInfoResponse(savedCommunityUser.getId());
        } catch (IOException e) {
            UserCommunityInfo communityUser = UserCommunityInfo.builder()
                    .nickname(nickname)
                    .community(community)
                    .user(user)
                    .build();

            UserCommunityInfo savedCommunityUser = userCommunityInfoRepository.save(communityUser);
            return new UserCommunityInfoResponse(savedCommunityUser.getId());
        }
    }

    private void validateDuplicateJoinCommunity(User user, Community community) {
        if (userCommunityInfoRepository.existsByUserAndCommunity(user, community)) {
            throw new DuplicatedCommunityJoinException(ExceptionMessage.DUPLICATED_JOIN_COMMUNITY);
        }
    }

    private FoundCommunitiesResponse generateTeamCommunityResponse(TeamCommunity teamCommunity) {
        List<Player> players = teamCommunity.getPlayers();
        List<Community> playerCommunities = getPlayerCommunitiesByPlayers(players);

        List<CommunityResponse> communityResponse = CommunityResponse.ofList(playerCommunities);
        return FoundCommunitiesResponse.of(communityResponse, teamCommunity);
    }

    private List<FoundCommunitiesResponse> generatePlayerCommunityResponse(List<Community> playerCommunities) {
        List<FoundCommunitiesResponse> result = new ArrayList<>();

        for (Community community : playerCommunities) {
            CommunityResponse communityResponse = CommunityResponse.of(community);
            TeamCommunity teamCommunity = community.getPlayer().getTeamCommunity();

            FoundCommunitiesResponse foundCommunitiesResponse = FoundCommunitiesResponse.of(List.of(communityResponse),
                    teamCommunity);

            result.add(foundCommunitiesResponse);
        }

        return result;
    }

    private List<Community> getPlayerCommunitiesByPlayers(List<Player> players) {
        return players.stream().map(Player::getCommunity).toList();
    }

    @Transactional
    public void setData() {
        String imageUrl = awsS3Util.getPath(
                "community/user-community-info-profile/0d5211b8-6ee0-4d04-a310-ed1df5dcd89e.png");
        Community community1 = Community.builder().name("이강인")
                .fanCount(1L).image(imageUrl).build();
        Community community2 = Community.builder().name("음바페")
                .fanCount(2L).image(imageUrl).build();
        Community community3 = Community.builder().name("아센시오")
                .fanCount(3L).image(imageUrl).build();

        Community community4 = Community.builder().name("손흥민")
                .fanCount(4L).image(imageUrl).build();
        Community community5 = Community.builder().name("히샬리송")
                .fanCount(5L).image(imageUrl).build();
        Community community6 = Community.builder().name("메디슨")
                .fanCount(6L).image(imageUrl).build();

        playerCommunityRepository.save(community1);
        playerCommunityRepository.save(community2);
        playerCommunityRepository.save(community3);
        playerCommunityRepository.save(community4);
        playerCommunityRepository.save(community5);
        playerCommunityRepository.save(community6);

        TeamCommunity psgCommunity = TeamCommunity.builder()
                .name("파리 생제르맹")
                .players(new ArrayList<>())
                .category(Category.SOCCER)
                .league(League.FRENCH_LEAGUE1)
                .image(imageUrl)
                .build();

        TeamCommunity tottenhamCommunity = TeamCommunity.builder()
                .name("토트넘")
                .players(new ArrayList<>())
                .category(Category.SOCCER)
                .league(League.EPL)
                .image(imageUrl)
                .build();

        teamCommunityRepository.save(psgCommunity);
        teamCommunityRepository.save(tottenhamCommunity);

        Player playerA1 = Player.builder().community(community1).name("이강인").build();
        Player playerA2 = Player.builder().community(community2).name("음바페").build();
        Player playerA3 = Player.builder().community(community3).name("아센시오").build();

        playerA1.connectTeamCommunity(psgCommunity);
        playerA2.connectTeamCommunity(psgCommunity);
        playerA3.connectTeamCommunity(psgCommunity);

        playerRepository.save(playerA1);
        playerRepository.save(playerA2);
        playerRepository.save(playerA3);

        Player playerB1 = Player.builder().community(community4).name("손흥민").build();
        Player playerB2 = Player.builder().community(community5).name("히샬리송").build();
        Player playerB3 = Player.builder().community(community6).name("메디슨").build();

        playerB1.connectTeamCommunity(tottenhamCommunity);
        playerB2.connectTeamCommunity(tottenhamCommunity);
        playerB3.connectTeamCommunity(tottenhamCommunity);

        playerRepository.save(playerB1);
        playerRepository.save(playerB2);
        playerRepository.save(playerB3);
    }
}
