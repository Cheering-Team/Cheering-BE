package com.cheering.community;

import com.cheering.community.relation.Fan;
import com.cheering.community.relation.FanResponse;
import com.cheering.team.TeamResponse;
import com.cheering.team.league.League;
import com.cheering.team.sport.Sport;

import java.util.List;
import java.util.Optional;

public class CommunityResponse {
    public record CommunityDTO(Long id, CommunityType type, String koreanName, String englishName, String image, String backgroundImage, Long fanCount, FanResponse.FanDTO user, List<TeamResponse.TeamDTO> teams, String sportName, String leagueName, Boolean isManager, FanResponse.FanDTO manager, Long officialChatRoomId) {

        public CommunityDTO(Community community, Long fanCount, Fan fan, List<TeamResponse.TeamDTO> teams, String sportName, String leagueName, Boolean isOwner, Long officialChatRoomId) {
            this(community.getId(), community.getType(), community.getKoreanName(), community.getEnglishName(), community.getImage(), community.getBackgroundImage(), fanCount, Optional.ofNullable(fan)
                    .map(FanResponse.FanDTO::new)
                    .orElse(null), teams, sportName, leagueName, isOwner, Optional.ofNullable(community.getManager())
                    .map(FanResponse.FanDTO::new)
                    .orElse(null), officialChatRoomId);
        }

        public CommunityDTO(Community community) {
            this(community.getId(), community.getType(), community.getKoreanName(), community.getEnglishName(), community.getImage(), community.getBackgroundImage(), null, null, null, null, null, null, Optional.ofNullable(community.getManager())
                    .map(FanResponse.FanDTO::new)
                    .orElse(null), null);
        }
    }

    public record PlayersOfTeamDTO(String sportName, String leagueName, TeamResponse.TeamDTO team, List<CommunityDTO> communities) {
        public PlayersOfTeamDTO(Sport sport, League league, TeamResponse.TeamDTO team, List<CommunityDTO> communities) {
            this(sport.getName(), league.getName(), team, communities);
        }
    }
}
