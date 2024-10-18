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
            String name
    ) { }

    public record NameDTO(
            String name
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
