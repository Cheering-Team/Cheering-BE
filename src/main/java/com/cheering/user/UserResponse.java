package com.cheering.user;

import java.time.LocalDateTime;

public class UserResponse {
    public record UserDTO(Long id, String phone, String nickname, String role) {
        public UserDTO(User user) {
            this(user.getId(), user.getPhone(), user.getNickname(), user.getRole().toString());
        }
    }

    public record UserWithCreatedAtDTO(Long id, String phone, String nickname, LocalDateTime createdAt) {
        public UserWithCreatedAtDTO(User user) {
            this(user.getId(), user.getPhone(), user.getNickname(), user.getCreatedAt());
        }
    }

    public record TokenDTO(String accessToken, String refreshToken) { }
}
