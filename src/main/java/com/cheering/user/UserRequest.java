package com.cheering.user;

public class UserRequest {
    public record SendSMSDTO(
        String phone
    ) { }

    public record CheckCodeDTO(
            String phone,
            String code
    ) { }
}
