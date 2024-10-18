package com.cheering.user;

import com.cheering.community.Community;
import com.cheering.community.CommunityResponse;
import com.cheering.community.relation.Fan;

import java.time.LocalDateTime;

public class UserResponse {
    public record UserDTO(Long id, String phone, String name, Role role, CommunityResponse.CommunityDTO community) {
        public UserDTO(User user) {
            this(user.getId(), user.getPhone(), user.getName(), user.getRole(), null);
        }

        public UserDTO(User user, Community community, Long fanCount) {
            this(user.getId(), user.getPhone(), user.getName(), user.getRole(),  new CommunityResponse.CommunityDTO(community, fanCount, null, null, null, null, null, null));
        }

        public UserDTO(User user, Community community, Fan fan, Long fanCount) {
            this(user.getId(), user.getPhone(), user.getName(), user.getRole(), new CommunityResponse.CommunityDTO(community, fanCount, fan, null, null, null, null, null));
        }
    }

    public record UserWithCreatedAtDTO(Long id, String phone, String name, LocalDateTime createdAt) {
        public UserWithCreatedAtDTO(User user) {
            this(user.getId(), user.getPhone(), user.getName(), user.getCreatedAt());
        }
    }

    public record TokenDTO(String accessToken, String refreshToken) { }
}
