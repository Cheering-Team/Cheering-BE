package com.cheering.team.league;

public class LeagueResponse {
    public record LeagueDTO(Long id, String name) {
        public LeagueDTO(League league) {
            this(league.getId(), league.getName());
        }
    }
}
