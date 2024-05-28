package com.cheering.team;

public class TeamResponse {
    public record TeamDTO(Long id, String name, String image, Long fanCount) {
        public TeamDTO(Team team) {
            this(team.getId(), team.getName(), team.getImage(), team.getFanCount());
        }
    }
}
