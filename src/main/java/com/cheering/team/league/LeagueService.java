package com.cheering.team.league;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeagueService {
    private final LeagueRepository leagueRepository;

    public List<LeagueResponse.LeagueDTO> getLeagues(Long sportId) {
        List<League> leagues = leagueRepository.findBySportId(sportId);

        return leagues.stream().map(LeagueResponse.LeagueDTO::new).toList();
    }
}
