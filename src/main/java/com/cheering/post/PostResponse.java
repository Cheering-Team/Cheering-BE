package com.cheering.post;

import com.cheering.community.CommunityResponse;
import com.cheering.match.MatchResponse;
import com.cheering.player.Player;
import com.cheering.fan.Fan;
import com.cheering.fan.FanResponse;
import com.cheering.post.PostImage.PostImageResponse;
import com.cheering.team.Team;
import com.cheering.vote.Vote;
import com.cheering.vote.VoteResponse;
import com.cheering.vote.voteOption.VoteOption;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    public record PostIdDTO (Long id) { }

    public record PostInfoWithCommunityDTO(Long id,
                                           CommunityResponse.CommunityDTO community,
                                           String content, LocalDateTime createdAt,
                                           Boolean isHide,
                                           Boolean isLike,
                                           Long likeCount,
                                           Long commentCount,
                                           List<PostImageResponse.ImageDTO> images,
                                           FanResponse.FanDTO writer,
                                           FanResponse.FanDTO user,
                                           VoteResponse.VoteDTO vote
    ) {
        public PostInfoWithCommunityDTO(Post post, Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, Fan fan, Team team, VoteResponse.VoteDTO vote) {
            this(post.getId(), new CommunityResponse.CommunityDTO(team, null, null), post.getContent(), post.getCreatedAt(), post.getIsHide(), isLike, likeCount, commentCount, images, new FanResponse.FanDTO(post.getWriter()), new FanResponse.FanDTO(fan), vote);
        }

        public PostInfoWithCommunityDTO(Post post, Boolean isLike, Long likeCount, Long commentCount, List<PostImageResponse.ImageDTO> images, Fan fan, Player player, VoteResponse.VoteDTO vote) {
            this(post.getId(), new CommunityResponse.CommunityDTO(player, null, null), post.getContent(), post.getCreatedAt(), post.getIsHide(), isLike, likeCount, commentCount, images, new FanResponse.FanDTO(post.getWriter()), new FanResponse.FanDTO(fan), vote);
        }
    }

    public record PostListDTO(List<PostInfoWithCommunityDTO> posts, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public PostListDTO(Page<?> page, List<PostInfoWithCommunityDTO> posts) {
            this(posts, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }

    }
    public record LikeResponseDTO(Boolean isLike, Long likeCount) { }
}
