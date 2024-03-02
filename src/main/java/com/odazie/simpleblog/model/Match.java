package com.odazie.simpleblog.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table( name = "match" )
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;
    private long matchId;
    private String summonerName;
    private String result;
    private int kills;
    private int deaths;
    private int assists;
    private String champion;
    private boolean ignore;
}
