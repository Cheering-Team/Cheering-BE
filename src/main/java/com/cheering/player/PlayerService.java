package com.cheering.player;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering.chat.chatRoom.ChatRoom;
import com.cheering.chat.chatRoom.ChatRoomRepository;
import com.cheering.chat.chatRoom.ChatRoomType;
import com.cheering.community.CommunityResponse;
import com.cheering.fan.Fan;
import com.cheering.fan.FanRepository;
import com.cheering.fan.FanResponse;
import com.cheering.fan.CommunityType;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.TeamResponse;
import com.cheering.team.relation.TeamPlayer;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final FanRepository fanRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void registerPlayer(Long teamId, PlayerRequest.RegisterCommunityDTO requestDTO) {
        Team team = teamRepository.findById(teamId).orElseThrow(()->new CustomException(ExceptionCode.TEAM_NOT_FOUND));

        Player player = Player.builder()
                .koreanName(requestDTO.koreanName())
                .englishName(requestDTO.englishName())
                .image(requestDTO.image())
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
                .image(requestDTO.image())
                .description(player.getKoreanName() + " 팬들끼리 응원해요!")
                .communityId(player.getId())
                .build();

        chatRoomRepository.save(chatRoom);
    }

    public List<CommunityResponse.CommunityDTO> getPopularPlayers() {
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        Pageable topTen = PageRequest.of(0, 10);
        List<Player> players = playerRepository.findTop10PlayersByRecentFanCount(lastWeek, topTen);

        return players.stream().map(player -> new CommunityResponse.CommunityDTO(player, null, null)).toList();
    }
}
