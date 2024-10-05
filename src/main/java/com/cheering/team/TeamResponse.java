package com.cheering.team;

public class TeamResponse {
    public record TeamDTO(Long id, String name, String image, Long fanCount, Long communityId) {
        public TeamDTO(Team team, Long fanCount, Long communityId) {
            this(team.getId(), team.getFirstName() + " " + team.getSecondName(), team.getImage(), fanCount, communityId);
        }
    }

    public record TeamNameDTO(Long id, String firstName, String secondName, String image) {
        public TeamNameDTO(Team team) {
            this(team.getId(), team.getFirstName(), team.getSecondName(), team.getImage());
        }
    }
}
