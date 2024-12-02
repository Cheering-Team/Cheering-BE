package com.cheering.comment;

import com.cheering._core.errors.*;
import com.cheering.badword.BadWordService;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.notification.NotificaitonType;
import com.cheering.notification.Notification;
import com.cheering.notification.NotificationRepository;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.report.block.BlockRepository;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cheering.user.deviceToken.DeviceToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final FanRepository fanRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final NotificationRepository notificationRepository;
    private final BlockRepository blockRepository;
    private final BadWordService badWordService;
    private final FcmServiceImpl fcmService;

    // 댓글 작성
    @Transactional
    public CommentResponse.CommentIdDTO writeComment(Long postId, CommentRequest.WriteCommentDTO requestDTO, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        if(badWordService.containsBadWords(requestDTO.content())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        String content = requestDTO.content();

        Fan curFan = fanRepository.findByCommunityIdAndUser(post.getWriter().getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(content)
                .writer(curFan)
                .post(post)
                .build();

        commentRepository.save(comment);

        if(!post.getWriter().equals(curFan) && blockRepository.findByFromAndTo(post.getWriter(), curFan).isEmpty()) {
            Notification notification = new Notification(NotificaitonType.COMMENT, post.getWriter(), curFan, post, comment);

            notificationRepository.save(notification);
            for(DeviceToken deviceToken: notification.getTo().getUser().getDeviceTokens()){
                fcmService.sendPostMessageTo(deviceToken.getToken(), "댓글", comment.getWriter().getName() + "님이 댓글을 남겼습니다:\"" + comment.getContent() + "\"", postId, notification.getId());
            }

        }
        return new CommentResponse.CommentIdDTO(comment.getId());
    }

    // 특정 게시글 댓글 목록 불러오기 (무한 스크롤)
    @Transactional
    public CommentResponse.CommentListDTO getComments(Long postId, Pageable pageable, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityIdAndUser(post.getWriter().getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Page<Comment> commentList = commentRepository.findByPost(post, curFan, pageable);

        List<CommentResponse.CommentDTO> myComments = new ArrayList<>();
        List<CommentResponse.CommentDTO> otherComments = new ArrayList<>();

        for (Comment comment : commentList) {
            Fan writer = comment.getWriter();
            Long reCount = reCommentRepository.countByCommentId(comment.getId());
            CommentResponse.CommentDTO commentDTO = new CommentResponse.CommentDTO(comment, reCount, writer.equals(curFan));

            if (writer.equals(curFan)) {
                myComments.add(commentDTO);
            } else {
                otherComments.add(commentDTO);
            }
        }
        Collections.reverse(myComments);

        List<CommentResponse.CommentDTO> combinedComments = new ArrayList<>();
        combinedComments.addAll(myComments);
        combinedComments.addAll(otherComments);

        return new CommentResponse.CommentListDTO(commentList, combinedComments);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Fan writer = comment.getWriter();

        Fan curFan = fanRepository.findByCommunityIdAndUser(writer.getCommunityId(), user).orElseThrow(() -> new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(!writer.equals(curFan)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByComment(comment);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }

        List<CommentReport> commentReports = commentReportRepository.findByComment(comment);
        for(CommentReport commentReport : commentReports) {
            commentReport.setComment(null);
        }

        commentRepository.delete(comment);
    }

    public CommentResponse.CommentDTO getRandomComment(Long postId, User user) {
        Optional<Comment> comment = commentRepository.findRandomComment(postId);

        return comment.map(value -> new CommentResponse.CommentDTO(value, null, null)).orElse(null);
    }
}
