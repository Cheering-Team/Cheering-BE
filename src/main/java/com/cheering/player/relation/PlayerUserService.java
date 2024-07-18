package com.cheering.player.relation;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.post.Like.Like;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.Post;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostImage.PostImageResponse;
import com.cheering.post.PostRepository;
import com.cheering.post.PostResponse;
import com.cheering.post.Tag.Tag;
import com.cheering.post.Tag.TagRepository;
import com.cheering.post.relation.PostTag;
import com.cheering.post.relation.PostTagRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerUserService {
    private final PlayerUserRepository playerUserRepository;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final PostImageRepository postImageRepository;

    public PlayerUserResponse.ProfileDTO getPlayerUserInfo(Long playerUserId, User user) {
        // 유저
        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        // 현 접속자
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerUser.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        PlayerUserResponse.PlayerUserDTO playerUserDTO = new PlayerUserResponse.PlayerUserDTO(playerUser);

        return new PlayerUserResponse.ProfileDTO(playerUserDTO, playerUser.equals(curPlayerUser), playerUser.getPlayer().getId());
    }

    public PostResponse.PostListDTO getPlayerUserPosts(Long playerUserId, Pageable pageable, User user) {
        // 유저
        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        // 현재 접속 유저
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerUser.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        // 유저의 글 목록
        Page<Post> postList = postRepository.findByPlayerUser(playerUser, pageable);

        List<PostResponse.PostInfoDTO> postInfoDTOS = postList.stream().map((post -> {
            PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(playerUser);

            // 태그
            List<PostTag> postTags = postTagRepository.findByPostId(post.getId());
            List<String> tags = postTags.stream().map((postTag) -> {
                Tag tag = tagRepository.findById(postTag.getTag().getId()).orElseThrow(()-> new CustomException(ExceptionCode.TAG_NOT_FOUND));

                return tag.getName();
            }).toList();

            // 좋아요
            Optional<Like> like = likeRepository.findByPostIdAndPlayerUserId(post.getId(), curPlayerUser.getId());
            Long likeCount = likeRepository.countByPostId(post.getId());

            // 댓글
            Long commentCount = commentRepository.countByPostId(post.getId()) + reCommentRepository.countByPostId(post.getId());

            // 이미지
            List<PostImage> postImages = postImageRepository.findByPostId(post.getId());
            List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

            return new PostResponse.PostInfoDTO(post.getId(), playerUser.getUser().getId().equals(user.getId()), post.getContent(), post.getCreatedAt(), tags, like.isPresent(), likeCount, commentCount, imageDTOS, writerDTO);
        })).toList();

        return new PostResponse.PostListDTO(postList, postInfoDTOS);
    }
}
