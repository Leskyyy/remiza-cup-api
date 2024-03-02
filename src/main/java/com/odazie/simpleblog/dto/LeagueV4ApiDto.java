package com.odazie.simpleblog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueV4ApiDto {
    private String tier;
    private String rank;
    private int leaguePoints;
    private boolean hotStreak;
}
