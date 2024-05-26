package com.cheering.team;

public class TeamResponse {
    public record TeamDTO(Long id, String name) {
        public TeamDTO(Team team) {
            this(team.getId(), team.getName());
        }
    }
}
