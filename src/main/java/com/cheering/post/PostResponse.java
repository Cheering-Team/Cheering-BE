package com.cheering.post;

import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;
import com.cheering.post.PostImage.PostImageResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    public record PostIdDTO (Long id, PlayerUserResponse.PlayerUserDTO playerUser) { }

    public record PostInfoDTO(Long id, PlayerUserResponse.PlayerUserDTO playerUser, String content, LocalDateTime createdAt, List<String> tags,
                              boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, WriterDTO writer) { }

    public record PostInfoWithPlayerDTO(Long id, PlayerUserResponse.PlayerUserDTO playerUser, PlayerResponse.PlayerDTO player, String content, LocalDateTime createdAt, List<String> tags,
                                        boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, WriterDTO writer) { }

    public record PostByIdDTO (PostInfoDTO post, PlayerResponse.PlayerNameDTO player) { }

    public record PostListDTO(List<PostInfoDTO> posts, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public PostListDTO(Page<?> page, List<PostInfoDTO> posts) {
            this(posts, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }

    }

    public record PostWithPlayerListDTO(List<PostInfoWithPlayerDTO> posts, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public PostWithPlayerListDTO(Page<?> page, List<PostInfoWithPlayerDTO> posts) {
            this(posts, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }

    }

    public record WriterDTO (Long id, String name, String image) {
        public WriterDTO(PlayerUser playerUser) {
            this(playerUser.getId(), playerUser.getNickname(), playerUser.getImage());
        }
    }
}
