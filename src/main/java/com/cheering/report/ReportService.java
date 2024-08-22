package com.cheering.report;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final PlayerUserRepository playerUserRepository;

    // 게시물 신고
    @Transactional
    public void postReport(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser curUser = playerUserRepository.findByPlayerIdAndUserId(post.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Optional<Report> report = reportRepository.findByPostIdAndPlayerUserId(postId, curUser.getId());

        if(report.isPresent()) {
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        Report newReport = Report.builder()
                .post(post)
                .playerUser(curUser)
                .build();

        reportRepository.save(newReport);

        Long reportCount = reportRepository.countByPostId(postId);

        if(reportCount >= 3 && !post.getIsHide()) {
            post.setIsHide(true);
        }
    }
}
