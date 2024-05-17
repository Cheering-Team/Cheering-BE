package com.cheering.user;

public class UserResponse {
    public record UserDTO(Long id, String phone, String nickname) {
        public UserDTO(User user) {
            this(user.getId(), user.getPhone(), user.getNickname());
        }
    }

    public record TokenDTO(String accessToken, String refreshToken) { }
    public record AccessTokenDTO(String accessToken) { }
}
