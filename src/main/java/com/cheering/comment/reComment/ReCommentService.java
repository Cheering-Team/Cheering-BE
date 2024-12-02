package com.cheering.comment.reComment;

import com.cheering._core.errors.*;
import com.cheering.badword.BadWordService;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.player.PlayerRepository;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.notification.NotificaitonType;
import com.cheering.notification.Notification;
import com.cheering.notification.NotificationRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.post.PostRepository;
import com.cheering.report.block.BlockRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.User;

import com.cheering.user.deviceToken.DeviceToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReCommentService {
    private final ReCommentRepository reCommentRepository;
    private final CommentRepository commentRepository;
    private final FanRepository fanRepository;
    private final PlayerRepository playerRepository;
    private final PostRepository postRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final NotificationRepository notificationRepository;
    private final BlockRepository blockRepository;
    private final BadWordService badWordService;
    private final FcmServiceImpl fcmService;

    @Transactional
    public ReCommentResponse.ReCommentIdDTO writeReComment(Long postId, Long commentId, ReCommentRequest.WriteReCommentDTO requestDTO, User user) {
        String content = requestDTO.content();
        Long toId = requestDTO.toId();

        postRepository.findById(postId).orElseThrow(()-> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        if(badWordService.containsBadWords(requestDTO.content())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Fan curFan = fanRepository.findByCommunityIdAndUser(comment.getWriter().getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Fan toFan = fanRepository.findById(toId).orElseThrow(()->new CustomException(ExceptionCode.COMMENT_WRITER_NOT_FOUND));

        ReComment reComment = ReComment.builder()
                .content(content)
                .comment(comment)
                .writer(curFan)
                .to(toFan)
                .build();

        reCommentRepository.save(reComment);

        if(!toFan.equals(curFan) && blockRepository.findByFromAndTo(toFan, curFan).isEmpty()) {
            Notification notification = new Notification(NotificaitonType.RECOMMNET, toFan, curFan, comment.getPost(), reComment);
            notificationRepository.save(notification);
            for(DeviceToken deviceToken: notification.getTo().getUser().getDeviceTokens()){
                fcmService.sendPostMessageTo(deviceToken.getToken(), curFan.getName(), "회원님의 댓글에 답글을 남겼습니다:\"" + reComment.getContent() + "\"", comment.getPost().getId(), notification.getId());
            }
        }

        return new ReCommentResponse.ReCommentIdDTO(reComment.getId());
    }

    public List<ReCommentResponse.ReCommentDTO> getReComments(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(comment.getWriter().getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        List<ReComment> reCommentList = reCommentRepository.findByComment(comment, curFan);

        return reCommentList.stream().map((reComment -> {
            Fan writer = reComment.getWriter();
            return new ReCommentResponse.ReCommentDTO(reComment, writer.equals(curFan));
        })).toList();
    }

    // 답글 삭제
    @Transactional
    public void deleteReComment(Long reCommentId, User user) {
        ReComment reComment = reCommentRepository.findById(reCommentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Fan writer = reComment.getWriter();

        Fan curFan = fanRepository.findByCommunityIdAndUser(writer.getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(!writer.equals(curFan)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByReComment(reComment);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }

        reCommentRepository.delete(reComment);
    }
}
