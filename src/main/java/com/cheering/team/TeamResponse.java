package com.cheering.team;

public class TeamResponse {
    public record TeamDTO(Long id, String name, String image) {
        public TeamDTO(Team team) {
            this(team.getId(), team.getName(), team.getImage());
        }
    }
}
