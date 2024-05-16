package com.cheering.user;

public class UserResponse {
    public record UserDTO(Long id, String phone, String nickname) {
        public UserDTO(User user) {
            this(user.getId(), user.getPhone(), user.getNickname());
        }
    }
}
