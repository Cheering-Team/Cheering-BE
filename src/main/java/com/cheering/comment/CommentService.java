package com.cheering.comment;

import com.cheering._core.errors.*;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.post.PostResponse;
import com.cheering.user.User;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final PlayerUserRepository playerUserRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;

    @Transactional
    public CommentResponse.CommentIdDTO writeComment(Long postId, CommentRequest.WriteCommentDTO requestDTO, User user) {
        String content = requestDTO.content();

        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser playerUser = playerUserRepository.findByPlayerIdAndUserId(post.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .content(content)
                .playerUser(playerUser)
                .post(post)
                .build();

        commentRepository.save(comment);

        return new CommentResponse.CommentIdDTO(comment.getId());
    }

    public CommentResponse.CommentListDTO getComments(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(post.getPlayerUser().getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        List<Comment> commentList = commentRepository.findByPostId(postId);

        List<CommentResponse.CommentDTO> commentDTOS = commentList.stream().map((comment -> {
            PlayerUser writer = comment.getPlayerUser();
            Long reCount = reCommentRepository.countByCommentId(comment.getId());
            PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(writer);
            return new CommentResponse.CommentDTO(comment, reCount, writerDTO, writer.equals(curPlayerUser));
        })).toList();

        return new CommentResponse.CommentListDTO(commentDTOS);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        PlayerUser writer = comment.getPlayerUser();

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(writer.getPlayer().getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        if(!writer.equals(curPlayerUser)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        reCommentRepository.deleteByComment(comment);
        commentRepository.delete(comment);
    }
//    @Transactional
//    public Long createComment(Long communityId, Long postId, String content) {
//        User loginUser = getLoginUser();
//
//        Community findCommunity = communityRepository.findById(communityId)
//                .orElseThrow(() -> new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));
//
//        UserCommunityInfo findUserCommunityInfo = userCommunityInfoRepository.findByUserAndCommunity(loginUser,
//                        findCommunity)
//                .orElseThrow(() -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));
//
//        Post findPost = postRepository.findById(postId)
//                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));
//
//        Comment newComment = Comment.builder()
//                .content(content)
//                .post(findPost)
//                .content(content)
//                .writerInfo(findUserCommunityInfo)
//                .build();
//
//        commentRepository.save(newComment);
//
//        return newComment.getId();
//    }
//
//    public List<CommentResponse> getComments(Long communityId, Long postId) {
//        Post findPost = postRepository.findById(postId)
//                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));
//        List<Comment> findComments = commentRepository.findCommentsByPost(findPost);
//
//        return CommentResponse.ofList(findComments);
//    }
//
//    private User getLoginUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loginId = authentication.getName();
//        return userRepository.findById(Long.valueOf(loginId))
//                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
//    }
}
