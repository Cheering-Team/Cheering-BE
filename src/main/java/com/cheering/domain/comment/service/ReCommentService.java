package com.cheering.domain.comment.service;

import com.cheering.domain.comment.domain.Comment;
import com.cheering.domain.comment.domain.ReComment;
import com.cheering.domain.comment.repository.CommentRepository;
import com.cheering.domain.comment.repository.ReCommentRepository;
import com.cheering.domain.community.domain.Community;
import com.cheering.domain.community.domain.UserCommunityInfo;
import com.cheering.domain.community.repository.UserCommunityInfoRepository;
import com.cheering.domain.user.domain.User;
import com.cheering.domain.user.repository.UserRepository;
import com.cheering.global.exception.comment.NotFoundCommentException;
import com.cheering.global.exception.community.NotFoundUserCommunityInfoException;
import com.cheering.global.exception.constant.ExceptionMessage;
import com.cheering.global.exception.user.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReCommentService {
    private final ReCommentRepository reCommentRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserCommunityInfoRepository userCommunityInfoRepository;

    @Transactional
    public Long createReComment(Long communityId, Long postId, Long commentId, String content) {
        User loginUser = getLoginUser();

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException(ExceptionMessage.NOT_FOUND_COMMENT));

        Community commentCommunity = findComment.getWriterInfo().getCommunity();

        UserCommunityInfo findUserCommunityInfo = userCommunityInfoRepository.findByUserAndCommunity(loginUser,
                        commentCommunity)
                .orElseThrow(() -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));

        ReComment newReComment = ReComment.builder()
                .comment(findComment)
                .content(content)
                .writerInfo(findUserCommunityInfo)
                .build();

        reCommentRepository.save(newReComment);
        
        return newReComment.getId();
    }

    private User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userRepository.findById(Long.valueOf(loginId))
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }
}
