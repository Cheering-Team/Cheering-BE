package com.cheering.post;

import com.cheering.community.CommunityResponse;
import com.cheering.player.Player;
import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import com.cheering.post.PostImage.PostImageResponse;
import com.cheering.team.Team;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    public record PostIdDTO (Long id) { }

    public record PostInfoWithCommunityDTO(Long id, CommunityResponse.CommunityDTO community, String content, LocalDateTime createdAt, Boolean isHide, List<String> tags,
                                           Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, FanResponse.FanDTO writer, FanResponse.FanDTO user) {
        public PostInfoWithCommunityDTO(Post post, List<String> tags, Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, Fan fan, Team team) {
            this(post.getId(), new CommunityResponse.CommunityDTO(team, null, null), post.getContent(), post.getCreatedAt(), post.getIsHide(), tags, isLike, likeCount, commentCount, images, new FanResponse.FanDTO(post.getWriter()), new FanResponse.FanDTO(fan));
        }

        public PostInfoWithCommunityDTO(Post post, List<String> tags, Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, Fan fan, Player player) {
            this(post.getId(), new CommunityResponse.CommunityDTO(player, null, null), post.getContent(), post.getCreatedAt(), post.getIsHide(), tags, isLike, likeCount, commentCount, images, new FanResponse.FanDTO(post.getWriter()), new FanResponse.FanDTO(fan));
        }

        public PostInfoWithCommunityDTO(Post post, List<String> tags, Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, Fan fan) {
            this(post.getId(), null, post.getContent(), post.getCreatedAt(), post.getIsHide(), tags, isLike, likeCount, commentCount, images, new FanResponse.FanDTO(post.getWriter()), new FanResponse.FanDTO(fan));
        }
    }

    public record PostListDTO(List<PostInfoWithCommunityDTO> posts, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public PostListDTO(Page<?> page, List<PostInfoWithCommunityDTO> posts) {
            this(posts, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }

    }

    public record DailyListDTO(List<PostInfoWithCommunityDTO> dailys, Boolean isManager, FanResponse.FanDTO manager, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public DailyListDTO(Page<?> page, List<PostInfoWithCommunityDTO> dailys, Boolean isManager, FanResponse.FanDTO manager) {
            this(dailys, isManager, manager, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }
    }

    public record LikeResponseDTO(Boolean isLike, Long likeCount) { }
}
