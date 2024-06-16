package com.cheering.player;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.team.Team;
import com.cheering.team.TeamRepository;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.league.LeagueRepository;
import com.cheering.team.relation.TeamPlayerRepository;
import com.cheering.team.sport.Sport;
import com.cheering.team.sport.SportRepository;
import com.cheering.user.User;
import com.cheering.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerUserRepository playerUserRepository;
    private final UserRepository userRepository;
    private final S3Util s3Util;

    @Transactional
    public PlayerResponse.PlayersOfTeamDTO getPlayersByTeam(Long teamId, User user) {
        List<Player> players = teamPlayerRepository.findByTeamId(teamId);

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
        League league = team.getLeague();
        Sport sport = league.getSport();

        List<PlayerResponse.PlayerDTO> playerDTOS = players.stream().map((player)-> {
            Optional<PlayerUser> playerUser = playerUserRepository.findByPlayerIdAndUserId(player.getId(), user.getId());

            return new PlayerResponse.PlayerDTO(player, playerUser.isPresent());
        }).toList();
        TeamResponse.TeamDTO teamDTO = new TeamResponse.TeamDTO(team);

        return new PlayerResponse.PlayersOfTeamDTO(sport, league, teamDTO, playerDTOS);
    }

    // 해당 선수 커뮤니티 정보 및 가입 여부 불러오기
    @Transactional
    public PlayerResponse.PlayerAndTeamsDTO getPlayerInfo(Long playerId, User user) {
        Player player = playerRepository.findById(playerId).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

        List<Team> teams = teamPlayerRepository.findByPlayerId(playerId);

        List<TeamResponse.TeamDTO> teamDTOS = teams.stream().map(TeamResponse.TeamDTO::new).toList();

        Optional<PlayerUser> playerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId());

        return new PlayerResponse.PlayerAndTeamsDTO(player, playerUser.isPresent(), teamDTOS);
    }

    @Transactional
    public void checkNickname(Long playerId, String nickname) {
        Optional<PlayerUser> playerUser = playerUserRepository.findByPlayerIdAndNickname(playerId, nickname);

        if(playerUser.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NICKNAME);
        }
    }

    @Transactional
    public void joinCommunity(Long playerId, String nickname, MultipartFile image, User user) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

        String imageUrl = s3Util.upload(image);

        PlayerUser playerUser = PlayerUser.builder()
                .player(player)
                .user(user)
                .nickname(nickname)
                .image(imageUrl)
                .build();

        playerUserRepository.save(playerUser);
    }
}
