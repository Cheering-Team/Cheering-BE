package com.cheering.report.reCommentReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.comment.reComment.ReComment;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanRepository;
import com.cheering.post.PostRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReCommentReportService {
    private final ReCommentReportRepository reCommentReportRepository;
    private final PostRepository postRepository;
    private final ReCommentRepository reCommentRepository;
    private final FanRepository fanRepository;

    // 답글 신고
    @Transactional
    public void reportReComment(Long postId, Long reCommentId, User user) {
        postRepository.findById(postId).orElseThrow(()-> new CustomException(ExceptionCode.POST_NOT_FOUND));

        ReComment reComment = reCommentRepository.findById(reCommentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityAndUser(reComment.getWriter().getCommunity(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<ReCommentReport> reCommentReport = reCommentReportRepository.findByReCommentAndWriter(reComment, curFan);

        if(reCommentReport.isPresent()) {
            return;
        }

        ReCommentReport newReCommentReport = ReCommentReport.builder()
                .reComment(reComment)
                .writer(curFan)
                .userId(reComment.getWriter().getUser().getId())
                .reportContent(reComment.getContent())
                .build();

        reCommentReportRepository.save(newReCommentReport);

        Long reportCount = reCommentReportRepository.countByReComment(reComment);

        if(reportCount >= 3 && !reComment.getIsHide()) {
            reComment.setIsHide(true);
        }
    }
}
