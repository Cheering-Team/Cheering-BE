package com.cheering.post;

import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserResponse;
import com.cheering.post.PostImage.PostImageResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    public record PostIdDTO (Long id) { }

    public record PostInfoWithPlayerDTO(Long id, PlayerResponse.PlayerDTO player, String content, LocalDateTime createdAt, Boolean isHide, List<String> tags,
                                        Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, WriterDTO writer, PlayerUserResponse.PlayerUserDTO playerUser) {
        public PostInfoWithPlayerDTO(Post post, List<String> tags, Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, PlayerUser playerUser) {
            this(post.getId(), new PlayerResponse.PlayerDTO(post.getPlayerUser().getPlayer()), post.getContent(), post.getCreatedAt(), post.getIsHide(), tags, isLike, likeCount, commentCount, images, new WriterDTO(post.getPlayerUser(), playerUser.getPlayer().getOwner() != null && playerUser.getPlayer().getOwner().equals(post.getPlayerUser())), new PlayerUserResponse.PlayerUserDTO(playerUser));
        }
    }

    public record PostListDTO(List<PostInfoWithPlayerDTO> posts, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public PostListDTO(Page<?> page, List<PostInfoWithPlayerDTO> posts) {
            this(posts, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }

    }

    public record DailyListDTO(List<PostInfoWithPlayerDTO> dailys, Boolean isOwner, PlayerUserResponse.PlayerUserDTO owner, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public DailyListDTO(Page<?> page, List<PostInfoWithPlayerDTO> dailys, Boolean isOwner, PlayerUserResponse.PlayerUserDTO owner) {
            this(dailys, isOwner, owner, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }
    }

    public record WriterDTO (Long id, String nickname, String image, Boolean isOwner) {
        public WriterDTO(PlayerUser playerUser, Boolean isOwner) {
            this(playerUser.getId(), playerUser.getNickname(), playerUser.getImage(), isOwner);
        }
    }
}
