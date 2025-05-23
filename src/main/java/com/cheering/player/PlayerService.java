package com.cheering.player;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.community.CommunityResponse;
import com.cheering.fan.CommunityType;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.post.Post;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FanRepository fanRepository;
    private final S3Util s3Util;

    // 선수 검색하기 (무한 스크롤)
    public CommunityResponse.CommunityListDTO searchPlayers(String name, Long teamId, Pageable pageable, User user) {
        name = name.replace(" ", "");

        Page<Player> players;
        if(teamId != null && name.isEmpty()) {
            players = playerRepository.findByTeamId(teamId, pageable);
        } else if(teamId != null) {
            players = playerRepository.findByNameAndTeamId(name, teamId, pageable);
        } else if(!name.isEmpty()){
            players = playerRepository.findByNameOrTeamName(name, pageable);
        } else {
            players = Page.empty();
        }

        List<CommunityResponse.CommunityDTO> playerDTOS = players.getContent().stream().map((player) -> {
            Long fanCount = fanRepository.countByCommunityId(player.getId());
            Optional<Fan> fan = fanRepository.findByCommunityIdAndUser(player.getId(), user);
            return new CommunityResponse.CommunityDTO(player, fanCount, fan.map(FanResponse.FanDTO::new).orElse(null));
        }).toList();

        return new CommunityResponse.CommunityListDTO(players, playerDTOS);
    }

    public List<CommunityResponse.CommunityDTO> getPopularPlayers() {
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        Pageable topTen = PageRequest.of(0, 10);
        List<Player> players = playerRepository.findTop10PlayersByRecentFanCount(lastWeek, topTen);

        return players.stream().map(player -> new CommunityResponse.CommunityDTO(player, null, null)).toList();
    }

    public void registerPlayer(Long teamId, List<PlayerRequest.RegisterCommunityDTO> requestDTOs) {
        Team team = teamRepository.findById(teamId).orElseThrow(()->new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        requestDTOs.forEach(requestDTO -> {
            String imageUrl = "";

            if(!requestDTO.image().isEmpty()) {
                if(requestDTO.image().startsWith("https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/")) {
                    imageUrl = requestDTO.image();
                } else {
                    try {
                        URL url = new URL(requestDTO.image());
                        try (InputStream inputStream = url.openStream()) {
                            String fileName = requestDTO.image().substring(requestDTO.image().lastIndexOf("/") + 1);
                            MultipartFile image = new MockMultipartFile(fileName, fileName, "image/png", inputStream);
                            imageUrl = s3Util.upload(image);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            Player player = Player.builder()
                    .koreanName(requestDTO.koreanName())
                    .englishName(requestDTO.englishName())
                    .image(imageUrl)
                    .backgroundImage(requestDTO.backgroundImage())
                    .firstTeam(team)
                    .build();

            playerRepository.save(player);

            TeamPlayer teamPlayer = TeamPlayer.builder()
                    .player(player)
                    .team(team)
                    .build();

            teamPlayerRepository.save(teamPlayer);

            ChatRoom chatRoom = ChatRoom.builder()
                    .type(ChatRoomType.OFFICIAL)
                    .communityType(CommunityType.PLAYER)
                    .name(requestDTO.koreanName())
                    .image(imageUrl)
                    .description(player.getKoreanName() + " 팬들끼리 응원해요!")
                    .communityId(player.getId())
                    .build();

            chatRoomRepository.save(chatRoom);
        });
    }
}
