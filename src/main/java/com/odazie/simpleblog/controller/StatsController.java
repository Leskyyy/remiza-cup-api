package com.odazie.simpleblog.controller;

import com.odazie.simpleblog.dto.LeagueV4ApiDto;
import com.odazie.simpleblog.dto.MatchHistoryDetailsDto;
import com.odazie.simpleblog.dto.StatsTableDto;
import com.odazie.simpleblog.model.Match;
import com.odazie.simpleblog.model.Player;
import com.odazie.simpleblog.repository.MatchRepository;
import com.odazie.simpleblog.repository.PlayerRepository;
import com.odazie.simpleblog.service.MatchHistoryService;
import com.odazie.simpleblog.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class StatsController {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private StatsService statsService;
    @Autowired
    private MatchHistoryService matchHistoryService;

    @GetMapping("/updateStatistics")
    public List<StatsTableDto> updateStatistics() {
        StatsTableDto statsTableDto = new StatsTableDto();
        List<Player> players = playerRepository.findAll();
        List<StatsTableDto> statsTableDtos = new ArrayList<>();

        for (Player player : players) {
            LeagueV4ApiDto leagueResponse = null;
            try{
                leagueResponse = statsService.getLeagueV4ApiDto(player.getEncryptedAccountId());
            }
            catch (Exception e){
                log.error("Error while updating statistics for player: " + player.getPlayerName());
            }

            MatchHistoryDetailsDto matchHistoryDetailsDto = getMatchHistoryDetailsDto(player.getPlayerName());
            statsTableDto = getStatsTableDto(player.getPlayerName(), leagueResponse, matchHistoryDetailsDto, player);
            statsTableDtos.add(statsTableDto);
        }

        return statsTableDtos;
    }

    @GetMapping("/updateMatchHistory/{count}")
    public ResponseEntity<HttpStatus> updateMatchHistory(@PathVariable int count) {
        List<Player> players = playerRepository.findAll();

        for (Player player : players) {
            matchHistoryService.updateMatchHistory(player.getPlayerName(), player.getPuuid(), count);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private StatsTableDto getStatsTableDto(String playerName, LeagueV4ApiDto leagueResponse, MatchHistoryDetailsDto matchHistoryDetailsDto, Player player){
        StatsTableDto statsTableDto = new StatsTableDto();
        statsTableDto.setPlayerName(playerName);
        statsTableDto.setWins(matchHistoryDetailsDto.getWins());
        statsTableDto.setLosses(matchHistoryDetailsDto.getLosses());
        statsTableDto.setGames(matchHistoryDetailsDto.getWins() + matchHistoryDetailsDto.getLosses());
        statsTableDto.setWinRate(String.format("%.2f", ((float) matchHistoryDetailsDto.getWins() / (matchHistoryDetailsDto.getWins() + matchHistoryDetailsDto.getLosses())) * 100) + "%");
        statsTableDto.setCurrentDivision(leagueResponse.getTier() + " " + leagueResponse.getRank() + " " + leagueResponse.getLeaguePoints() + " LP");
        statsTableDto.setHotStreak(leagueResponse.isHotStreak());
        statsTableDto.setLongestWin(matchHistoryDetailsDto.getLongestWin());
        statsTableDto.setLongestLoss(matchHistoryDetailsDto.getLongestLoss());
        statsTableDto.setKda(matchHistoryDetailsDto.getKda());
        statsTableDto.setUniqueChamps(matchHistoryDetailsDto.getUniqueChamps());
        statsTableDto.setBestChamp(matchHistoryDetailsDto.getBestChamp());
        statsTableDto.setWorstChamp(matchHistoryDetailsDto.getWorstChamp());
        statsTableDto.setMostKills(matchHistoryDetailsDto.getMostKills());
        statsTableDto.setMostDeaths(matchHistoryDetailsDto.getMostDeaths());
        statsTableDto.setMilestoneWeekOne(player.getMilestoneWeekOne());
        statsTableDto.setMilestoneWeekTwo(player.getMilestoneWeekTwo());
        statsTableDto.setMilestoneWeekThree(player.getMilestoneWeekThree());
        statsTableDto.setMilestoneWeekFour(player.getMilestoneWeekFour());
        statsTableDto.setMilestoneWeekFive(player.getMilestoneWeekFive());
        statsTableDto.setMilestoneWeekSix(player.getMilestoneWeekSix());
        statsTableDto.setMilestoneWeekSeven(player.getMilestoneWeekSeven());
        statsTableDto.setMilestoneWeekEight(player.getMilestoneWeekEight());

        return statsTableDto;
    }

    private MatchHistoryDetailsDto getMatchHistoryDetailsDto(String playerName) {
        List<Match> matches = matchRepository.findBySummonerName(playerName);

        return MatchHistoryDetailsDto.builder()
                .wins(matchHistoryService.countWins(matches))
                .losses(matchHistoryService.countLosses(matches))
                .longestWin(matchHistoryService.countLongestWinningStreak(matches))
                .longestLoss(matchHistoryService.countLongestLosingStreak(matches))
                .kda(matchHistoryService.calculateKda(matches))
                .uniqueChamps(matchHistoryService.countUniqueChampions(matches))
                .bestChamp(matchHistoryService.countBestChampion(matches))
                .worstChamp(matchHistoryService.countWorstChampion(matches))
                .mostKills(matchHistoryService.findMostKills(matches))
                .mostDeaths(matchHistoryService.findMostDeaths(matches))
                .build();
    }
}
