package com.cheering.comment.reComment;

import com.cheering._core.errors.*;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.notification.Notification;
import com.cheering.notification.NotificationRepository;
import com.cheering.player.Player;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Post;
import com.cheering.post.PostResponse;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReCommentService {
    private final ReCommentRepository reCommentRepository;
    private final CommentRepository commentRepository;
    private final PlayerUserRepository playerUserRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public ReCommentResponse.ReCommentIdDTO writeReComment(Long commentId, ReCommentRequest.WriteReCommentDTO requestDTO, User user) {
        String content = requestDTO.content();
        Long toId = requestDTO.toId();

        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Long playerId = comment.getPost().getPlayerUser().getPlayer().getId();

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId()).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        PlayerUser toPlayerUser = playerUserRepository.findById(toId).orElseThrow(()->new CustomException(ExceptionCode.COMMENT_WRITER_NOT_FOUND));



        ReComment reComment = ReComment.builder()
                .content(content)
                .comment(comment)
                .playerUser(curPlayerUser)
                .toPlayerUser(toPlayerUser)
                .build();

        reCommentRepository.save(reComment);

        if(!toPlayerUser.equals(curPlayerUser)) {
            Notification notification = new Notification("RECOMMENT", toPlayerUser, curPlayerUser, comment.getPost(), reComment);
            notificationRepository.save(notification);
        }

        return new ReCommentResponse.ReCommentIdDTO(reComment.getId());
    }

    public ReCommentResponse.ReCommentListDTO getReComments(Long commentId, User user) {
        List<ReComment> reCommentList = reCommentRepository.findByCommentId(commentId);

        Player player = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND)).getPlayerUser().getPlayer();

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(player.getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        List<ReCommentResponse.ReCommentDTO> reCommentDTOS = reCommentList.stream().map((reComment -> {
            PlayerUser writer = reComment.getPlayerUser();
            PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(writer);

            PostResponse.WriterDTO toDTO = new PostResponse.WriterDTO(reComment.getToPlayerUser());

            return new ReCommentResponse.ReCommentDTO(reComment, toDTO, writerDTO, writer.equals(curPlayerUser));
        })).toList();

        return new ReCommentResponse.ReCommentListDTO(reCommentDTOS);
    }

    // 답글 삭제
    @Transactional
    public void deleteReComment(Long reCommentId, User user) {
        ReComment reComment = reCommentRepository.findById(reCommentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        PlayerUser writer = reComment.getPlayerUser();

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(writer.getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        if(!writer.equals(curPlayerUser)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByReComment(reComment);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }

        reCommentRepository.delete(reComment);
    }
}
