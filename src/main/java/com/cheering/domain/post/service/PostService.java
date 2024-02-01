package com.cheering.domain.post.service;

import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.community.domain.repository.CommunityRepository;
import com.cheering.domain.community.domain.repository.UserCommunityInfoRepository;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.post.dto.PostResponse;
import com.cheering.domain.post.repository.PostRepository;
import com.cheering.domain.user.domain.Player;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.domain.repository.PlayerRepository;
import com.cheering.domain.user.domain.repository.TeamRepository;
import com.cheering.domain.user.domain.repository.UserRepository;
import com.cheering.domain.user.dto.response.PostOwnerResponse;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.community.NotFoundUserCommunityInfoException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.NotFoundTeamException;
import com.cheering.global.exception.user.NotFoundUserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;

    @Transactional(readOnly = true)
    public List<PostResponse> getPlayerPosts(Long communityId, Long writerId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        Player findPlayer = playerRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));

        List<Post> result = postRepository.findByCommunityAndPlayer(findCommunity, findPlayer);

        PostOwnerResponse postOwnerResponse = PostOwnerResponse.builder()
                .id(findPlayer.getId())
                .name(findPlayer.getName())
                .build();

        return PostResponse.ofList(result, postOwnerResponse);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getUserPosts(Long communityId, Long writerId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        User findUser = userRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));

        UserCommunityInfo userCommunityInfo =
                userCommunityInfoRepository.findByUserAndCommunity(findUser, findCommunity).orElseThrow(
                        () -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));

        List<Post> result = postRepository.findByCommunityAndUser(findCommunity, findUser);

        PostOwnerResponse postOwnerResponse = PostOwnerResponse.builder()
                .id(findUser.getId())
                .name(userCommunityInfo.getNickname())
                .build();

        return PostResponse.ofList(result, postOwnerResponse);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getTeamPosts(Long communityId, Long writerId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        Team findTeam = teamRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundTeamException(ExceptionMessage.NOT_FOUND_TEAM));

        List<Post> result = postRepository.findByCommunityAndTeam(findCommunity, findTeam);

        PostOwnerResponse postOwnerResponse = PostOwnerResponse.builder()
                .id(findTeam.getId())
                .name(findTeam.getTeamCommunity().getName())
                .build();

        return PostResponse.ofList(result, postOwnerResponse);
    }
}
