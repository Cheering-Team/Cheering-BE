package com.cheering.domain.post.service;

import com.cheering.domain.community.constant.BooleanType;
import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.community.repository.CommunityRepository;
import com.cheering.domain.community.repository.UserCommunityInfoRepository;
import com.cheering.domain.post.domain.ImageFile;
import com.cheering.domain.post.domain.Interesting;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.post.dto.PostResponse;
import com.cheering.domain.post.repository.ImageFileRepository;
import com.cheering.domain.post.repository.InterestingRepository;
import com.cheering.domain.post.repository.PostRepository;
import com.cheering.domain.user.domain.Team;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.dto.response.WriterResponse;
import com.cheering.domain.user.repository.UserRepository;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.community.NotFoundUserCommunityInfoException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.post.NotFoundPostException;
import com.cheering.global.exception.user.NotFoundUserException;
import com.cheering.global.util.AwsS3Util;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final UserRepository userRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;
    private final ImageFileRepository imageFileRepository;
    private final AwsS3Util awsS3Util;
    private final InterestingRepository interestingRepository;

    @Transactional(readOnly = true)
    public List<PostResponse> getPlayerPosts(Long communityId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        User findPlayer = findCommunity.getUser();
        
        UserCommunityInfo findWriterInfo = userCommunityInfoRepository.findByUserAndCommunity(findPlayer,
                        findCommunity)
                .orElseThrow(() -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));

        List<Post> result = postRepository.findByWriterInfoCommunityAndWriterInfoUser(findCommunity, findPlayer);

        WriterResponse writerResponse = WriterResponse.of(findPlayer.getId(), findWriterInfo.getNickname(),
                findWriterInfo.getProfileImage());

        return PostResponse.ofList(result, writerResponse);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getUserPosts(Long communityId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        List<Post> findAllUserPosts = postRepository.findByWriterInfoCommunityAndWriterInfoUserIsNotNull(findCommunity);

        List<Post> findFanPosts = findAllUserPosts.stream()
                .filter(post -> !post.getWriterInfo().getUser().equals(findCommunity.getUser())).toList();

        List<PostResponse> result = new ArrayList<>();
        for (Post fanPost : findFanPosts) {
            WriterResponse writerResponse = WriterResponse.of(fanPost.getWriterInfo().getUser().getId(),
                    fanPost.getWriterInfo().getNickname(), fanPost.getWriterInfo().getProfileImage());

            List<URL> imageUrls = fanPost.getFiles().stream().map(ImageFile::getPath).toList();

            PostResponse postResponse = PostResponse.of(fanPost, writerResponse, imageUrls);

            result.add(postResponse);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getTeamPosts(Long communityId) {
        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        Team findTeam = findCommunity.getTeam();
        List<Post> result = postRepository.findByWriterInfoCommunityAndTeam(findCommunity, findTeam);

        WriterResponse writerResponse = WriterResponse.builder()
                .id(findTeam.getId())
                .name(findTeam.getTeamCommunity().getName())
                .profileImage(findCommunity.getThumbnailImage())
                .build();

        return PostResponse.ofList(result, writerResponse);
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

        //todo: Post 객체 생성
        String category = "post";
        try {
            List<URL> imageUrls = awsS3Util.uploadFiles(files, category);

            Post newPost = Post.builder()
                    .writerInfo(findUserCommunityInfo)
                    .content(content)
                    .build();

            List<ImageFile> imageFiles = imageUrls.stream()
                    .map(url -> ImageFile.builder()
                            .post(newPost)
                            .path(url)
                            .build())
                    .toList();

            imageFileRepository.saveAll(imageFiles);

            //todo: 객체 저장 및 생성된 id 반환
            postRepository.save(newPost);
            return newPost.getId();
        } catch (IOException e) {
            Post newPost = Post.builder()
                    .writerInfo(findUserCommunityInfo)
                    .content(content)
                    .build();

            //todo: 객체 저장 및 생성된 id 반환
            postRepository.save(newPost);
            return newPost.getId();
        }
    }

    private User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userRepository.findById(Long.valueOf(loginId))
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }

    public PostResponse detailPost(Long communityId, Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));

        WriterResponse writerResponse = WriterResponse.of(findPost.getWriterInfo().getUser().getId(),
                findPost.getWriterInfo().getNickname(), findPost.getWriterInfo().getProfileImage());

        List<URL> imageUrls = findPost.getFiles().stream().map(ImageFile::getPath).toList();
        return PostResponse.of(findPost, writerResponse, imageUrls);
    }

    @Transactional
    public void toggleInteresting(Long communityId, Long postId) {
        User loginUser = getLoginUser();

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));

        Optional<Interesting> findInteresting = interestingRepository.findByUserAndPost(loginUser, findPost);
        findInteresting.ifPresentOrElse(Interesting::changeStatus,
                () -> {
                    Interesting newInteresting = Interesting.builder()
                            .post(findPost)
                            .user(loginUser)
                            .status(BooleanType.TRUE)
                            .build();

                    interestingRepository.save(newInteresting);
                }
        );
    }
}
