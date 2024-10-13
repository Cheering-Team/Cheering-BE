package com.cheering._core.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    @Value("${coolsms.sender}")
    private String senderNumber;
    @Value("${coolsms.apiKey}")
    private String apiKey;
    @Value("${coolsms.apiSecret}")
    private String apiSecretKey;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendCode(String to, String verificationCode) {
        Message message = new Message();
        message.setFrom("01062013110");
        message.setTo(to);
        message.setText("[치어링] 인증번호 ["+verificationCode+"]를 입력해주세요.\n");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }

    public SingleMessageSentResponse sendAccount(String to, String account, String password) {
        Message message = new Message();
        message.setFrom("01062013110");
        message.setTo(to);
        message.setText("[치어링]\n선수용 계정입니다. 절대 남에게 보여주지 마십시오.\n"+"휴대폰 번호: "+account + "\n인증번호: "+password+"\n안전한 곳에 보관하여 사용해주세요.");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }
}