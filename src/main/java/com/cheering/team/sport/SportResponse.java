package com.cheering.team.sport;

public class SportResponse {

    public record SportDTO(Long id, String name, String image) {
        public SportDTO(Sport sport){
            this(sport.getId(), sport.getName(), sport.getImage());
        }
    }
}
