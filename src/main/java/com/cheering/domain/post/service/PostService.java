package com.cheering.domain.post.service;

import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.community.repository.CommunityRepository;
import com.cheering.domain.community.repository.UserCommunityInfoRepository;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.post.domain.PostInfo;
import com.cheering.domain.post.dto.PostResponse;
import com.cheering.domain.post.repository.PostInfoRepository;
import com.cheering.domain.post.repository.PostRepository;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.dto.response.PostOwnerResponse;
import com.cheering.domain.user.repository.TeamRepository;
import com.cheering.domain.user.repository.UserRepository;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.community.NotFoundUserCommunityInfoException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.NotFoundTeamException;
import com.cheering.global.exception.user.NotFoundUserException;
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
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;
    private final PostInfoRepository postInfoRepository;

    @Transactional(readOnly = true)
    public List<PostResponse> getPlayerPosts(Long communityId, Long writerId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        User findPlayer = userRepository.findById(writerId)
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));

        List<Post> result = postRepository.findByCommunityAndOwner(findCommunity, findPlayer);

        PostOwnerResponse postOwnerResponse = PostOwnerResponse.builder()
                .id(findPlayer.getId())
                .name(findPlayer.getName())
                .build();

        return PostResponse.ofList(result, postOwnerResponse);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getUserPosts(Long communityId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        List<Post> findUserPosts = postRepository.findByCommunityAndOwnerIsNotNull(findCommunity);

        List<PostResponse> result = new ArrayList<>();
        for (Post findUserPost : findUserPosts) {
            PostOwnerResponse postOwnerResponse = PostOwnerResponse.of(findUserPost.getOwner().getId(),
                    findUserPost.getPostInfo().getWriterName());

            PostResponse postResponse = PostResponse.of(findUserPost, postOwnerResponse);

            result.add(postResponse);
        }

        return result;
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

    @Transactional
    public Long createPost(Long communityId, String content, List<MultipartFile> files) {
        User loginUser = getLoginUser();

        //todo: Community 찾기
        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        //todo: userCommunityInfo 에서 커뮤니티 id로 유저의 닉네임과 프로필 이미지 가져오기
        UserCommunityInfo findUserCommunityInfo = userCommunityInfoRepository.findByUserAndCommunity(loginUser,
                findCommunity).orElseThrow(
                () -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO)
        );

        //todo: PostInfo 객체 생성
        PostInfo newPostInfo = PostInfo.builder()
                .image(findUserCommunityInfo.getProfileImage())
                .writerName(findUserCommunityInfo.getNickname())
                .build();
        postInfoRepository.save(newPostInfo);

        //todo: Post 객체 생성
        Post newPost = Post.builder().postInfo(newPostInfo)
                .content(content)
                .owner(loginUser)
                .community(findCommunity)
                .owner(loginUser)
                .build();

        //todo: 객체 저장 및 생성된 id 반환
        postRepository.save(newPost);
        return newPost.getId();
    }

    private User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userRepository.findById(Long.valueOf(loginId))
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }
}
