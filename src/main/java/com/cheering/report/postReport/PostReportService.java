package com.cheering.report.postReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReportService {
    private final PostReportRepository postReportRepository;
    private final PostRepository postRepository;
    private final PlayerUserRepository playerUserRepository;

    // 게시물 신고
    @Transactional
    public void reportPost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser curUser = playerUserRepository.findByPlayerIdAndUserId(post.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        Optional<PostReport> report = postReportRepository.findByPostIdAndPlayerUserId(postId, curUser.getId());

        if(report.isPresent()) {
            throw new CustomException(ExceptionCode.ALREADY_REPORT);
        }

        PostReport newPostReport = PostReport.builder()
                .post(post)
                .playerUser(curUser)
                .userId(post.getPlayerUser().getUser().getId())
                .reportContent(post.getContent())
                .build();

        postReportRepository.save(newPostReport);

        Long reportCount = postReportRepository.countByPostId(postId);

        if(reportCount >= 3 && !post.getIsHide()) {
            post.setIsHide(true);
        }
    }
}
