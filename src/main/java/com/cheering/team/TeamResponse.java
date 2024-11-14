package com.cheering.team;

import com.cheering.fan.FanResponse;

public class TeamResponse {
    public record TeamDTO(Long id, String koreanName, String image, String shortName, String color) {

        public TeamDTO(Team team) {
            this(team.getId(), team.getKoreanName(), team.getImage(), team.getShortName(), team.getColor());
        }
    }

    public record TeamWithLeagueDTO(Long id, String koreanName, String image, String shortName, String color, String sportName, String leagueName) {

        public TeamWithLeagueDTO(Team team) {
            this(team.getId(), team.getKoreanName(), team.getImage(), team.getShortName(), team.getColor(), team.getLeague().getSport().getName(), team.getLeague().getName());
        }
    }
}
