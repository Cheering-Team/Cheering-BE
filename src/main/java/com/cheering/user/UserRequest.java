package com.cheering.user;

public class UserRequest {
    public record SendSMSDTO(
        String phone
    ) { }
}
