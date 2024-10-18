package com.cheering.notification;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final LikeRepository likeRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
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
            List<PostImage> postImages = postImageRepository.findByPost(notification.getPost());
            if(notification.getType().equals("LIKE")) {
                Long count = likeRepository.countByPost(notification.getPost());
                return postImages.isEmpty() ? new NotificationResponse.NotificationDTO(notification, count) : new NotificationResponse.NotificationDTO(notification, count, postImages.get(0));
            } else if(notification.getType().equals("COMMENT")) {
                return postImages.isEmpty() ? new NotificationResponse.NotificationDTO(notification, notification.getComment()) : new NotificationResponse.NotificationDTO(notification, notification.getComment(), postImages.get(0));
            } else {
                return postImages.isEmpty() ? new NotificationResponse.NotificationDTO(notification, notification.getReComment()) : new NotificationResponse.NotificationDTO(notification, notification.getReComment(), postImages.get(0));
            }
        })).toList();

        notifications.forEach(notification -> {
            if(!notification.getIsRead()) {
                notification.setIsRead(true);
            }
        });

        return new NotificationResponse.NotificationListDTO(notifications, notificationDTOS);
    }

    public boolean isUnread(User user) {
        return notificationRepository.isUnreadByUser(user);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        notificationRepository.deleteByCreatedAtBefore(oneMonthAgo);
    }

    @Transactional
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(()-> new CustomException(ExceptionCode.NOTIFICATION_NOT_FOUND));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}
