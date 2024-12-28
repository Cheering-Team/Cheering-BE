package com.cheering.cheer;

import com.cheering.comment.Comment;
import com.cheering.comment.CommentResponse;
import com.cheering.fan.FanResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class CheerResponse {
    public record CheerListDTO(List<CheerDTO> cheers, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last, boolean hasNext) {
        public CheerListDTO(Page<?> page, List<CheerDTO> cheers) {
            this(cheers, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast(), page.hasNext());
        }
    }

    public record CheerDTO(Long id, String content, LocalDateTime createdAt, FanResponse.FanDTO writer, Boolean isWriter, Boolean isLike, Long likeCount) {
        public CheerDTO(Cheer cheer, Boolean isWriter, Boolean isLike, Long likeCount) {
            this(cheer.getId(), cheer.getContent(), cheer.getCreatedAt(), new FanResponse.FanDTO(cheer.getWriter()), isWriter, isLike, likeCount);
        }
    }
}
