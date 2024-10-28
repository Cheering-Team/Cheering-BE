package com.cheering.notification;

import com.cheering.comment.Comment;
import com.cheering.comment.reComment.ReComment;
import com.cheering.fan.FanResponse;
import com.cheering.post.Post;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {
    public record NotificationPostDTO (Long id, PostImageResponse.ImageDTO image) {
        public NotificationPostDTO(Post post, PostImage postImage){
            this(post.getId(), new PostImageResponse.ImageDTO(postImage));
        }
        public NotificationPostDTO(Post post){
            this(post.getId(), null);
        }

    }
    public record NotificationDTO (Long id, NotificaitonType type, FanResponse.FanDTO from, FanResponse.FanDTO to, Long count, NotificationPostDTO post, String content, Boolean isRead, LocalDateTime createdAt) {

        // 좋아요 알림 생성자
        public NotificationDTO(Notification notification, Long count, PostImage postImage) {
            this(notification.getId(), notification.getType(), new FanResponse.FanDTO(notification.getFrom()), new FanResponse.FanDTO(notification.getTo()), count, new NotificationPostDTO(notification.getPost(), postImage), null, notification.getIsRead(), notification.getCreatedAt());
        }
        public NotificationDTO(Notification notification, Long count) {
            this(notification.getId(), notification.getType(), new FanResponse.FanDTO(notification.getFrom()), new FanResponse.FanDTO(notification.getTo()), count, new NotificationPostDTO(notification.getPost()), null, notification.getIsRead(), notification.getCreatedAt());
        }

        // 댓글 알림 생성자
        public NotificationDTO(Notification notification, Comment comment, PostImage postImage) {
            this(notification.getId(), notification.getType(), new FanResponse.FanDTO(notification.getFrom()), new FanResponse.FanDTO(notification.getTo()), null, new NotificationPostDTO(notification.getPost(), postImage), comment.getContent(), notification.getIsRead(), notification.getCreatedAt());
        }
        public NotificationDTO(Notification notification, Comment comment) {
            this(notification.getId(), notification.getType(), new FanResponse.FanDTO(notification.getFrom()), new FanResponse.FanDTO(notification.getTo()), null,  new NotificationPostDTO(notification.getPost()), comment.getContent(), notification.getIsRead(), notification.getCreatedAt());
        }

        // 답글 알림 생성자
        public NotificationDTO(Notification notification, ReComment reComment, PostImage postImage) {
            this(notification.getId(), notification.getType(), new FanResponse.FanDTO(notification.getFrom()), new FanResponse.FanDTO(notification.getTo()), null, new NotificationPostDTO(notification.getPost(), postImage), reComment.getContent(), notification.getIsRead(), notification.getCreatedAt());
        }
        public NotificationDTO(Notification notification, ReComment reComment) {
            this(notification.getId(), notification.getType(), new FanResponse.FanDTO(notification.getFrom()), new FanResponse.FanDTO(notification.getTo()), null,  new NotificationPostDTO(notification.getPost()), reComment.getContent(), notification.getIsRead(), notification.getCreatedAt());
        }
    }

    public record NotificationListDTO(List<NotificationDTO> notifications, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public NotificationListDTO(Page<?> page, List<NotificationDTO> notifications) {
            this(notifications, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }
    }
}
