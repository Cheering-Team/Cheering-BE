package com.cheering.fan;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.comment.CommentRepository;
import com.cheering.comment.reComment.ReCommentRepository;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.post.Like.Like;
import com.cheering.post.Like.LikeRepository;
import com.cheering.post.Post;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.post.PostImage.PostImageResponse;
import com.cheering.post.PostRepository;
import com.cheering.post.PostResponse;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import com.cheering.user.UserRequest;
import com.cheering.vote.VoteResponse;
import com.cheering.vote.VoteService;
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
public class FanService {
    private final FanRepository fanRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final PostImageRepository postImageRepository;
    private final BadWordService badWordService;
    private final S3Util s3Util;
    private final VoteService voteService;

    public FanResponse.FanProfileDTO getFanInfo(Long fanId, User user) {
        // 유저
        Fan fan = fanRepository.findById(fanId).orElseThrow(()->new CustomException(ExceptionCode.FAN_NOT_FOUND));

        // 현 접속자
        Fan curFan = fanRepository.findByCommunityIdAndUser(fan.getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        return new FanResponse.FanProfileDTO(fan, fan.equals(curFan));
    }

    public PostResponse.PostListDTO getFanPosts(Long fanId, Pageable pageable, User user) {
        // 유저
        Fan fan = fanRepository.findById(fanId).orElseThrow(()->new CustomException(ExceptionCode.FAN_NOT_FOUND));

        // 현재 접속 유저
        Fan curFan = fanRepository.findByCommunityIdAndUser(fan.getCommunityId(), user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));
        boolean isTeam = curFan.getType().equals(CommunityType.TEAM);

        // 유저의 글 목록
        Page<Post> postList = postRepository.findByFan(fan, curFan, pageable);

        List<PostResponse.PostInfoWithCommunityDTO> postInfoDTOS = postList.stream().map((post -> {
            List<PostImage> postImages = postImageRepository.findByPost(post);
            List<PostImageResponse.ImageDTO> imageDTOS = postImages.stream().map((PostImageResponse.ImageDTO::new)).toList();

            Optional<Like> like = likeRepository.findByTargetIdAndTargetTypeAndFan(post.getId(), "POST", curFan);
            Long likeCount = likeRepository.countByTargetIdAndTargetType(post.getId(), "POST");

            Long commentCount = commentRepository.countByPost(post) + reCommentRepository.countByPost(post);

            VoteResponse.VoteDTO voteDTO = post.getVote() != null ? voteService.getVoteInfo(post.getVote(), curFan) : null;

            if(isTeam) {
                Team team = teamRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));

                return new PostResponse.PostInfoWithCommunityDTO(post, like.isPresent(), likeCount, commentCount, imageDTOS, curFan, team, voteDTO);
            } else {
                Player player = playerRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

                return new PostResponse.PostInfoWithCommunityDTO(post, like.isPresent(), likeCount, commentCount, imageDTOS, curFan, player, voteDTO);
            }
        })).toList();

        return new PostResponse.PostListDTO(postList, postInfoDTOS);
    }

    @Transactional
    public void updateFanImage(Long fanId, String type, MultipartFile image) {
        Fan fan = fanRepository.findById(fanId).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        String imageUrl = "";
        if(image == null) {
            imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/profile-image.jpg";
        } else {
            imageUrl = s3Util.upload(image);
        }

        if(type.equals("COMMUNITY")) {
            fan.setImage(imageUrl);
        } else {
            fan.setMeetImage(imageUrl);
        }

        fanRepository.save(fan);
    }

    public void updateFanName(Long fanId, String type, UserRequest.NameDTO requestDTO) {
        if(badWordService.containsBadWords(requestDTO.name())) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }
        String name = requestDTO.name();

        Fan fan = fanRepository.findById(fanId).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        if(fan.getType().equals(CommunityType.TEAM)) {
            Team team = teamRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
            if(team.getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }
        } else {
            Player player = playerRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
            if(player.getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }
        }

        Optional<Fan> duplicateNameFan = fanRepository.findByCommunityIdAndName(fan.getCommunityId(), name);

        if(duplicateNameFan.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NAME);
        }

        if(type.equals("COMMUNITY")) {
            fan.setName(requestDTO.name());
        } else {
            fan.setMeetName(requestDTO.name());
        }
        fanRepository.save(fan);
    }
}
