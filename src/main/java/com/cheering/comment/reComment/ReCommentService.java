package com.cheering.comment.reComment;

import com.cheering._core.errors.*;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.user.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReCommentService {
    private final ReCommentRepository reCommentRepository;
    private final CommentRepository commentRepository;
    private final PlayerUserRepository playerUserRepository;



    @Transactional
    public ReCommentResponse.ReCommentIdDTO writeReComment(Long commentId, ReCommentRequest.WriteReCommentDTO requestDTO, User user) {
        String content = requestDTO.content();
        Long toId = requestDTO.toId();

        Comment comment = commentRepository.findById(commentId).orElseThrow(()->new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        Long playerId = comment.getPost().getPlayerUser().getPlayer().getId();

        PlayerUser playerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        PlayerUser toPlayerUser = null;

        if(toId != null) {
            toPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, toId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));
        }

        ReComment reComment = ReComment.builder()
                .content(content)
                .comment(comment)
                .playerUser(playerUser)
                .toPlayerUser(toPlayerUser)
                .build();

        reCommentRepository.save(reComment);

        return new ReCommentResponse.ReCommentIdDTO(reComment.getId());
    }

//    @Transactional
//    public Long createReComment(Long communityId, Long postId, Long commentId, String content) {
//        User loginUser = getLoginUser();
//
//        Community findCommunity = communityRepository.findById(communityId)
//                .orElseThrow(() -> new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));
//
//        UserCommunityInfo findUserCommunityInfo = userCommunityInfoRepository.findByUserAndCommunity(loginUser,
//                        findCommunity)
//                .orElseThrow(() -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));
//
//        Comment findComment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new NotFoundCommentException(ExceptionMessage.NOT_FOUND_COMMENT));
//
//        ReComment newReComment = ReComment.builder()
//                .comment(findComment)
//                .content(content)
//                .writerInfo(findUserCommunityInfo)
//                .build();
//
//        reCommentRepository.save(newReComment);
//
//        return newReComment.getId();
//    }
//
//    private User getLoginUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loginId = authentication.getName();
//        return userRepository.findById(Long.valueOf(loginId))
//                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
//    }
//
//    public List<ReCommentResponse> getReComments(Long commentId) {
//        Comment findComment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new NotFoundCommentException(ExceptionMessage.NOT_FOUND_COMMENT));
//
//        List<ReComment> findReComments = reCommentRepository.findByComment(findComment);
//
//        return ReCommentResponse.ofList(findReComments);
//    }

}
