package com.cheering.notification.Fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FcmServiceImpl {
    public void sendMessageTo(String token, String title, String body, Long postId, Long notificationId) {
        Message message = Message.builder()
                .setToken(token)
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
            e.printStackTrace();
        }
    }
}
