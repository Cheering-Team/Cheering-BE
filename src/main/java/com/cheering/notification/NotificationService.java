package com.cheering.notification;

import com.cheering.post.Like.LikeRepository;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final LikeRepository likeRepository;
    private final PostImageRepository postImageRepository;

    public NotificationResponse.NotificationListDTO getNotifications(User user, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUser(user, pageable);

        Set<Long> seenPostIds = new HashSet<>();

        List<NotificationResponse.NotificationDTO> notificationDTOS = notifications.getContent().stream().filter((notification -> {
            if(notification.getType().equals("LIKE")) {
                if(seenPostIds.contains(notification.getPost().getId())) {
                    return false;
                }
                seenPostIds.add(notification.getPost().getId());
            }
            return true;
        })).map((notification -> {
            if(notification.getType().equals("LIKE")) {
                Long count = likeRepository.countByPostId(notification.getPost().getId());
                List<PostImage> postImages = postImageRepository.findByPostId(notification.getPost().getId());
                return postImages.isEmpty() ? new NotificationResponse.NotificationDTO(notification, count) : new NotificationResponse.NotificationDTO(notification, count, postImages.get(0));
            }
            return null;
        })).toList();

        return new NotificationResponse.NotificationListDTO(notifications, notificationDTOS);
    }
}
