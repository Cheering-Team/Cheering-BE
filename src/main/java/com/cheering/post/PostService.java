package com.cheering.post;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.community.relation.FanType;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.notification.NotificaitonType;
import com.cheering.notification.Notification;
import com.cheering.notification.NotificationRepository;
import com.cheering.community.Community;
import com.cheering.community.CommunityRepository;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanRepository;
import com.cheering.community.relation.FanResponse;
import com.cheering.post.Like.Like;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostImage.PostImageResponse;
import com.cheering.post.PostImage.PostImageType;
import com.cheering.post.Tag.Tag;
import com.cheering.post.Tag.TagRepository;
import com.cheering.post.relation.PostTag;
import com.cheering.post.relation.PostTagRepository;
import com.cheering.report.block.BlockRepository;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.postReport.PostReport;
import com.cheering.report.postReport.PostReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FanRepository fanRepository;
    private final PostImageRepository postImageRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final NotificationRepository notificationRepository;
    private final CommunityRepository communityRepository;
    private final BlockRepository blockRepository;
    private final BadWordService badWordService;
    private final S3Util s3Util;
    private final FcmServiceImpl fcmService;

    @Transactional
    public PostResponse.PostIdDTO writePost(Long communityId, String content, List<MultipartFile> images, List<Integer> widthDatas, List<Integer> heightDatas, List<String> tags, User user) {
        if(badWordService.containsBadWords(content)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Community community = communityRepository.findById(communityId).orElseThrow(()->new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));

        Fan writer = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Post post = Post.builder()
                .content(content)
                .type(writer.getType() == FanType.FAN ? PostType.FAN_POST : PostType.PLAYER_POST)
                .writer(writer)
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

        if(images != null) {
            for(int i=0; i<images.size(); i++) {
                MultipartFile image = images.get(i);
                Integer width = widthDatas.get(i);
                Integer height = heightDatas.get(i);

                String imageUrl = s3Util.upload(image);

                PostImageType type = getPostImageType(image);

                PostImage postImage = PostImage.builder()
                        .path(imageUrl)
                        .width(width)
                        .height(height)
                        .post(post)
                        .type(type)
                        .build();

                postImageRepository.save(postImage);
            }
        }
        return new PostResponse.PostIdDTO(post.getId());
    }

    // 커뮤니티 게시글 불러오기 (무한 스크롤) (id = 0 -> 모든 커뮤니티 게시글)
    public PostResponse.PostListDTO getPosts(Long communityId, String type, String tagName, Pageable pageable, User user) {
        Page<Post> postList;

        if(communityId == 0) {
            List<Fan> fans = fanRepository.findByUser(user);
            List<Community> communities = fans.stream().map((Fan::getCommunity)).toList();

            postList = postRepository.findByCommunities(communities, PostType.valueOf(type), fans, pageable);
        } else {
            Community community = communityRepository.findById(communityId).orElseThrow(()->new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));

            if(tagName.isEmpty()) {
                Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()-> new CustomException((ExceptionCode.CUR_FAN_NOT_FOUND)));
                postList = postRepository.findByCommunity(community, PostType.valueOf(type), curFan, pageable);
            } else if(tagName.equals("hot")) {
                Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()-> new CustomException((ExceptionCode.CUR_FAN_NOT_FOUND)));
                postList = postRepository.findHotPosts(community, PostType.valueOf(type), curFan, pageable);
            } else {
                Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()-> new CustomException((ExceptionCode.CUR_FAN_NOT_FOUND)));
                postList = postRepository.findByCommunityAndTagName(community, PostType.valueOf(type), tagName, curFan, pageable);
            }
        }

        List<PostResponse.PostInfoWithPlayerDTO> postInfoDTOS = postList.getContent().stream().map((post -> {
            Fan curFan = fanRepository.findByCommunityAndUser(post.getWriter().getCommunity(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

            // 태그
            List<PostTag> postTags = postTagRepository.findByPost(post);
            List<String> tags = postTags.stream().map((postTag) -> {
                Tag tag = tagRepository.findById(postTag.getTag().getId()).orElseThrow(()-> new CustomException(ExceptionCode.TAG_NOT_FOUND));

                return tag.getName();
            }).toList();

            Optional<Like> like = likeRepository.findByPostAndFan(post, curFan);
            Long likeCount = likeRepository.countByPost(post);

            Long commentCount = commentRepository.countByPost(post) + reCommentRepository.countByPost(post);

            List<PostImage> postImages = postImageRepository.findByPost(post);
            List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

            return new PostResponse.PostInfoWithPlayerDTO(post, tags, like.isPresent(), likeCount, commentCount, imageDTOS, curFan);
        })).toList();

        return new PostResponse.PostListDTO(postList, postInfoDTOS);
    }

    // 게시글 불러오기
    public PostResponse.PostInfoWithPlayerDTO getPostById(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan writer = post.getWriter();
        Community community = writer.getCommunity();

        Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<Like> like = likeRepository.findByPostAndFan(post, curFan);
        Long likeCount = likeRepository.countByPost(post);

        Long commentCount = commentRepository.countByPost(post) + reCommentRepository.countByPost(post);

        List<PostTag> postTags = postTagRepository.findByPost(post);

        List<String> tags = postTags.stream().map((postTag) -> {
            Tag tag = tagRepository.findById(postTag.getTag().getId()).orElseThrow(()-> new CustomException(ExceptionCode.TAG_NOT_FOUND));

            return tag.getName();
        }).toList();

        List<PostImage> postImages = postImageRepository.findByPost(post);

        List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

        return new PostResponse.PostInfoWithPlayerDTO(post, tags, like.isPresent(), likeCount, commentCount, imageDTOS, curFan);

    }

    @Transactional
    // 게시글 좋아요
    public PostResponse.LikeResponseDTO toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Community community = post.getWriter().getCommunity();

        Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<Like> like = likeRepository.findByPostAndFan(post, curFan);

        if(like.isEmpty()) {
            Like newLike = Like.builder()
                    .post(post)
                    .fan(curFan)
                    .build();

            likeRepository.save(newLike);

            Long likeCount = likeRepository.countByPost(post);

            if(!post.getWriter().equals(curFan) && blockRepository.findByFromAndTo(post.getWriter(), curFan).isEmpty()){
                Notification notification = new Notification(NotificaitonType.LIKE, post.getWriter(), curFan, post);

                notificationRepository.save(notification);
                if(notification.getTo().getUser().getDeviceToken() != null) {
                    fcmService.sendMessageTo(notification.getTo().getUser().getDeviceToken(), curFan.getName(), "회원님의 게시글을 좋아합니다.", postId, notification.getId());
                }
            }
            return new PostResponse.LikeResponseDTO(true, likeCount);
        } else {
            if(!post.getWriter().equals(curFan)) {
                notificationRepository.deleteLikeByPostAndFrom(post, curFan, "LIKE");
            }
            likeRepository.delete(like.get());
            Long likeCount = likeRepository.countByPost(post);

            return new PostResponse.LikeResponseDTO(false, likeCount);
        }
    }

    // 게시글 수정
    @Transactional
    public void editPost(Long postId, String content, List<MultipartFile> images, List<Integer> widthDatas, List<Integer> heightDatas, List<String> tags, User user) {
        if(badWordService.containsBadWords(content)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan writer = post.getWriter();
        Fan curFan = fanRepository.findByCommunityAndUser(writer.getCommunity(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(!writer.equals(curFan)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        post.setContent(content);
        postRepository.save(post);

        postTagRepository.deleteByPost(post);

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

        List<PostImage> postImages = postImageRepository.findByPost(post);

        for(PostImage postImage : postImages) {
            s3Util.deleteImageFromS3(postImage.getPath());
            postImageRepository.delete(postImage);
        }

        if(images != null) {
            for(int i=0; i<images.size(); i++) {
                MultipartFile image = images.get(i);
                Integer width = widthDatas.get(i);
                Integer height = heightDatas.get(i);

                String imageUrl = s3Util.upload(image);

                PostImageType type = getPostImageType(image);

                PostImage postImage = PostImage.builder()
                        .path(imageUrl)
                        .width(width)
                        .height(height)
                        .post(post)
                        .type(type)
                        .build();

                postImageRepository.save(postImage);
            }
        }
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan writer = post.getWriter();
        Fan curFan = fanRepository.findByCommunityAndUser(writer.getCommunity(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(!writer.equals(curFan)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        List<PostImage> postImages = postImageRepository.findByPost(post);

        for(PostImage postImage : postImages) {
            s3Util.deleteImageFromS3(postImage.getPath());
        }

        // Comment
        List<Comment> commentList = commentRepository.findByPost(post);

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByCommentIn(commentList);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }

        List<CommentReport> commentReports = commentReportRepository.findByCommentIn(commentList);
        for(CommentReport commentReport : commentReports) {
            commentReport.setComment(null);
        }

        // Report
        List<PostReport> reportList = postReportRepository.findByPost(post);
        for(PostReport report : reportList) {
            report.setPost(null);
        }

        // Post
        postRepository.delete(post);
    }

    // 데일리 작성
    public void writeDaily(Long communityId, PostRequest.PostContentDTO requestDTO, User user) {
        Community community = communityRepository.findById(communityId).orElseThrow(()-> new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(!curFan.equals(community.getManager())) {
            throw new CustomException(ExceptionCode.NOT_OWNER);
        }

        Post daily = Post.builder()
                .type(PostType.DAILY)
                .content(requestDTO.content())
                .writer(curFan)
                .build();

        postRepository.save(daily);
    }

    @Transactional
    // 데일리 불러오기
    public PostResponse.DailyListDTO getDailys(Long communityId, String dateString, Pageable pageable, User user) {
        Community community = communityRepository.findById(communityId).orElseThrow(()->new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));
        Fan curFan = fanRepository.findByCommunityAndUser(community, user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Page<Post> posts;

        if(dateString.isEmpty()) {
            posts = postRepository.findAllDaily(community, PostType.DAILY, pageable);
        } else {
            LocalDate date = LocalDate.parse(dateString);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            posts = postRepository.findDaily(community, PostType.DAILY, startOfDay, endOfDay, pageable);
        }

        return new PostResponse.DailyListDTO(posts, posts.stream().map((post -> {
            Long commentCount = commentRepository.countByPost(post);

            return new PostResponse.PostInfoWithPlayerDTO(post, null, null, null, commentCount, null, curFan);})).toList(), community.getManager().equals(curFan), new FanResponse.FanDTO(community.getManager()));
    }

    // 데일리 수정
    @Transactional
    public void editDaily(Long dailyId, PostRequest.PostContentDTO requestDTO, User user) {
        Post daily = postRepository.findById(dailyId).orElseThrow(()-> new CustomException(ExceptionCode.POST_NOT_FOUND));

        daily.setContent(requestDTO.content());

        postRepository.save(daily);
    }

    // 데일리 삭제
    @Transactional
    public void deleteDaily(Long dailyId) {
        postRepository.deleteById(dailyId);
    }

    public List<String> getDailyExist(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(()->new CustomException(ExceptionCode.COMMUNITY_NOT_FOUND));

        return postRepository.findDistinctDailyDates(community, PostType.DAILY);
    }

    @NotNull
    private static PostImageType getPostImageType(MultipartFile image) {
        PostImageType type;

        int lastDotIndex = image.getOriginalFilename().lastIndexOf(".");
        if(lastDotIndex == -1) {
            throw new CustomException(ExceptionCode.INVALID_FILE_EXTENSION);
        }
        String extension = image.getOriginalFilename().substring(lastDotIndex + 1).toLowerCase();

        if(extension.equals("mov") || extension.equals("mp4") || extension.equals("avi") || extension.equals("mkv")) {
            type = PostImageType.VIDEO;
        } else {
            type = PostImageType.IMAGE;
        }
        return type;
    }
}
