package com.cheering.post;

import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.post.PostImage.PostImageResponse;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    public record PostIdDTO (Long id) { }

    public record PostInfoDTO(Boolean isWriter, String content, LocalDateTime createdAt, List<String> tags, List<PostImageResponse.ImageDTO> images, WriterDTO writer) { }

    public record PostByIdDTO (PostInfoDTO post, PlayerResponse.PlayerNameDTO player) { }

    public record WriterDTO (Long id, String name, String image) {
        public WriterDTO(PlayerUser playerUser) {
            this(playerUser.getId(), playerUser.getNickname(), playerUser.getImage());
        }
    }
}
