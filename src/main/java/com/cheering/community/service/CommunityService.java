package com.cheering.community.service;

import com.cheering.community.domain.Community;
import com.cheering.community.domain.PlayerCommunity;
import com.cheering.community.domain.TeamCommunity;
import com.cheering.community.domain.repository.CommunityRepository;
import com.cheering.community.domain.repository.PlayerCommunityRepository;
import com.cheering.community.domain.repository.PlayerRepository;
import com.cheering.community.domain.repository.TeamCommunityRepository;
import com.cheering.community.dto.CommunityResponse;
import com.cheering.user.domain.Player;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PlayerCommunityRepository playerCommunityRepository;
    private final TeamCommunityRepository teamCommunityRepository;
    private final CommunityRepository communityRepository;
    private final PlayerRepository playerRepository;

    public List<CommunityResponse> findCommunitiesByName(String name) {
        List<Community> communities = communityRepository.findCommunitiesByName(name);

        List<CommunityResponse> responseResult = new ArrayList<>();

        for (Community community : communities) {
            if (community instanceof PlayerCommunity playerCommunity) {
                CommunityResponse communityResponse = generatePlayerCommunityResponse(playerCommunity);
                responseResult.add(communityResponse);
            }

            if (community instanceof TeamCommunity teamCommunity) {
                CommunityResponse communityResponse = generateTeamCommunityResponse(teamCommunity);
                responseResult.add(communityResponse);
            }
        }

        return responseResult;
    }

    private CommunityResponse generateTeamCommunityResponse(TeamCommunity teamCommunity) {
        List<Player> players = teamCommunity.getPlayers();
        List<PlayerCommunity> playerCommunities = getPlayerCommunitiesByPlayers(players);

        return CommunityResponse.of(playerCommunities, teamCommunity);
    }

    private CommunityResponse generatePlayerCommunityResponse(PlayerCommunity playerCommunity) {
        TeamCommunity teamCommunity = playerCommunity.getPlayer().getTeamCommunity();
        List<PlayerCommunity> playerCommunities = List.of(playerCommunity);

        return CommunityResponse.of(playerCommunities, teamCommunity);
    }

    private List<PlayerCommunity> getPlayerCommunitiesByPlayers(List<Player> players) {
        return players.stream().map(Player::getPlayerCommunity).toList();
    }

    @Transactional
    public void setData() {
        PlayerCommunity playerCommunity1 = PlayerCommunity.builder().name("playerA1")
                .fanCount(1L).build();
        PlayerCommunity playerCommunity2 = PlayerCommunity.builder().name("playerA2")
                .fanCount(2L).build();
        PlayerCommunity playerCommunity3 = PlayerCommunity.builder().name("playerA3")
                .fanCount(3L).build();
        PlayerCommunity playerCommunity4 = PlayerCommunity.builder().name("playerB4")
                .fanCount(4L).build();
        PlayerCommunity playerCommunity5 = PlayerCommunity.builder().name("playerB5")
                .fanCount(5L).build();
        PlayerCommunity playerCommunity6 = PlayerCommunity.builder().name("playerB6")
                .fanCount(6L).build();

        playerCommunityRepository.save(playerCommunity1);
        playerCommunityRepository.save(playerCommunity2);
        playerCommunityRepository.save(playerCommunity3);
        playerCommunityRepository.save(playerCommunity4);
        playerCommunityRepository.save(playerCommunity5);
        playerCommunityRepository.save(playerCommunity6);

        TeamCommunity teamCommunity1 = TeamCommunity.builder().name("teamCommunity1").players(new ArrayList<>())
                .build();
        TeamCommunity teamCommunity2 = TeamCommunity.builder().name("teamCommunity2").players(new ArrayList<>())
                .build();

        teamCommunityRepository.save(teamCommunity1);
        teamCommunityRepository.save(teamCommunity2);

        Player playerA1 = Player.builder().playerCommunity(playerCommunity1).name("playerA1").build();
        Player playerA2 = Player.builder().playerCommunity(playerCommunity2).name("playerA2").build();
        Player playerA3 = Player.builder().playerCommunity(playerCommunity3).name("playerA3").build();

        playerA1.connectTeamCommunity(teamCommunity1);
        playerA2.connectTeamCommunity(teamCommunity1);
        playerA3.connectTeamCommunity(teamCommunity1);

        playerRepository.save(playerA1);
        playerRepository.save(playerA2);
        playerRepository.save(playerA3);

        Player playerB1 = Player.builder().playerCommunity(playerCommunity4).name("playerB1").build();
        Player playerB2 = Player.builder().playerCommunity(playerCommunity5).name("playerB2").build();
        Player playerB3 = Player.builder().playerCommunity(playerCommunity6).name("playerB3").build();

        playerB1.connectTeamCommunity(teamCommunity2);
        playerB2.connectTeamCommunity(teamCommunity2);
        playerB3.connectTeamCommunity(teamCommunity2);

        playerRepository.save(playerB1);
        playerRepository.save(playerB2);
        playerRepository.save(playerB3);

    }
}
