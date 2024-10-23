package com.cheering.post;

import com.cheering.community.CommunityResponse;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanResponse;
import com.cheering.post.PostImage.PostImageResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    public record PostIdDTO (Long id) { }

    public record PostInfoWithPlayerDTO(Long id, CommunityResponse.CommunityDTO community, String content, LocalDateTime createdAt, Boolean isHide, List<String> tags,
                                        Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, FanResponse.FanDTO writer, FanResponse.FanDTO user) {
        public PostInfoWithPlayerDTO(Post post, List<String> tags, Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, Fan fan) {
            this(post.getId(), new CommunityResponse.CommunityDTO(post.getWriter().getCommunity()), post.getContent(), post.getCreatedAt(), post.getIsHide(), tags, isLike, likeCount, commentCount, images, new FanResponse.FanDTO(post.getWriter()), new FanResponse.FanDTO(fan));
        }
    }

    public record PostListDTO(List<PostInfoWithPlayerDTO> posts, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public PostListDTO(Page<?> page, List<PostInfoWithPlayerDTO> posts) {
            this(posts, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }

    }

    public record DailyListDTO(List<PostInfoWithPlayerDTO> dailys, Boolean isManager, FanResponse.FanDTO manager, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
        public DailyListDTO(Page<?> page, List<PostInfoWithPlayerDTO> dailys, Boolean isManager, FanResponse.FanDTO manager) {
            this(dailys, isManager, manager, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        }
    }
}
