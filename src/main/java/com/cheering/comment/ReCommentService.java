package com.cheering.comment;

import com.cheering.community.Community;
import com.cheering.community.UserCommunityInfo;
import com.cheering.community.CommunityRepository;
import com.cheering.community.UserCommunityInfoRepository;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
import com.cheering._core.errors.NotFoundCommentException;
import com.cheering._core.errors.NotFoundCommunityException;
import com.cheering._core.errors.NotFoundUserCommunityInfoException;
import com.cheering._core.errors.ExceptionMessage;
import com.cheering._core.errors.NotFoundUserException;
import java.util.List;
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
    private final CommunityRepository communityRepository;

    @Transactional
    public Long createReComment(Long communityId, Long postId, Long commentId, String content) {
        User loginUser = getLoginUser();

        Community findCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new NotFoundCommunityException(ExceptionMessage.NOT_FOUND_COMMUNITY));

        UserCommunityInfo findUserCommunityInfo = userCommunityInfoRepository.findByUserAndCommunity(loginUser,
                        findCommunity)
                .orElseThrow(() -> new NotFoundUserCommunityInfoException(ExceptionMessage.NOT_FOUND_COMMUNITY_INFO));

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException(ExceptionMessage.NOT_FOUND_COMMENT));

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

    public List<ReCommentResponse> getReComments(Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException(ExceptionMessage.NOT_FOUND_COMMENT));

        List<ReComment> findReComments = reCommentRepository.findByComment(findComment);

        return ReCommentResponse.ofList(findReComments);
    }
}
