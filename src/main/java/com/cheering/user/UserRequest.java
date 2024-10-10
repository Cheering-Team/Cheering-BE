package com.cheering.user;

public class UserRequest {
    public record SendSMSDTO(
        String phone
    ) { }

    public record CheckCodeDTO(
            String phone,
            String code
    ) { }

    public record SignUpDTO (
            String phone,
            String nickname
    ) { }

    public record NicknameDTO (
            String nickname
    ) { }

    public record IdDTO (
            String accessToken,
            Long userId
    ) { }

    public record SocialTokenDTO (
        String accessToken,
        String name
    ) { }

    public record SocialCheckCodeDTO (
            String accessToken,
            String phone,
            String code
    ) { }
}
