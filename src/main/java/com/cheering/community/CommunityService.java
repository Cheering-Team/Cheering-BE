package com.cheering.community;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.badword.BadWordService;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.player.Player;
import com.cheering.player.PlayerRepository;
import com.cheering.post.PostImage.PostImage;
import com.cheering.post.PostImage.PostImageRepository;
import com.cheering.report.commentReport.CommentReport;
import com.cheering.report.commentReport.CommentReportRepository;
import com.cheering.report.postReport.PostReport;
import com.cheering.report.postReport.PostReportRepository;
import com.cheering.report.reCommentReport.ReCommentReport;
import com.cheering.report.reCommentReport.ReCommentReportRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final BadWordService badWordService;
    private final PostReportRepository postReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReCommentReportRepository reCommentReportRepository;
    private final S3Util s3Util;
    private final PostImageRepository postImageRepository;

    // 커뮤니티 조회
    public CommunityResponse.CommunityDTO getCommunityById(Long communityId, User user) {
        Optional<Team> team = teamRepository.findById(communityId);
        Optional<Player> player = playerRepository.findById(communityId);

        Long fanCount = fanRepository.countByCommunityId(communityId);
        Optional<Fan> fan = fanRepository.findByCommunityIdAndUser(communityId, user);
        ChatRoom officialChatRoom = chatRoomRepository.findOfficialByCommunityId(communityId);
        if(team.isPresent()) {
            return new CommunityResponse.CommunityDTO(team.get(), fanCount, fan.map(FanResponse.FanDTO::new).orElse(null), officialChatRoom.getId());
        }
        else if(player.isPresent()) {
            return new CommunityResponse.CommunityDTO(player.get(), fanCount, fan.map(FanResponse.FanDTO::new).orElse(null), officialChatRoom.getId());
        }
        return null;
    }

    // 커뮤니티 가입
    @Transactional
    public void joinCommunity(Long communityId, String name, User user) {
        Optional<Player> player = playerRepository.findById(communityId);
        Optional<Team> team = teamRepository.findById(communityId);

        Optional<Fan> duplicatePlayerUser = fanRepository.findByCommunityIdAndName(communityId, name);

        if(duplicatePlayerUser.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NAME);
        }

        if(badWordService.containsBadWords(name)) {
            throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
        }

        Integer count = fanRepository.countByUser(user);

        if(team.isPresent()) {
            if(team.get().getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }

            String imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/profile-image.jpg";

            Fan fan = Fan.builder()
                    .type(CommunityType.TEAM)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityId)
                    .communityOrder(count + 1)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
        if(player.isPresent()) {
            if(player.get().getKoreanName().equals(name)) {
                throw new CustomException(ExceptionCode.BADWORD_INCLUDED);
            }

            String imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/profile-image.jpg";

            Fan fan = Fan.builder()
                    .type(CommunityType.PLAYER)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityId)
                    .communityOrder(count + 1)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
    }

    // 내가 가입한 커뮤니티 조회
    public List<CommunityResponse.CommunityDTO> getMyCommunities(User user) {
        List<Fan> fans = fanRepository.findByUserOrderByCommunityOrderAsc(user);

        return fans.stream().map((fan -> {
            Long fanCount = fanRepository.countByCommunityId(fan.getCommunityId());
            ChatRoom officialChatRoom = chatRoomRepository.findOfficialByCommunityId(fan.getCommunityId());
            if(fan.getType().equals(CommunityType.TEAM)) {
                Team team = teamRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
                return new CommunityResponse.CommunityDTO(team, fanCount, new FanResponse.FanDTO(fan), officialChatRoom.getId());
            } else {
                Player player = playerRepository.findById(fan.getCommunityId()).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));
                return new CommunityResponse.CommunityDTO(player, fanCount, new FanResponse.FanDTO(fan), officialChatRoom.getId());
            }
        })).toList();
    }

    @Transactional
    public void changeCommunityOrder(List<CommunityRequest.ChangeOrderRequest> changeOrderRequests, User user) {
        for(CommunityRequest.ChangeOrderRequest changeOrderRequest: changeOrderRequests) {
            Fan fan = fanRepository.findByCommunityIdAndUser(changeOrderRequest.communityId(), user).orElseThrow(()-> new CustomException(ExceptionCode.FAN_NOT_FOUND));

            fan.setCommunityOrder(changeOrderRequest.communityOrder());
            fanRepository.save(fan);
        }
    }

    public void joinCommunities(CommunityRequest.JoinCommunitiesRequest requestDTO, User user) {
        List<Long> communityIds = requestDTO.communityIds();

        for(int i=0; i < communityIds.size(); i++){
            String name;
            String imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/profile-image.jpg";

            do {
                name = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            } while (fanRepository.existsByName(name));

            Fan fan = Fan.builder()
                    .type(CommunityType.TEAM)
                    .name(name)
                    .image(imageUrl)
                    .communityId(communityIds.get(i))
                    .communityOrder(i+1)
                    .user(user)
                    .build();

            fanRepository.save(fan);
        }
    }

    public CommunityResponse.CommunityDTO getRandomCommunity() {
        long teamCount = teamRepository.count();
        long playerCount = playerRepository.count();
        long totalCount = teamCount + playerCount;

        Random random = new Random();
        long randomNumber = random.nextLong(totalCount);

        if (randomNumber < teamCount) {
            Team team = teamRepository.findRandomTeam();
            return new CommunityResponse.CommunityDTO(team, null, null);
        } else {
            Player player = playerRepository.findRandomPlayer();
            return new CommunityResponse.CommunityDTO(player, null, null);
        }
    }

    // 커뮤니티 탈퇴
    @Transactional
    public void leaveCommunity(Long communityId, User user) {
        Fan fan = fanRepository.findByCommunityIdAndUser(communityId, user).orElseThrow(()->new CustomException(ExceptionCode.CUR_FAN_NOT_FOUND));

        List<PostImage> postImages = postImageRepository.findByFan(fan);

        for(PostImage postImage : postImages) {
            s3Util.deleteImageFromS3(postImage.getPath());
        }

        List<ReCommentReport> reCommentReports = reCommentReportRepository.findByWriter(fan);
        for(ReCommentReport reCommentReport : reCommentReports) {
            reCommentReport.setReComment(null);
        }

        List<CommentReport> commentReports = commentReportRepository.findByWriter(fan);
        for(CommentReport commentReport : commentReports) {
            commentReport.setComment(null);
        }

        List<PostReport> postReports = postReportRepository.findByWriter(fan);
        for(PostReport postReport : postReports) {
            postReport.setPost(null);
        }

        List<Fan> fans = fanRepository.findByUserOrderByCommunityOrderAsc(fan.getUser());

        Integer removedOrder = fan.getCommunityOrder();

        fanRepository.delete(fan);
        fans.remove(fan);

        for(Fan eachFan : fans) {
            if(eachFan.getCommunityOrder() > removedOrder) {
                eachFan.setCommunityOrder(eachFan.getCommunityOrder() - 1);
            }
        }

        fanRepository.saveAll(fans);
    }
}
