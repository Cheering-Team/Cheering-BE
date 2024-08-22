package com.cheering.post;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.comment.Comment;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReComment;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.player.Player;
import com.cheering.player.PlayerResponse;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.player.relation.PlayerUserResponse;
import com.cheering.post.Like.Like;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostImage.PostImageResponse;
import com.cheering.post.Tag.Tag;
import com.cheering.post.Tag.TagRepository;
import com.cheering.post.relation.PostTag;
import com.cheering.post.relation.PostTagRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PlayerUserRepository playerUserRepository;
    private final PostImageRepository postImageRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final S3Util s3Util;

    @Transactional
    public PostResponse.PostIdDTO writePost(Long playerId, String content, List<MultipartFile> images, List<String> tags, User user) {
        PlayerUser playerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Post post = Post.builder()
                .content(content)
                .playerUser(playerUser)
                .build();

        postRepository.save(post);

        if(tags != null) {
            tags.forEach((tagName) -> {
                Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new CustomException(ExceptionCode.TAG_NOT_FOUND));

                PostTag postTag = PostTag.builder()
                        .post(post)
                        .tag(tag)
                        .build();

                postTagRepository.save(postTag);
            });
        }

        if(images != null ){
            images.forEach((image)->{
                try {
                    String imageUrl = s3Util.upload(image);
                    BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();

                    PostImage postImage = PostImage.builder()
                            .path(imageUrl)
                            .width(width)
                            .height(height)
                            .post(post)
                            .build();

                    postImageRepository.save(postImage);
                } catch (IOException e) {
                    throw new CustomException(ExceptionCode.IMAGE_UPLOAD_FAILED);
                }
            });
        }
        return new PostResponse.PostIdDTO(post.getId(), new PlayerUserResponse.PlayerUserDTO(playerUser));
    }


    public PostResponse.PostListDTO getPosts(Long playerId, String tagName, Pageable pageable, User user) {
        Page<Post> postList;

        if(tagName.isEmpty()) {
            postList = postRepository.findByPlayerId(playerId, pageable);
        } else if(tagName.equals("hot")) {
            postList = postRepository.findHotPosts(playerId, pageable);
        } else {
            postList = postRepository.findByPlayerIdAndTagName(playerId, tagName, pageable);
        }

        List<PostResponse.PostInfoWithPlayerDTO> postInfoDTOS = postList.getContent().stream().map((post -> {
            // 작성자
            PlayerUser playerUser = post.getPlayerUser();
            PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(playerUser);

            Player player = playerUser.getPlayer();

            // 태그
            List<PostTag> postTags = postTagRepository.findByPostId(post.getId());
            List<String> tags = postTags.stream().map((postTag) -> {
                Tag tag = tagRepository.findById(postTag.getTag().getId()).orElseThrow(()-> new CustomException(ExceptionCode.TAG_NOT_FOUND));

                return tag.getName();
            }).toList();

            PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerUser.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

            Optional<Like> like = likeRepository.findByPostIdAndPlayerUserId(post.getId(), curPlayerUser.getId());
            Long likeCount = likeRepository.countByPostId(post.getId());

            Long commentCount = commentRepository.countByPostId(post.getId()) + reCommentRepository.countByPostId(post.getId());

            List<PostImage> postImages = postImageRepository.findByPostId(post.getId());
            List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

            return new PostResponse.PostInfoWithPlayerDTO(post.getId(), new PlayerUserResponse.PlayerUserDTO(curPlayerUser), new PlayerResponse.PlayerDTO(player), post.getContent(), false, post.getCreatedAt(), tags, like.isPresent(), likeCount, commentCount, imageDTOS, writerDTO);
        })).toList();

        return new PostResponse.PostListDTO(postList, postInfoDTOS);
    }

    public PostResponse.PostWithPlayerListDTO getPlayersPosts(Pageable pageable, User user) {
        Page<Post> postList;

        List<PlayerUser> playerUsers = playerUserRepository.findByUserId(user.getId());

        List<Long> playerIds = playerUsers.stream().map((playerUser -> playerUser.getPlayer().getId())).toList();

        postList = postRepository.findByPlayerIds(playerIds, pageable);

        List<PostResponse.PostInfoWithPlayerDTO> postInfoWithPlayerDTOS = postList.getContent().stream().map((post -> {
            // 작성자
            PlayerUser playerUser = post.getPlayerUser();
            PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(playerUser);

            // 현 접속자
            PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerUser.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

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

            // 선수
            PlayerResponse.PlayerDTO playerDTO = new PlayerResponse.PlayerDTO(playerUser.getPlayer());

            return new PostResponse.PostInfoWithPlayerDTO(post.getId(), new PlayerUserResponse.PlayerUserDTO(curPlayerUser), playerDTO, post.getContent(), false, post.getCreatedAt(), tags, like.isPresent(), likeCount, commentCount, imageDTOS, writerDTO);
        })).toList();

        return new PostResponse.PostWithPlayerListDTO(postList, postInfoWithPlayerDTOS);
    }

    public PostResponse.PostByIdDTO getPostById(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser playerUser = post.getPlayerUser();

        Player player = playerUser.getPlayer();

        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(player.getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Optional<Like> like = likeRepository.findByPostIdAndPlayerUserId(postId, curPlayerUser.getId());
        Long likeCount = likeRepository.countByPostId(postId);

        Long commentCount = commentRepository.countByPostId(postId) + reCommentRepository.countByPostId(postId);

        List<PostTag> postTags = postTagRepository.findByPostId(postId);

        List<String> tags = postTags.stream().map((postTag) -> {
            Tag tag = tagRepository.findById(postTag.getTag().getId()).orElseThrow(()-> new CustomException(ExceptionCode.TAG_NOT_FOUND));

            return tag.getName();
        }).toList();

        List<PostImage> postImages = postImageRepository.findByPostId(postId);

        List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

        PostResponse.WriterDTO writerDTO = new PostResponse.WriterDTO(playerUser);

        PostResponse.PostInfoWithPlayerDTO postInfoDTO = new PostResponse.PostInfoWithPlayerDTO(post.getId(), new PlayerUserResponse.PlayerUserDTO(curPlayerUser), new PlayerResponse.PlayerDTO(player), post.getContent(), false, post.getCreatedAt(), tags, like.isPresent(), likeCount, commentCount, imageDTOS, writerDTO);
        PlayerResponse.PlayerNameDTO playerNameDTO = new PlayerResponse.PlayerNameDTO(player);

        return new PostResponse.PostByIdDTO(postInfoDTO, playerNameDTO);
    }

    public boolean toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        Player player = post.getPlayerUser().getPlayer();

        PlayerUser playerUser = playerUserRepository.findByPlayerIdAndUserId(player.getId(), user.getId()).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        Optional<Like> like = likeRepository.findByPostIdAndPlayerUserId(postId, playerUser.getId());

        if(like.isEmpty()) {
            Like newLike = Like.builder()
                    .post(post)
                    .playerUser(playerUser)
                    .build();

            likeRepository.save(newLike);

            return true;
        } else {
            likeRepository.deleteById(like.get().getId());

            return false;
        }
    }

    @Transactional
    public void editPost(Long postId, String content, List<MultipartFile> images, List<String> tags, User user) {
        Post post = postRepository.findById(postId).orElseThrow(()->new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser writer = post.getPlayerUser();
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(writer.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        if(!writer.equals(curPlayerUser)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }

        post.setContent(content);
        postRepository.save(post);

        postTagRepository.deleteByPost(post);
        if(tags != null) {
            tags.forEach((tagName) -> {
                Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new CustomException(ExceptionCode.TAG_NOT_FOUND));

                PostTag postTag = PostTag.builder()
                        .post(post)
                        .tag(tag)
                        .build();

                postTagRepository.save(postTag);
            });
        }

        postImageRepository.deleteByPost(post);
        if(images != null ){
            images.forEach((image)->{
                try {
                    String imageUrl = s3Util.upload(image);
                    BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();

                    PostImage postImage = PostImage.builder()
                            .path(imageUrl)
                            .width(width)
                            .height(height)
                            .post(post)
                            .build();

                    postImageRepository.save(postImage);
                } catch (IOException e) {
                    throw new CustomException(ExceptionCode.IMAGE_UPLOAD_FAILED);
                }
            });
        }
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        PlayerUser writer = post.getPlayerUser();
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(writer.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        if(!writer.equals(curPlayerUser)) {
            throw new CustomException(ExceptionCode.NOT_WRITER);
        }


        // Tag
        postTagRepository.deleteByPost(post);

        // Image
        postImageRepository.deleteByPost(post);

        // Comment
        List<Comment> commentList = commentRepository.findByPost(post);
        reCommentRepository.deleteByCommentIn(commentList);
        commentRepository.deleteByPost(post);

        // Like
        likeRepository.deleteByPost(post);

        // Post
        postRepository.deleteById(postId);
    }
}
