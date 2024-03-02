package com.odazie.simpleblog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatsTableDto {
    private String playerName; //
    private int games; //
    private int wins; //
    private int losses; //
    private String winRate; //
    private int longestWin;
    private int longestLoss;
    private String currentDivision; //
    private float kda;
    private int uniqueChamps;
    private String bestChamp;
    private String worstChamp;
    private int mostKills;
    private int mostDeaths;
    private boolean hotStreak; //
    private String milestoneWeekOne;
    private String milestoneWeekTwo;
    private String milestoneWeekThree;
    private String milestoneWeekFour;
    private String milestoneWeekFive;
    private String milestoneWeekSix;
    private String milestoneWeekSeven;
    private String milestoneWeekEight;
}
