package com.cheering.team;

import com.cheering.fan.FanResponse;

public class TeamResponse {
    public record TeamDTO(Long id, String koreanName, String image) {

        public TeamDTO(Team team) {
            this(team.getId(), team.getKoreanName(), team.getImage());
        }
//        public TeamDTO(Team team, Long fanCount) {
//            this(team.getId(), team.getKoreanName(), team.getEnglishName(), team.getImage(), team.getBackgroundImage(), team.getLeague().getSport().getName(), team.getLeague().getName(), fanCount, null);
//        }
//
//        public TeamDTO(Team team, Long fanCount, FanResponse.FanDTO curfan) {
//            this(team.getId(), team.getKoreanName(), team.getEnglishName(), team.getImage(), team.getBackgroundImage(), team.getLeague().getSport().getName(), team.getLeague().getName(), fanCount, curfan);
//        }
    }
}
