package com.cheering.report.reCommentReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.comment.reComment.ReComment;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReCommentReportService {
    private final ReCommentReportRepository reCommentReportRepository;
    private final ReCommentRepository reCommentRepository;
    private final PlayerUserRepository playerUserRepository;

    // 답글 신고
    @Transactional
    public void reportReComment(Long reCommentId, User user) {
        ReComment reComment = reCommentRepository.findById(reCommentId).orElseThrow(() -> new CustomException(ExceptionCode.RECOMMENT_NOT_FOUND));

        PlayerUser curUser = playerUserRepository.findByPlayerIdAndUserId(reComment.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Optional<ReCommentReport> reCommentReport = reCommentReportRepository.findByReCommentIdAndPlayerUserId(reCommentId, curUser.getId());

        if(reCommentReport.isPresent()) {
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        ReCommentReport newReCommentReport = ReCommentReport.builder()
                .reComment(reComment)
                .playerUser(curUser)
                .build();

        reCommentReportRepository.save(newReCommentReport);

        Long reportCount = reCommentReportRepository.countByReCommentId(reCommentId);

        if(reportCount >= 3 && !reComment.getIsHide()) {
            reComment.setIsHide(true);
        }
    }
}
