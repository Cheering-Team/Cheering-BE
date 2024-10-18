package com.cheering.comment;

import com.cheering._core.errors.*;
import com.cheering.badword.BadWordService;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.community.relation.FanResponse;
import com.cheering.notification.Fcm.FcmServiceImpl;
import com.cheering.notification.NotificaitonType;
import com.cheering.notification.Notification;
import com.cheering.notification.NotificationRepository;
import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.post.PostResponse;
import com.cheering.report.block.BlockRepository;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.User;

import java.util.List;
import java.util.Optional;

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
        if(badWordService.containsBadWords(requestDTO.content())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        String content = requestDTO.content();

        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityAndUser(post.getWriter().getCommunity(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(content)
                .writer(curFan)
                .post(post)
                .build();

        commentRepository.save(comment);

        if(!post.getWriter().equals(curFan) && blockRepository.findByFromAndTo(post.getWriter(), curFan).isEmpty()) {
            Notification notification = new Notification(NotificaitonType.COMMENT, post.getWriter(), curFan, post, comment);

            notificationRepository.save(notification);
            if(notification.getTo().getUser().getDeviceToken() != null) {
                fcmService.sendMessageTo(notification.getTo().getUser().getDeviceToken(), "댓글", comment.getWriter().getName() + "님이 댓글을 남겼습니다:\"" + comment.getContent() + "\"", postId, notification.getId());
            }

        }
        return new CommentResponse.CommentIdDTO(comment.getId());
    }

    // 특정 게시글 댓글 목록 불러오기 (무한 스크롤)
    @Transactional
    public CommentResponse.CommentListDTO getComments(Long postId, Pageable pageable, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Fan curFan = fanRepository.findByCommunityAndUser(post.getWriter().getCommunity(), user).orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

        Page<Comment> commentList = commentRepository.findByPost(post, curFan, pageable);

        List<CommentResponse.CommentDTO> commentDTOS = commentList.stream().map((comment -> {
            Fan writer = comment.getWriter();
            Long reCount = reCommentRepository.countByCommentId(comment.getId());
            return new CommentResponse.CommentDTO(comment, reCount, writer.equals(curFan));
        })).toList();

        return new CommentResponse.CommentListDTO(commentList, commentDTOS);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Fan writer = comment.getWriter();

        Fan curFan = fanRepository.findByCommunityAndUser(writer.getCommunity(), user).orElseThrow(() -> new CustomException(ExceptionCode.FAN_NOT_FOUND));

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
