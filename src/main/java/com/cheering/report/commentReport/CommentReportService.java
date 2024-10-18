package com.cheering.report.commentReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentReportService {
    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private final FanRepository fanRepository;

    // 댓글 신고
    @Transactional
    public void reportComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityAndUser(comment.getWriter().getCommunity(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<CommentReport> commentReport = commentReportRepository.findByCommentAndWriter(comment, curFan);

        if(commentReport.isPresent()) {
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        CommentReport newCommentReport = CommentReport.builder()
                .comment(comment)
                .writer(curFan)
                .userId(comment.getWriter().getUser().getId())
                .reportContent(comment.getContent())
                .build();

        commentReportRepository.save(newCommentReport);

        Long reportCount = commentReportRepository.countByComment(comment);

        if(reportCount >= 3 && !comment.getIsHide()) {
            comment.setIsHide(true);
        }
    }
}
