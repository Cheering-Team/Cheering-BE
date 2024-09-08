package com.cheering.report.commentReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
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
    private final PlayerUserRepository playerUserRepository;

    // 댓글 신고
    @Transactional
    public void reportComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        PlayerUser curUser = playerUserRepository.findByPlayerIdAndUserId(comment.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        Optional<CommentReport> commentReport = commentReportRepository.findByCommentIdAndPlayerUserId(commentId, curUser.getId());

        if(commentReport.isPresent()) {
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        CommentReport newCommentReport = CommentReport.builder()
                .comment(comment)
                .playerUser(curUser)
                .userId(comment.getPlayerUser().getUser().getId())
                .reportContent(comment.getContent())
                .build();

        commentReportRepository.save(newCommentReport);

        Long reportCount = commentReportRepository.countByCommentId(commentId);

        if(reportCount >= 3 && !comment.getIsHide()) {
            comment.setIsHide(true);
        }
    }
}
