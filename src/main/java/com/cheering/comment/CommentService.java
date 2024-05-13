package com.cheering.comment;

import com.cheering.community.Community;
import com.cheering.community.UserCommunityInfo;
import com.cheering.community.CommunityRepository;
import com.cheering.community.UserCommunityInfoRepository;
import com.cheering.post.Post;
import com.cheering.post.PostRepository;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
import com.cheering._core.errors.NotFoundCommunityException;
import com.cheering._core.errors.NotFoundUserCommunityInfoException;
import com.cheering._core.errors.ExceptionMessage;
import com.cheering._core.errors.NotFoundPostException;
import com.cheering._core.errors.NotFoundUserException;
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
        
        return CommentResponse.ofList(findComments);
    }

    private User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        return userRepository.findById(Long.valueOf(loginId))
                .orElseThrow(() -> new NotFoundUserException(ExceptionMessage.NOT_FOUND_USER));
    }
}
