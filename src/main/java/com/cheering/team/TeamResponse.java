package com.cheering.team;

public class TeamResponse {
    public record TeamDTO(Long id, String name, String image, Long fanCount, Long communityId) {
        public TeamDTO(Team team, Long communityId) {
            this(team.getId(), team.getName(), team.getImage(), team.getFanCount(), communityId);
        }

        public TeamDTO(Team team) {
            this(team.getId(), team.getName(), team.getImage(), team.getFanCount(), null);
        }
    }
}
