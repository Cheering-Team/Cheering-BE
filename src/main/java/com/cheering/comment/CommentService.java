package com.cheering.comment;

import com.cheering._core.errors.*;
import com.cheering.comment.reComment.ReComment;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.notification.Notification;
import com.cheering.notification.NotificationRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.post.PostResponse;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.User;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final PlayerUserRepository playerUserRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public CommentResponse.CommentIdDTO writeComment(Long postId, CommentRequest.WriteCommentDTO requestDTO, User user) {
        String content = requestDTO.content();

        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser curplayerUser = playerUserRepository.findByPlayerIdAndUserId(post.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(content)
                .playerUser(curplayerUser)
                .post(post)
                .build();

        commentRepository.save(comment);

        if(!post.getPlayerUser().equals(curplayerUser)) {
            Notification notification = new Notification("COMMENT", post.getPlayerUser(), curplayerUser, post, comment);

            notificationRepository.save(notification);
        }

        return new CommentResponse.CommentIdDTO(comment.getId());
    }

    // 특정 게시글 댓글 목록 불러오기 (무한 스크롤)
    @Transactional
    public CommentResponse.CommentListDTO getComments(Long postId, Pageable pageable, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(post.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Page<Comment> commentList = commentRepository.findByPostId(postId, pageable);

        List<CommentResponse.CommentDTO> commentDTOS = commentList.stream().map((comment -> {
            PlayerUser writer = comment.getPlayerUser();
            Long reCount = reCommentRepository.countByCommentId(comment.getId());
            PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(writer);
            return new CommentResponse.CommentDTO(comment, reCount, writerDTO, writer.equals(curPlayerUser));
        })).toList();

        return new CommentResponse.CommentListDTO(commentList, commentDTOS);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        PlayerUser writer = comment.getPlayerUser();

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(writer.getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        if(!writer.equals(curPlayerUser)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByCommentId(commentId);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }
        notificationRepository.deleteByCommentId(commentId);
        reCommentRepository.deleteByComment(comment);

        List<CommentReport> commentReports = commentReportRepository.findByComment(comment);
        for(CommentReport commentReport : commentReports) {
            commentReport.setComment(null);
        }
        commentRepository.delete(comment);
    }
}
