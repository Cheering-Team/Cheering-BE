package com.cheering.report.postReport;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
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
    private final FanRepository fanRepository;

    // 게시물 신고
    @Transactional
    public void reportPost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(post.getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Optional<PostReport> report = postReportRepository.findByPostAndWriter(post, curFan);

        if(report.isPresent()) {
            return;
        }

        PostReport newPostReport = PostReport.builder()
                .post(post)
                .writer(curFan)
                .userId(post.getWriter().getUser().getId())
                .reportContent(post.getContent())
                .build();

        postReportRepository.save(newPostReport);

        Long reportCount = postReportRepository.countByPost(post);

        if(reportCount >= 3 && !post.getIsHide()) {
            post.setIsHide(true);
        }
    }
}
