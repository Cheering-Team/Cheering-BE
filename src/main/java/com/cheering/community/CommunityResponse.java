package com.cheering.community;

import com.cheering.fan.FanResponse;
import com.cheering.player.Player;
import com.cheering.team.Team;

public class CommunityResponse {
    public record CommunityDTO(Long id, String type, String koreanName, String englishName, String image, String backgroundImage, Long fanCount, FanResponse.FanDTO curFan, String sportName, String leagueName, String firstTeamName, Long officialRoomId) {

        public CommunityDTO(Team team, Long fanCount, FanResponse.FanDTO curFan) {
            this(team.getId(), "TEAM", team.getKoreanName(), team.getEnglishName(), team.getImage(), team.getBackgroundImage(), fanCount, curFan, team.getLeague().getSport().getName(), team.getLeague().getName(), null, null);
        }

        public CommunityDTO(Player player, Long fanCount, FanResponse.FanDTO curFan) {
            this(player.getId(), "PLAYER", player.getKoreanName(), player.getEnglishName(), player.getImage(), player.getBackgroundImage(), fanCount, curFan, null, null, player.getFirstTeam().getKoreanName(), null);
        }

        public CommunityDTO(Team team, Long fanCount, FanResponse.FanDTO curFan, Long officialRoomId) {
            this(team.getId(), "TEAM", team.getKoreanName(), team.getEnglishName(), team.getImage(), team.getBackgroundImage(), fanCount, curFan, team.getLeague().getSport().getName(), team.getLeague().getName(), null, officialRoomId);
        }

        public CommunityDTO(Player player, Long fanCount, FanResponse.FanDTO curFan, Long officialRoomId) {
            this(player.getId(), "PLAYER", player.getKoreanName(), player.getEnglishName(), player.getImage(), player.getBackgroundImage(), fanCount, curFan, null, null, player.getFirstTeam().getKoreanName(), officialRoomId);
        }
    }
}
