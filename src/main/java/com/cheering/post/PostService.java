package com.cheering.post;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.fan.CommunityType;
import com.cheering.player.Player;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.notification.NotificaitonType;
import com.cheering.notification.Notification;
import com.cheering.notification.NotificationRepository;
import com.cheering.player.PlayerRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
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
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FanRepository fanRepository;
    private final TeamRepository teamRepository;
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
    private final PlayerRepository playerRepository;
    private final BlockRepository blockRepository;
    private final BadWordService badWordService;
    private final S3Util s3Util;
    private final FcmServiceImpl fcmService;

    @Transactional
    public PostResponse.PostIdDTO writePost(Long communityId, String content, List<MultipartFile> images, List<Integer> widthDatas, List<Integer> heightDatas, List<String> tags, User user) {
        if(badWordService.containsBadWords(content)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Fan writer = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Post post = Post.builder()
                .content(content)
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

    // 커뮤니티 게시글 불러오기 (무한 스크롤)
    public PostResponse.PostListDTO getPosts(Long communityId, String tagName, Pageable pageable, User user) {
        Page<Post> postList;

        Fan curFan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(()-> new CustomException((ExceptionCode.CUR_FAN_NOT_FOUND)));

        if(tagName.isEmpty()) {
            postList = postRepository.findByCommunityId(communityId, curFan, pageable);
        } else if(tagName.equals("hot")) {
            postList = postRepository.findHotPosts(communityId, curFan, pageable);
        } else {
            postList = postRepository.findByCommunityAndTagName(communityId, tagName, curFan, pageable);
        }

        List<PostResponse.PostInfoWithCommunityDTO> postInfoDTOS = postList.getContent().stream().map((post -> getPostInfo(post, curFan))).toList();

        return new PostResponse.PostListDTO(postList, postInfoDTOS);
    }

    // 게시글 불러오기
    public PostResponse.PostInfoWithCommunityDTO getPostById(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan writer = post.getWriter();

        Fan curFan = fanRepository.findByCommunityIdAndUser(writer.getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        return getPostInfo(post, curFan);
    }

    @Transactional
    // 게시글 좋아요
    public PostResponse.LikeResponseDTO toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(post.getWriter().getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

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
                notificationRepository.deleteLikeByPostAndFrom(post, curFan, NotificaitonType.LIKE);
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
        Fan curFan = fanRepository.findByCommunityIdAndUser(writer.getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

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
        Fan curFan = fanRepository.findByCommunityIdAndUser(writer.getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

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


    public PostResponse.PostListDTO getMyHotPosts(Pageable pageable, User user) {
        List<Fan> fans = fanRepository.findByUserOrderByCommunityOrderAsc(user);
        List<Long> communityIds = fans.stream().map(Fan::getCommunityId).toList();

        Page<Post> posts = postRepository.findMyHotPosts(communityIds, fans, pageable);

        List<PostResponse.PostInfoWithCommunityDTO> postInfoDTOS = posts.getContent().stream().map((post -> {
            Fan curFan = fanRepository.findByCommunityIdAndUser(post.getWriter().getCommunityId(), user).orElseThrow(()-> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

            return getPostInfo(post, curFan);
        })).toList();

        return new PostResponse.PostListDTO(posts, postInfoDTOS);
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

    @NotNull
    private PostResponse.PostInfoWithCommunityDTO getPostInfo(Post post, Fan curFan) {
        List<PostTag> postTags = postTagRepository.findByPost(post);
        List<String> tags = postTags.stream().map((postTag) -> {
            Tag tag = tagRepository.findById(postTag.getTag().getId()).orElseThrow(()-> new CustomException(ExceptionCode.TAG_NOT_FOUND));

            return tag.getName();
        }).toList();

        List<PostImage> postImages = postImageRepository.findByPost(post);
        List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

        Optional<Like> like = likeRepository.findByPostAndFan(post, curFan);
        Long likeCount = likeRepository.countByPost(post);

        Long commentCount = commentRepository.countByPost(post) + reCommentRepository.countByPost(post);

        if(curFan.getType().equals(CommunityType.TEAM)) {
            Team team = teamRepository.findById(curFan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

            return new PostResponse.PostInfoWithCommunityDTO(post, tags, like.isPresent(), likeCount, commentCount, imageDTOS, curFan, team);
        } else {
            Player player = playerRepository.findById(curFan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

            return new PostResponse.PostInfoWithCommunityDTO(post, tags, like.isPresent(), likeCount, commentCount, imageDTOS, curFan, player);
        }
    }
}
