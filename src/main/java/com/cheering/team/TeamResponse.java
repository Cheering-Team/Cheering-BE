package com.cheering.team;

import com.cheering.fan.FanResponse;

public class TeamResponse {
    public record TeamDTO(Long id, String koreanName, String image, String shortName) {

        public TeamDTO(Team team) {
            this(team.getId(), team.getKoreanName(), team.getImage(), team.getShortName());
        }
    }
}
