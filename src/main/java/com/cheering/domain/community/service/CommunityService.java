package com.cheering.domain.community.service;

import com.cheering.domain.community.constant.BooleanType;
import com.cheering.domain.community.constant.Category;
import com.cheering.domain.community.constant.CommunityType;
import com.cheering.domain.community.constant.League;
import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.community.dto.response.CommunityResponse;
import com.cheering.domain.community.dto.response.FoundCommunitiesResponse;
import com.cheering.domain.community.dto.response.UserCommunityInfoResponse;
import com.cheering.domain.community.repository.CommunityRepository;
import com.cheering.domain.community.repository.UserCommunityInfoRepository;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.post.domain.PostInfo;
import com.cheering.domain.post.repository.PostInfoRepository;
import com.cheering.domain.post.repository.PostRepository;
import com.cheering.domain.user.domain.Role;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.repository.TeamRepository;
import com.cheering.domain.user.repository.UserRepository;
import com.cheering.global.exception.community.DuplicatedCommunityJoinException;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.NotFoundUserException;
import com.cheering.global.util.AwsS3Util;
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

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;
    private final PostRepository postRepository;
    private final PostInfoRepository postInfoRepository;
    private final AwsS3Util awsS3Util;

    public List<FoundCommunitiesResponse> findCommunitiesByName(String name) {
        List<Community> communities = communityRepository.findByNameContainingIgnoreCase(name);

        List<Community> playerCommunities = communities.stream()
                .filter(com -> com.getCType().equals(CommunityType.PLAYER_COMMUNITY))
                .toList();

        User loginUser = getLoginUser();
        List<UserCommunityInfo> userCommunities = userCommunityInfoRepository.findByUser(loginUser);
        List<Long> joinCommunityIds = userCommunities.stream().map(com -> com.getCommunity().getId()).toList();

        List<FoundCommunitiesResponse> foundPlayerCommunitiesResponses = generatePlayerCommunityResponse(
                playerCommunities, joinCommunityIds);

        List<FoundCommunitiesResponse> responseResult = new ArrayList<>(foundPlayerCommunitiesResponses);

        List<Community> teamCommunities = communities.stream()
                .filter(com -> com.getCType().equals(CommunityType.TEAM_COMMUNITY))
                .toList();

        if (!teamCommunities.isEmpty()) {
            List<FoundCommunitiesResponse> foundTeamCommunitiesResponses = generateTeamCommunityResponse(
                    teamCommunities, joinCommunityIds);

            responseResult.addAll(foundTeamCommunitiesResponses);
        }

        return responseResult;
    }

    private List<FoundCommunitiesResponse> generateTeamCommunityResponse(List<Community> teamCommunities,
                                                                         List<Long> joinCommunityIds) {
        List<FoundCommunitiesResponse> result = new ArrayList<>();
        for (Community teamCommunity : teamCommunities) {
            List<User> players = teamCommunity.getTeam().getPlayers();
            List<Community> playerCommunities = getPlayerCommunitiesByPlayers(players);

            List<CommunityResponse> communityResponse = CommunityResponse.ofList(playerCommunities, joinCommunityIds);

            FoundCommunitiesResponse foundCommunitiesResponse;

            if (joinCommunityIds.contains(teamCommunity.getId())) {
                foundCommunitiesResponse = FoundCommunitiesResponse.of(communityResponse,
                        teamCommunity, BooleanType.TRUE);
            } else {
                foundCommunitiesResponse = FoundCommunitiesResponse.of(communityResponse,
                        teamCommunity, BooleanType.FALSE);
            }

            result.add(foundCommunitiesResponse);
        }

        return result;
    }

    private List<FoundCommunitiesResponse> generatePlayerCommunityResponse(List<Community> playerCommunities,
                                                                           List<Long> joinCommunityIds) {
        List<FoundCommunitiesResponse> result = new ArrayList<>();

        for (Community community : playerCommunities) {
            if (joinCommunityIds.contains(community.getId())) {
                CommunityResponse communityResponse = new CommunityResponse(community.getId(), community.getName(),
                        community.getImage(),
                        community.getFanCount(),
                        BooleanType.TRUE);

                FoundCommunitiesResponse foundCommunitiesResponse;

                if (joinCommunityIds.contains(community.getUser().getTeam().getTeamCommunity().getId())) {
                    foundCommunitiesResponse = FoundCommunitiesResponse.of(
                            List.of(communityResponse), community.getUser().getTeam().getTeamCommunity(),
                            BooleanType.TRUE);
                } else {
                    foundCommunitiesResponse = FoundCommunitiesResponse.of(
                            List.of(communityResponse), community.getUser().getTeam().getTeamCommunity(),
                            BooleanType.FALSE);
                }

                result.add(foundCommunitiesResponse);
            } else {
                CommunityResponse communityResponse = new CommunityResponse(community.getId(), community.getName(),
                        community.getImage(),
                        community.getFanCount(),
                        BooleanType.FALSE);

                FoundCommunitiesResponse foundCommunitiesResponse;

                if (joinCommunityIds.contains(community.getUser().getTeam().getTeamCommunity().getId())) {
                    foundCommunitiesResponse = FoundCommunitiesResponse.of(
                            List.of(communityResponse), community.getUser().getTeam().getTeamCommunity(),
                            BooleanType.TRUE);
                } else {
                    foundCommunitiesResponse = FoundCommunitiesResponse.of(
                            List.of(communityResponse), community.getUser().getTeam().getTeamCommunity(),
                            BooleanType.FALSE);
                }

                result.add(foundCommunitiesResponse);
            }
        }

        return result;
    }

    private User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userRepository.findById(Long.valueOf(loginId))
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }

    @Transactional
    public UserCommunityInfoResponse joinCommunity(Long communityId, String nickname, MultipartFile file) {
        User user = getLoginUser();

        Community community = communityRepository.findById(communityId)
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

    private static FoundCommunitiesResponse generateFoundCommunitiesResponse(Community community, BooleanType isJoin) {
        CommunityResponse communityResponse = CommunityResponse.of(community, isJoin);
        Community teamCommunity = community.getUser().getTeam().getTeamCommunity();

        return FoundCommunitiesResponse.of(
                List.of(communityResponse),
                teamCommunity,
                isJoin);
    }

    private List<Community> getPlayerCommunitiesByPlayers(List<User> players) {
        return players.stream().map(User::getCommunity).toList();
    }

    @Transactional
    public void setData() {
        URL teamTottenhanImageUrl = awsS3Util.getPath("community/team-profile/team_tottenhan_image.png");

        URL teamPSGImageUrl = awsS3Util.getPath("community/team-profile/team_PSG_image.jpeg");

        URL playerLeeImageUrl = awsS3Util.getPath("community/player-profile/player_leeKangIn.jpg");

        URL playerSonImageUrl = awsS3Util.getPath("community/player-profile/player_SonHeungMin.png");

        URL userImageUrl = awsS3Util.getPath("community/user-community-info-profile/user_img.avif");

        Community psgCommunity = Community.builder()
                .name("파리 생제르맹")
                .category(Category.SOCCER)
                .league(League.FRENCH_LEAGUE1)
                .image(teamPSGImageUrl)
                .cType(CommunityType.TEAM_COMMUNITY)
                .fanCount(3000L)
                .build();

        Community tottenhamCommunity = Community.builder()
                .name("토트넘")
                .category(Category.SOCCER)
                .league(League.EPL)
                .image(teamTottenhanImageUrl)
                .cType(CommunityType.TEAM_COMMUNITY)
                .fanCount(4000L)
                .build();

        communityRepository.save(psgCommunity);
        communityRepository.save(tottenhamCommunity);

        Team teamPSG = Team.builder().players(new ArrayList<>()).role(Role.ROLE_TEAM).teamCommunity(psgCommunity)
                .build();
        Team teamTottenham = Team.builder().players(new ArrayList<>()).role(Role.ROLE_TEAM)
                .teamCommunity(tottenhamCommunity).build();

        teamRepository.save(teamPSG);
        teamRepository.save(teamTottenham);

        Community community1 = Community.builder()
                .cType(CommunityType.PLAYER_COMMUNITY)
                .name("이강인")
                .fanCount(1L).image(playerLeeImageUrl).build();
        Community community2 = Community.builder()
                .cType(CommunityType.PLAYER_COMMUNITY)
                .name("음바페")
                .fanCount(2L).image(userImageUrl).build();
        Community community3 = Community.builder()
                .cType(CommunityType.PLAYER_COMMUNITY)
                .name("아센시오")
                .fanCount(3L).image(userImageUrl).build();

        Community community4 = Community.builder()
                .name("손흥민")
                .fanCount(4L)
                .image(playerSonImageUrl)
                .cType(CommunityType.PLAYER_COMMUNITY)
                .build();
        Community community5 = Community.builder()
                .cType(CommunityType.PLAYER_COMMUNITY)
                .name("히샬리송")
                .fanCount(5L).image(userImageUrl).build();
        Community community6 = Community.builder()
                .cType(CommunityType.PLAYER_COMMUNITY)
                .name("메디슨")
                .fanCount(6L).image(userImageUrl).build();

        communityRepository.save(community1);
        communityRepository.save(community2);
        communityRepository.save(community3);
        communityRepository.save(community4);
        communityRepository.save(community5);
        communityRepository.save(community6);

        User playerA1 = User.builder()
                .community(community1)
                .name("이강인")
                .profileImage(playerLeeImageUrl)
                .role(Role.ROLE_PLAYER).build();
        User playerA2 = User.builder().community(community2)
                .profileImage(userImageUrl).name("음바페").role(Role.ROLE_PLAYER).build();
        User playerA3 = User.builder().community(community3).profileImage(userImageUrl).name("아센시오")
                .role(Role.ROLE_PLAYER).build();

        playerA1.connectTeam(teamPSG);
        playerA2.connectTeam(teamPSG);
        playerA3.connectTeam(teamPSG);

        userRepository.save(playerA1);
        userRepository.save(playerA2);
        userRepository.save(playerA3);

        User playerB1 = User.builder().community(community4).name("손흥민").profileImage(playerSonImageUrl)
                .role(Role.ROLE_PLAYER).build();
        User playerB2 = User.builder().community(community5).profileImage(userImageUrl).name("히샬리송")
                .role(Role.ROLE_PLAYER).build();
        User playerB3 = User.builder().community(community6).profileImage(userImageUrl).name("메디슨")
                .role(Role.ROLE_PLAYER).build();

        playerB1.connectTeam(teamTottenham);
        playerB2.connectTeam(teamTottenham);
        playerB3.connectTeam(teamTottenham);

        userRepository.save(playerB1);
        userRepository.save(playerB2);
        userRepository.save(playerB3);

        User fan1 = User.builder()
                .nickname("이강인 팬")
                .role(Role.ROLE_USER)
                .profileImage(userImageUrl)
                .build();

        User fan2 = User.builder()
                .nickname("손흥민 팬")
                .role(Role.ROLE_USER)
                .profileImage(userImageUrl)
                .build();

        userRepository.save(fan1);
        userRepository.save(fan2);

        PostInfo fanPostInfo1 = PostInfo.builder().writerName(fan1.getNickname())
                .image(userImageUrl).build();
        PostInfo fanPostInfo2 = PostInfo.builder().writerName(fan2.getNickname())
                .image(userImageUrl).build();
        PostInfo fanPostInfo3 = PostInfo.builder().writerName(fan1.getNickname())
                .image(userImageUrl).build();
        PostInfo fanPostInfo4 = PostInfo.builder().writerName(playerA1.getNickname())
                .image(userImageUrl).build();
        PostInfo fanPostInfo5 = PostInfo.builder().writerName(playerA1.getNickname())
                .image(userImageUrl).build();

        postInfoRepository.save(fanPostInfo1);
        postInfoRepository.save(fanPostInfo2);
        postInfoRepository.save(fanPostInfo3);
        postInfoRepository.save(fanPostInfo4);
        postInfoRepository.save(fanPostInfo5);

        Post fanPost1 = Post.builder().community(community1)
                .content("팬 -> 이강인 커뮤니티1")
                .owner(fan1)
                .postInfo(fanPostInfo1)
                .build();

        Post fanPost2 = Post.builder().community(community1)
                .content("팬 -> 이강인 커뮤니티2")
                .owner(fan2)
                .postInfo(fanPostInfo2)
                .build();

        Post fanPost3 = Post.builder().community(psgCommunity)
                .content("팬 -> PSG 팀 커뮤니티")
                .owner(fan1)
                .postInfo(fanPostInfo3)
                .build();

        Post playerPost1 = Post.builder().community(community1)
                .content("아시안 컵 쉽네 ㅋ")
                .owner(playerA1)
                .postInfo(fanPostInfo4)
                .build();

        Post playerPost2 = Post.builder().community(psgCommunity)
                .content("선수 -> PSG 팀 커뮤니티")
                .owner(playerA1)
                .postInfo(fanPostInfo5)
                .build();

        Post teamPost = Post.builder().community(psgCommunity)
                .content("PSG 팀 게시글 입니다.")
                .team(teamPSG)
                .build();

        postRepository.save(fanPost1);
        postRepository.save(fanPost2);
        postRepository.save(fanPost3);
        postRepository.save(playerPost1);
        postRepository.save(playerPost2);
        postRepository.save(teamPost);
    }
}
