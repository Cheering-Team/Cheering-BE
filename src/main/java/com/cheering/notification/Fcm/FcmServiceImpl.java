package com.cheering.notification.Fcm;

import com.cheering.user.deviceToken.DeviceTokenRepository;
import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl {
    private final DeviceTokenRepository deviceTokenRepository;

    public void sendPostMessageTo(String token, String title, String body, Long postId, Long notificationId) {
        Message message = Message.builder()
                .setToken(token)
                .putData("type", "POST")
                .putData("postId", postId.toString())
                .putData("notificationId", notificationId.toString())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendMatchStartMessageTo(String token, String title, String body, Long matchId, Long communityId) {
        Message message = Message.builder()
                .setToken(token)
                .putData("type", "MATCH")
                .putData("matchId", matchId.toString())
                .putData("communityId", communityId.toString())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendMatchEndPostMessageTo(String token, String title, String body, Long postId) {
        Message message = Message.builder()
                .setToken(token)
                .putData("type", "MATCH_END_POST")
                .putData("postId", postId.toString())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendMatchEndCommunityMessageTo(String token, String title, String body, Long communityId) {
        Message message = Message.builder()
                .setToken(token)
                .putData("type", "MATCH_END_COMMUNITY")
                .putData("communityId", communityId.toString())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendChatMessageTo(String token, Integer count) {
        Message message = Message.builder()
                .setToken(token)
                .putData("type", "CHAT")
                .putData("count", count.toString())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (ErrorCode.NOT_FOUND.equals(e.getErrorCode())) {
                deviceTokenRepository.deleteByToken(token);
            } else {
                System.err.println(e.getMessage());
            }
        }
    }
}
