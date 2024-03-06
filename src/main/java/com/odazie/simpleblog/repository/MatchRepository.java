package com.odazie.simpleblog.repository;

import com.odazie.simpleblog.model.ApiKey;
import com.odazie.simpleblog.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, String >
{
    List<Match> findBySummonerName(String summonerName);

    Boolean existsByMatchIdAndSummonerName(long matchId, String summonerName);

    List<Match> findBySummonerNameAndIgnore(String summonerName, boolean ignore);

    int countBySummonerName(String summonerName);
}
