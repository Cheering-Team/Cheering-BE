package com.cheering.player;

import com.cheering._core.errors.CustomException;
import com.cheering._core.errors.ExceptionCode;
import com.cheering._core.util.S3Util;
import com.cheering.player.relation.PlayerUser;
import com.cheering.player.relation.PlayerUserRepository;
import com.cheering.player.relation.PlayerUserResponse;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final TeamPlayerRepository teamPlayerRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerUserRepository playerUserRepository;
    private final S3Util s3Util;

    public List<PlayerResponse.PlayerAndTeamsDTO> getPlayers(String name, User user) {
        name = name.replace(" ", "");
        List<Player> players = playerRepository.findByNameOrTeamName(name).stream().sorted(Comparator.comparing(player -> player.getTeam() != null ? 0 : 1)).toList();

        return players.stream().map((player -> {
            long fanCount = playerUserRepository.countByPlayerId(player.getId());

            List<Team> teams = teamPlayerRepository.findByPlayerId(player.getId());

            List<TeamResponse.TeamDTO> teamDTOS = teams.stream().map((team -> {
                Optional<Player> community = playerRepository.findByTeamId(team.getId());
                return new TeamResponse.TeamDTO(team, null, community.map(Player::getId).orElse(null));
            })).toList();

            Optional<PlayerUser> playerUser = playerUserRepository.findByPlayerIdAndUserId(player.getId(), user.getId());

            if(playerUser.isPresent()) {
                PlayerUserResponse.PlayerUserDTO playerUserDTO = new PlayerUserResponse.PlayerUserDTO(playerUser.get());
                if(player.getTeam() != null) {
                    return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, playerUserDTO, player.getTeam().getLeague().getSport().getName(), player.getTeam().getLeague().getName());
                }
                return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, playerUserDTO, teamDTOS);
            } else {
                if(player.getTeam() != null) {
                    return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, null, player.getTeam().getLeague().getSport().getName(), player.getTeam().getLeague().getName());
                }
                return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, teamDTOS);
            }
        })).toList();
    }

    // 특정 팀 소속 선수 목록
    @Transactional
    public PlayerResponse.PlayersOfTeamDTO getPlayersByTeam(Long teamId, User user) {
        List<Player> players = teamPlayerRepository.findByTeamId(teamId);
        Optional<Player> community = playerRepository.findByTeamId(teamId);

        Team team = teamRepository.findById(teamId).orElseThrow(() -> new CustomException(ExceptionCode.TEAM_NOT_FOUND));
        League league = team.getLeague();
        Sport sport = league.getSport();

        List<PlayerResponse.PlayerDTO> playerDTOS = players.stream().map((player)-> {
            long fanCount = playerUserRepository.countByPlayerId(player.getId());
            Optional<PlayerUser> playerUser = playerUserRepository.findByPlayerIdAndUserId(player.getId(), user.getId());
            if(playerUser.isPresent()) {
                PlayerUserResponse.PlayerUserDTO playerUserDTO = new PlayerUserResponse.PlayerUserDTO(playerUser.get());
                return new PlayerResponse.PlayerDTO(player, fanCount, playerUserDTO);
            } else {
                return new PlayerResponse.PlayerDTO(player, fanCount, null);
            }

        }).toList();

        TeamResponse.TeamDTO teamDTO = community.map(player -> new TeamResponse.TeamDTO(team, playerUserRepository.countByPlayerId(player.getId()), player.getId())).orElseGet(() -> new TeamResponse.TeamDTO(team, null, null));

        return new PlayerResponse.PlayersOfTeamDTO(sport, league, teamDTO, playerDTOS);
    }

    // 해당 선수 커뮤니티 정보 및 가입 여부 불러오기
    @Transactional
    public PlayerResponse.PlayerAndTeamsDTO getPlayerInfo(Long playerId, User user) {
        Player player = playerRepository.findById(playerId).orElseThrow(()-> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

        long fanCount = playerUserRepository.countByPlayerId(player.getId());

        List<Team> teams = teamPlayerRepository.findByPlayerId(playerId);

        List<TeamResponse.TeamDTO> teamDTOS = teams.stream().map((team -> {
            Optional<Player> community = playerRepository.findByTeamId(team.getId());
            return new TeamResponse.TeamDTO(team, null, community.map(Player::getId).orElse(null));
        })).toList();

        Optional<PlayerUser> playerUser = playerUserRepository.findByPlayerIdAndUserId(playerId, user.getId());

        if(playerUser.isPresent()) {
            PlayerUserResponse.PlayerUserDTO playerUserDTO = new PlayerUserResponse.PlayerUserDTO(playerUser.get());
            if(player.getTeam() != null) {
                return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, playerUserDTO, player.getTeam().getLeague().getSport().getName(), player.getTeam().getLeague().getName());
            }
            return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, playerUserDTO, teamDTOS);
        } else {
            if(player.getTeam() != null) {
                return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, null, player.getTeam().getLeague().getSport().getName(), player.getTeam().getLeague().getName());
            }
            return new PlayerResponse.PlayerAndTeamsDTO(player, fanCount, teamDTOS);
        }
    }

    @Transactional
    public void joinCommunity(Long playerId, String nickname, MultipartFile image, User user) {
        Optional<PlayerUser> duplicatePlayerUser = playerUserRepository.findByPlayerIdAndNickname(playerId, nickname);

        if(duplicatePlayerUser.isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_NICKNAME);
        }

        Player player = playerRepository.findById(playerId).orElseThrow(() -> new CustomException(ExceptionCode.PLAYER_NOT_FOUND));

        String imageUrl = "";
        if(image == null) {
            imageUrl = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/default-profile.jpg";
        } else {
            imageUrl = s3Util.upload(image);
        }


        PlayerUser playerUser = PlayerUser.builder()
                .player(player)
                .user(user)
                .nickname(nickname)
                .image(imageUrl)
                .build();

        playerUserRepository.save(playerUser);
    }

    public List<PlayerResponse.PlayerDTO> getMyPlayers(User user) {
        List<PlayerUser> playerUsers = playerUserRepository.findByUserId(user.getId()).stream().sorted(Comparator.comparing(playerUser -> playerUser.getPlayer().getTeam() != null ? 0 : 1)).toList();
        return playerUsers.stream().map((playerUser -> new PlayerResponse.PlayerDTO(playerUser.getPlayer(), new PlayerUserResponse.PlayerUserDTO(playerUser)))).toList();
    }
}
