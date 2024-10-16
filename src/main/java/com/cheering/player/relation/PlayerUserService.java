package com.cheering.player.relation;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.player.PlayerResponse;
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
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.postReport.PostReport;
import com.cheering.report.postReport.PostReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.user.User;
import com.cheering.user.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final BadWordService badWordService;
    private final S3Util s3Util;

    public PlayerUserResponse.ProfileDTO getPlayerUserInfo(Long playerUserId, User user) {
        // 유저
        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        // 현 접속자
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerUser.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        PlayerUserResponse.PlayerUserDTO playerUserDTO = new PlayerUserResponse.PlayerUserDTO(playerUser);

        PlayerResponse.PlayerDTO playerDTO = new PlayerResponse.PlayerDTO(playerUser.getPlayer());

        return new PlayerUserResponse.ProfileDTO(playerUserDTO, playerUser.equals(curPlayerUser), playerDTO);
    }

    public PostResponse.PostListDTO getPlayerUserPosts(Long playerUserId, Pageable pageable, User user) {
        // 유저
        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        // 현재 접속 유저
        PlayerUser curPlayerUser = playerUserRepository.findByPlayerIdAndUserId(playerUser.getPlayer().getId(), user.getId()).orElseThrow(()->new CustomException(ExceptionCode.CUR_PLAYER_USER_NOT_FOUND));

        // 유저의 글 목록
        Page<Post> postList = postRepository.findByPlayerUser(playerUser, curPlayerUser, pageable);

        List<PostResponse.PostInfoWithPlayerDTO> postInfoDTOS = postList.stream().map((post -> {
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

            return new PostResponse.PostInfoWithPlayerDTO(post, tags, like.isPresent(), likeCount, commentCount, imageDTOS, curPlayerUser);
        })).toList();

        return new PostResponse.PostListDTO(postList, postInfoDTOS);
    }

    public void updatePlayerUserImage(Long playerUserId, MultipartFile image) {
        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        String imageUrl = "";
        if(image == null) {
            imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-profile.jpg";
        } else {
            imageUrl = s3Util.upload(image);
        }

        playerUser.setImage(imageUrl);
        playerUserRepository.save(playerUser);
    }

    public void updatePlayerUserNickname(Long playerUserId, UserRequest.NicknameDTO requestDTO) {
        if(badWordService.containsBadWords(requestDTO.nickname())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }
        String nickname = requestDTO.nickname();

        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        if(playerUser.getPlayer().getKoreanName().equals(nickname) || playerUser.getPlayer().getEnglishName().equals(nickname)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Optional<PlayerUser> duplicatePlayerUser = playerUserRepository.findByPlayerIdAndNickname(playerUser.getPlayer().getId(), nickname);

        if(duplicatePlayerUser.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NICKNAME);
        }

        playerUser.setNickname(requestDTO.nickname());
        playerUserRepository.save(playerUser);
    }

    // 커뮤니티 탈퇴
    @Transactional
    public void deletePlayerUser(Long playerUserId) {
        PlayerUser playerUser = playerUserRepository.findById(playerUserId).orElseThrow(()->new CustomException(ExceptionCode.PLAYER_USER_NOT_FOUND));

        if(playerUser.getMyCommunity() != null) {
            playerUser.getMyCommunity().setOwner(null);
        }

        List<PostImage> postImages = postImageRepository.findByPlayerUser(playerUser);

        for(PostImage postImage : postImages) {
            s3Util.deleteImageFromS3(postImage.getPath());
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByWriter(playerUser);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }

        List<CommentReport> commentReports = commentReportRepository.findByWriter(playerUser);
        for(CommentReport commentReport : commentReports) {
            commentReport.setComment(null);
        }

        List<PostReport> postReports = postReportRepository.findByPlayerUser(playerUser);
        for(PostReport postReport : postReports) {
            postReport.setPost(null);
        }

        playerUserRepository.deleteById(playerUserId);
    }
}
