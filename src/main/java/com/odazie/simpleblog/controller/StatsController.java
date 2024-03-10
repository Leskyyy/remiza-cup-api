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
            try {
                leagueResponse = statsService.getLeagueV4ApiDto(player.getEncryptedAccountId());
            } catch (Exception e) {
                log.error("Error while updating statistics for player: " + player.getPlayerName());
            }

            MatchHistoryDetailsDto matchHistoryDetailsDto = getMatchHistoryDetailsDto(player.getPlayerName());
            statsTableDto = getStatsTableDto(player.getPlayerName(), leagueResponse, matchHistoryDetailsDto, player);
            statsTableDto.setActualName(player.getActualName());
            statsTableDto.setImageLink(player.getImageLink());
            statsTableDto.setMainAccountDivision(player.getMainAccountDivision());
            statsTableDto.setSumOfLp(calculateSumOfLp(leagueResponse));
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

    @GetMapping("/finishMilestone/{week}")
    public ResponseEntity<HttpStatus> finishMilestone(@PathVariable int week) {
        List<Player> players = playerRepository.findAll();

        for (Player player : players) {
            try{
                matchHistoryService.finishMilestone(player, week);
            } catch (Exception e) {
                log.error("Error while finishing milestone for player: " + player.getPlayerName());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private StatsTableDto getStatsTableDto(String playerName, LeagueV4ApiDto leagueResponse, MatchHistoryDetailsDto matchHistoryDetailsDto, Player player) {
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
        List<Match> matches = matchRepository.findBySummonerNameAndIgnore(playerName, false);

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

    private int calculateSumOfLp(LeagueV4ApiDto leagueResponse) {
        // based on tier, rank and leaguePoints, calculate the sum of LP
        int sumOfLp = 0;
        switch (leagueResponse.getTier()) {
            case "IRON":
                sumOfLp = 0;
                break;
            case "BRONZE":
                sumOfLp = 400;
                break;
            case "SILVER":
                sumOfLp = 800;
                break;
            case "GOLD":
                sumOfLp = 1200;
                break;
            case "PLATINUM":
                sumOfLp = 1600;
                break;
            case "EMERALD":
                sumOfLp = 2000;
                break;
            case "DIAMOND":
                sumOfLp = 2400;
                break;
            case "MASTER":
                sumOfLp = 2800;
                break;
            case "GRANDMASTER":
                sumOfLp = 3200;
                break;
            case "CHALLENGER":
                sumOfLp = 3600;
                break;

        }

        switch (leagueResponse.getRank()) {
            case "IV":
                sumOfLp += 0;
                break;
            case "III":
                sumOfLp += 100;
                break;
            case "II":
                sumOfLp += 200;
                break;
            case "I":
                sumOfLp += 300;
                break;
        }

        sumOfLp += leagueResponse.getLeaguePoints();

        return sumOfLp;
    }
}
