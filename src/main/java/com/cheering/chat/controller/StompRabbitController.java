package com.cheering.chat.controller;

import com.cheering.chat.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompRabbitController {

    private final RabbitTemplate template;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final static String CHAT_QUEUE_NAME = "chat.queue";

    @MessageMapping("chat.enter.{chatRoomId}")
    public void enter(ChatDto chat, @DestinationVariable("chatRoomId") String chatRoomId) {
        chat.setMessage("입장했습니다.");
//        chat.regDate(LocalDateTime.now());
        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat); // exchange
        //template.convertAndSend("room." + chatRoomId, chat); //queue
        //template.convertAndSend("amq.topic", "room." + chatRoomId, chat); //topic
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void send(ChatDto chat, @DestinationVariable("chatRoomId") String chatRoomId) {

//        chat.setRegDate(LocalDateTime.now());
        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
        //template.convertAndSend( "room." + chatRoomId, chat);
        //template.convertAndSend("amq.topic", "room." + chatRoomId, chat);
    }

    //receive()는 단순히 큐에 들어온 메세지를 소비만 한다. (현재는 디버그용도)
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void receive(ChatDto chat) {

        System.out.println("received : " + chat.getMessage());
//        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chat.getChatRoomId(), chat);
    }
}