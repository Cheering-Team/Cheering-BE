package com.cheering.user;

import java.time.LocalDateTime;

public class UserResponse {
    public record UserDTO(Long id, String phone, String name, Role role) {
        public UserDTO(User user) {
            this(user.getId(), user.getPhone(), user.getName(), user.getRole());
        }
    }

    public record UserWithCreatedAtDTO(Long id, String phone, String name, LocalDateTime createdAt) {
        public UserWithCreatedAtDTO(User user) {
            this(user.getId(), user.getPhone(), user.getName(), user.getCreatedAt());
        }
    }

    public record TokenDTO(String accessToken, String refreshToken) { }
}
