package com.cheering.team;

public class TeamRequest {
    public record RegisterTeamDTO(String firstName, String secondName, String englishName, String image, String backgroundImage) { }
}
