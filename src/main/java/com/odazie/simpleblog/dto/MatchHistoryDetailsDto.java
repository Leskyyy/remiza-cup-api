package com.odazie.simpleblog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MatchHistoryDetailsDto {

    private int wins;
    private int losses;
    private int longestWin;
    private int longestLoss;
    private float kda;
    private int uniqueChamps;
    private String bestChamp;
    private String worstChamp;
    private int mostKills;
    private int mostDeaths;
}
