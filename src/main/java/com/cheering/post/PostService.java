package com.cheering.post;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostImage.PostImageResponse;
import com.cheering.post.Tag.Tag;
import com.cheering.post.Tag.TagRepository;
import com.cheering.post.relation.PostTag;
import com.cheering.post.relation.PostTagRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PlayerUserRepository playerUserRepository;
    private final PostImageRepository postImageRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final S3Util s3Util;

    @Transactional
    public PostResponse.PostIdDTO writePost(Long playerId, String content, List<MultipartFile> images, List<String> tags, User user) {
        PlayerUser playerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Post post = Post.builder()
                .content(content)
                .playerUser(playerUser)
                .build();

        postRepository.save(post);

        if(tags != null) {
            tags.forEach((tagName) -> {
                Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new CustomException(ExceptionCode.TAG_NOT_FOUND));

                PostTag postTag = PostTag.builder()
                        .post(post)
                        .tag(tag)
                        .build();

                postTagRepository.save(postTag);
            });
        }

        if(images != null ){
            images.forEach((image)->{
                try {
                    String imageUrl = s3Util.upload(image);
                    BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();

                    PostImage postImage = PostImage.builder()
                            .path(imageUrl)
                            .width(width)
                            .height(height)
                            .post(post)
                            .build();

                    postImageRepository.save(postImage);
                } catch (IOException e) {
                    throw new CustomException(ExceptionCode.IMAGE_UPLOAD_FAILED);
                }
            });
        }
        return new PostResponse.PostIdDTO(post.getId());
    }

    public PostResponse.PostByIdDTO getPostById(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser playerUser = post.getPlayerUser();

        User writer = playerUser.getUser();

        Player player = playerUser.getPlayer();

        List<PostTag> postTags = postTagRepository.findByPostId(postId);

        List<String> tags = postTags.stream().map((postTag) -> {
            Tag tag = tagRepository.findById(postTag.getTag().getId()).orElseThrow(()-> new CustomException(ExceptionCode.TAG_NOT_FOUND));

            return tag.getName();
        }).toList();

        List<PostImage> postImages = postImageRepository.findByPostId(postId);

        List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

        PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(playerUser);

        PostResponse.PostInfoDTO postInfoDTO = new PostResponse.PostInfoDTO(user.getId().equals(writer.getId()), post.getContent(), post.getCreatedAt(), tags, imageDTOS, writerDTO);
        PlayerResponse.PlayerNameDTO playerNameDTO = new PlayerResponse.PlayerNameDTO(player);

        return new PostResponse.PostByIdDTO(postInfoDTO, playerNameDTO);
    }

