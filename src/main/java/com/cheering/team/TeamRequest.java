package com.cheering.team;

public class TeamRequest {
    public record RegisterTeamDTO(String koreanName, String shortName, String image, String radarId, String location) { }
}
