package com.odazie.simpleblog.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GenerationType;
import java.util.List;

@Entity
@Table( name = "player" )
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long playerId;

    private String playerName;

    private String puuid;

    private String encryptedAccountId;

    private String milestoneWeekOne;

    private String milestoneWeekTwo;

    private String milestoneWeekThree;

    private String milestoneWeekFour;

    private String milestoneWeekFive;

    private String milestoneWeekSix;

    private String milestoneWeekSeven;

    private String milestoneWeekEight;
}
