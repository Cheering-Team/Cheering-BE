package com.cheering.domain.comment.service;

import com.cheering.domain.comment.domain.Comment;
import com.cheering.domain.comment.dto.response.CommentResponse;
import com.cheering.domain.comment.repository.CommentRepository;
import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.community.repository.CommunityRepository;
import com.cheering.domain.community.repository.UserCommunityInfoRepository;
import com.cheering.domain.post.domain.Post;
import com.cheering.domain.post.repository.PostRepository;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.repository.UserRepository;
import com.cheering.global.exception.community.NotFoundCommunityException;
import com.cheering.global.exception.community.NotFoundUserCommunityInfoException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.post.NotFoundPostException;
import com.cheering.global.exception.user.NotFoundUserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public Long createComment(Long communityId, Long postId, String content) {
        User loginUser = getLoginUser();

        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        UserCommunityInfo findUserCommunityInfo = userCommunityInfoRepository.findByUserAndCommunity(loginUser,
                        findCommunity)
                .orElseThrow(() -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));

        Comment newComment = Comment.builder()
                .content(content)
                .post(findPost)
                .content(content)
                .writerInfo(findUserCommunityInfo)
                .build();

        commentRepository.save(newComment);

        return newComment.getId();
    }

    public List<CommentResponse> getComments(Long communityId, Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException(ExceptionMessage.NOT_FOUND_POST));
        List<Comment> findComments = commentRepository.findCommentsByPost(findPost);

        //todo: 추후 reCommentCount 계산 로직 구현 필요

        return CommentResponse.ofList(findComments);
    }

    private User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userRepository.findById(Long.valueOf(loginId))
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }
}
