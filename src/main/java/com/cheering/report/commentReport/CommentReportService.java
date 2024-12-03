package com.cheering.report.commentReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.post.PostRepository;
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
    private final PostRepository postRepository;
    private final FanRepository fanRepository;

    // 댓글 신고
    @Transactional
    public void reportComment(Long postId, Long commentId, User user) {
        postRepository.findById(postId).orElseThrow(()-> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(comment.getPost().getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<CommentReport> commentReport = commentReportRepository.findByCommentAndWriter(comment, curFan);

        if(commentReport.isPresent()) {
            return;
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
