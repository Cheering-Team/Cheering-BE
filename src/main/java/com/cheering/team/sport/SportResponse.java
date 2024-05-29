package com.cheering.team.sport;

import java.util.List;

public class SportResponse {

    public record SportDTO(Long id, String name) {
        public SportDTO(Sport sport){
            this(sport.getId(), sport.getName());
        }
    }
}
