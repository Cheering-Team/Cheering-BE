package com.cheering.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    public Object getTeams(Long leagueId) {
        List<Team> teams = teamRepository.findByLeagueId(leagueId);

        return teams.stream().map(TeamResponse.TeamDTO::new).toList();
    }
}
