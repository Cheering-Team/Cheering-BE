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

    public record SaveFCMDTO(
            String deviceId,
            String token
    ) { }

    public record AgeAndGenderDTO(
            Integer age,
            Gender gender
    ) { }

    public record AgeAndGenderAndProfileDTO(
            Integer age,
            Gender gender,
            String name,
            String status
    ) {}

    public record UpdateMeetProfileDTO(
            String name,
            String image
    ) {}
}