//    private final PostRepository postRepository;
//    private final CommunityRepository communityRepository;
//    private final UserRepository userRepository;
//    private final UserCommunityInfoRepository userCommunityInfoRepository;
//    private final ImageFileRepository imageFileRepository;
//    private final AwsS3Util awsS3Util;
//    private final InterestingRepository interestingRepository;

}
//
//    @Transactional(readOnly = true)
//    public List<PostResponse> getPlayerPosts(Long communityId) {
//        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
//                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));
//
//        User findPlayer = findCommunity.getUser();
//
//        UserCommunityInfo findWriterInfo = userCommunityInfoRepository.findByUserAndCommunity(findPlayer, findCommunity)
//                .orElseThrow(() -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));
//
//        List<Post> findPosts = postRepository.findByWriterInfoCommunityAndWriterInfoUser(findCommunity, findPlayer);
//
//        User loginUser = getLoginUser();
//
//        List<Interesting> interestings = getInterestingByPostAndUser(findPosts, loginUser);
//
//        WriterResponse writerResponse = WriterResponse.of(findPlayer.getId(), findWriterInfo.getNickname(),
//                findWriterInfo.getProfileImage());
//
//        return PostResponse.ofList(findPosts, interestings, writerResponse);
//    }
//
//    @Transactional(readOnly = true)
//    public List<PostResponse> getUserPosts(Long communityId) {
//        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
//                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));
//
//        List<Post> findAllUserPosts = postRepository.findByWriterInfoCommunityAndWriterInfoUserIsNotNull(findCommunity);
//
//        List<Post> findFanPosts = findAllUserPosts.stream()
//                .filter(post -> !post.getWriterInfo().getUser().equals(findCommunity.getUser())).toList();
//
//        User loginUser = getLoginUser();
//        List<Interesting> interestings = getInterestingByPostAndUser(findFanPosts, loginUser);
//
//        List<PostResponse> result = new ArrayList<>();
//
//        for (Post fanPost : findFanPosts) {
//            WriterResponse writerResponse = WriterResponse.of(fanPost.getWriterInfo().getUser().getId(),
//                    fanPost.getWriterInfo().getNickname(), fanPost.getWriterInfo().getProfileImage());
//
//            List<ImageFile> imageFiles = fanPost.getFiles();
//            List<ImageFileInfo> imageFileInfos = ImageFileInfo.ofList(imageFiles);
//            Interesting likeStatus = interestings.stream()
//                    .filter(interesting -> interesting.getPost().equals(fanPost))
//                    .findFirst().orElseThrow(RuntimeException::new);
//
//            PostResponse postResponse = PostResponse.of(fanPost, likeStatus.getStatus(), writerResponse,
//                    imageFileInfos);
//
//            result.add(postResponse);
//        }
//
//        return result;
//    }
//
//    @Transactional(readOnly = true)
//    public List<PostResponse> getTeamPosts(Long communityId) {
//        Community findCommunity = communityRepository.findById(communityId).orElseThrow(() ->
//                new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));
//
//        if (findCommunity.getTeam() == null) {
//            return new ArrayList<>();
//        }
//
//        Team findTeam = findCommunity.getTeam();
//        List<Post> result = postRepository.findByWriterInfoCommunityAndTeam(findCommunity, findTeam);
//
//        WriterResponse writerResponse = WriterResponse.builder()
//                .id(findTeam.getId())
//                .name(findTeam.getTeamCommunity().getName())
//                .profileImage(findCommunity.getThumbnailImage())
//                .build();
//
//        return PostResponse.ofList(result, null, writerResponse);
//    }
//
//    private List<Interesting> getInterestingByPostAndUser(List<Post> findPosts, User loginUser) {
//        List<Interesting> result = new ArrayList<>();
//
//        for (Post post : findPosts) {
//            Interesting findInteresting = interestingRepository.findByUserAndPost(loginUser, post).orElseGet(() ->
//                    Interesting.builder()
//                            .user(loginUser)
//                            .post(post)
//                            .status(BooleanType.FALSE)
//                            .build());
//
//            result.add(findInteresting);
//        }
//
//        return result;
//    }
//
//    @Transactional
//    public Long createPost(Long communityId, String content, List<MultipartFile> files) {
//        User loginUser = getLoginUser();
//
//        //todo: Community 찾기
//        Community findCommunity = communityRepository.findById(communityId)
//                .orElseThrow(() -> new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));
//
//        //todo: userCommunityInfo 에서 커뮤니티 id로 유저의 닉네임과 프로필 이미지 가져오기
//        UserCommunityInfo findUserCommunityInfo = userCommunityInfoRepository.findByUserAndCommunity(loginUser,
//                findCommunity).orElseThrow(
//                () -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO)
//        );
//
//        //todo: Post 객체 생성
//        String category = "post";
//        try {
//            List<ImageFileInfo> imageInfos = awsS3Util.uploadFiles(files, category);
//
//            Post newPost = Post.builder()
//                    .writerInfo(findUserCommunityInfo)
//                    .content(content)
//                    .build();
//
//            List<ImageFile> imageFiles = imageInfos.stream()
//                    .map(imageInfo -> ImageFile.builder()
//                            .post(newPost)
//                            .path(imageInfo.url())
//                            .width(imageInfo.width())
//                            .height(imageInfo.height())
//                            .build())
//                    .toList();
//
//            imageFileRepository.saveAll(imageFiles);
//
//            //todo: 객체 저장 및 생성된 id 반환
//            postRepository.save(newPost);
//            return newPost.getId();
//        } catch (IOException e) {
//            Post newPost = Post.builder()
//                    .writerInfo(findUserCommunityInfo)
//                    .content(content)
//                    .build();
//
//            //todo: 객체 저장 및 생성된 id 반환
//            postRepository.save(newPost);
//            return newPost.getId();
//        }
//    }
//
//    private User getLoginUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loginId = authentication.getName();
//        return userRepository.findById(Long.valueOf(loginId))
//                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
//    }
//
//    public PostResponse detailPost(Long communityId, Long postId) {
//        Post findPost = postRepository.findById(postId)
//                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));
//
//        WriterResponse writerResponse = WriterResponse.of(findPost.getWriterInfo().getUser().getId(),
//                findPost.getWriterInfo().getNickname(), findPost.getWriterInfo().getProfileImage());
//
//        User loginUser = getLoginUser();
//
//        Interesting interesting = interestingRepository.findByUserAndPost(loginUser, findPost)
//                .orElseGet(() -> Interesting.builder()
//                        .post(findPost)
//                        .user(loginUser)
//                        .status(BooleanType.FALSE)
//                        .build());
//
//        List<ImageFile> imageFiles = findPost.getFiles();
//        List<ImageFileInfo> imageFileInfos = ImageFileInfo.ofList(imageFiles);
//
//        return PostResponse.of(findPost, interesting.getStatus(), writerResponse, imageFileInfos);
//    }
//
//    @Transactional
//    public BooleanType toggleInteresting(Long communityId, Long postId) {
//        User loginUser = getLoginUser();
//
//        Post findPost = postRepository.findById(postId)
//                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));
//
//        Optional<Interesting> findInteresting = interestingRepository.findByUserAndPost(loginUser, findPost);
//        if (findInteresting.isEmpty()) {
//            Interesting newInteresting = Interesting.builder()
//                    .post(findPost)
//                    .user(loginUser)
//                    .status(BooleanType.TRUE)
//                    .build();
//
//            interestingRepository.save(newInteresting);
//            return BooleanType.TRUE;
//        }
//
//        return findInteresting.get().changeStatus();
//    }
//
//    @Transactional(readOnly = true)
//    public List<PostResponse> getUserCommunityPosts() {
//        User loginUser = getLoginUser();
//        List<UserCommunityInfo> userCommunityInfos = userCommunityInfoRepository.findByUser(loginUser);
//
//        List<PostResponse> result = new ArrayList<>();
//
//        for (UserCommunityInfo userCommunityInfo : userCommunityInfos) {
//            Long communityId = userCommunityInfo.getCommunity().getId();
//
//            List<PostResponse> playerPosts = getPlayerPosts(communityId);
//            List<PostResponse> teamPosts = getTeamPosts(communityId);
//
//            result.addAll(playerPosts);
//            result.addAll(teamPosts);
//        }
//
//        return result.stream().sorted(Comparator.comparing(PostResponse::createdAt))
//                .toList();
//    }
//}
