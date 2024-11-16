package com.cheering.match;

import java.util.List;

public class MatchRequest {
    public record MatchDTO (
           String month,
           String day,
           String time,
           String home,
           String away,
           String location
    ) { }

    public record MatchListDTO (List<MatchDTO> matches) { }
}
