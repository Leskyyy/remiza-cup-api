package com.odazie.simpleblog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odazie.simpleblog.model.Match;
import com.odazie.simpleblog.model.Player;
import com.odazie.simpleblog.repository.ApiKeyRepository;
import com.odazie.simpleblog.repository.MatchRepository;
import com.odazie.simpleblog.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchHistoryService {

    private static final String BASE_URL = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/";
    private static final String LEAGUE_BASE_URL = "https://euw1.api.riotgames.com/lol/league/v4/entries/by-summoner/";
    private static final String HTTP_CONSTANT = "/ids?start=0&count=";

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private PlayerRepository playerRepository;

    public void updateMatchHistory(String playerName, String puuid, int count) {
        List<String> matchIds = null;
        try {
            matchIds = requestLast5Matches(puuid, count);
        } catch (IOException | InterruptedException e) {
            log.error("Error while updating match history for player with puuid: " + puuid);
        }

        if (matchIds == null) {
            return;
        }

        for (String matchId : matchIds) {
            if (matchRepository.existsByMatchIdAndSummonerName(Long.parseLong(matchId.substring(5)), playerName)) {
                log.info("Match with id: " + matchId + " already exists in the database");
                continue;
            }
            try {
                log.info("this is the match id: " + matchId);
                Match match = getMatchDetails(puuid, playerName, matchId);
                if(match == null){
                    continue;
                }
                matchRepository.save(match);

                int matchCount = matchRepository.countBySummonerName(playerName);
                Player player = playerRepository.findByPlayerName(playerName);

                if(matchCount == 20 && player.getMilestoneWeekOne() == null){
                    player.setMilestoneWeekOne(getCurrentRank(player.getEncryptedAccountId()));
                }
                if(matchCount == 40 && player.getMilestoneWeekTwo() == null){
                    player.setMilestoneWeekTwo(getCurrentRank(player.getEncryptedAccountId()));
                }
                if(matchCount == 60 && player.getMilestoneWeekThree() == null){
                    player.setMilestoneWeekThree(getCurrentRank(player.getEncryptedAccountId()));
                }
                if(matchCount == 80 && player.getMilestoneWeekFour() == null){
                    player.setMilestoneWeekFour(getCurrentRank(player.getEncryptedAccountId()));
                }
                if(matchCount == 100 && player.getMilestoneWeekFive() == null){
                    player.setMilestoneWeekFive(getCurrentRank(player.getEncryptedAccountId()));
                }
                if(matchCount == 120 && player.getMilestoneWeekSix() == null){
                    player.setMilestoneWeekSix(getCurrentRank(player.getEncryptedAccountId()));
                }
                if(matchCount == 140 && player.getMilestoneWeekSeven() == null){
                    player.setMilestoneWeekSeven(getCurrentRank(player.getEncryptedAccountId()));
                }
                if(matchCount == 160 && player.getMilestoneWeekEight() == null){
                    player.setMilestoneWeekEight(getCurrentRank(player.getEncryptedAccountId()));
                }
                playerRepository.save(player);

            } catch (IOException | InterruptedException e) {
                log.error("Error while updating match history for player with puuid: " + puuid);
            }
        }
    }

    private List<String> requestLast5Matches(String puuid, int count) throws IOException, InterruptedException {
        String url = BASE_URL + puuid + HTTP_CONSTANT + count;
        String API_KEY = apiKeyRepository.findAll().get(0).getApiKey();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        List<String> data = mapper.readValue(response.body(), List.class);
        return data;
    }

    private Match getMatchDetails(String puuid, String playerName, String matchId) throws IOException, InterruptedException {
        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/" + matchId;
        String API_KEY = apiKeyRepository.findAll().get(0).getApiKey();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        JSONObject parsedResponse = new JSONObject(responseBody);  // Parse the JSON string
        JSONArray participantsArray = parsedResponse.getJSONObject("info").getJSONArray("participants");

        if(checkIfMatchIsInvalid(parsedResponse)){
            return null;
        }

        String champion = null;
        String result = null;
        int kills = 0;
        int deaths = 0;
        int assists = 0;

        for (int i = 0; i < participantsArray.length(); i++) {
            JSONObject participant = participantsArray.getJSONObject(i);
            if (participant.getString("puuid").equals(puuid)) {
                int teamId = participant.getInt("teamId");
                JSONArray teams = parsedResponse.getJSONObject("info").getJSONArray("teams");

                if (teamId == 100 && teams.getJSONObject(0).getBoolean("win")) {
                    result = "WIN";
                } else if (teamId == 200 && teams.getJSONObject(1).getBoolean("win")) {
                    result = "WIN";
                } else {
                    result = "LOSS";
                }
                champion = participant.getString("championName");
                kills = participant.getInt("kills");
                deaths = participant.getInt("deaths");
                assists = participant.getInt("assists");
            }
        }

        return Match.builder()
                .matchId(Long.parseLong(matchId.substring(5)))
                .summonerName(playerName)
                .result(result)
                .kills(kills)
                .deaths(deaths)
                .assists(assists)
                .champion(champion)
                .build();
    }

    public int countLongestWinningStreak(List<Match> matches) {
        int longestStreak = 0;
        int currentStreak = 0;
        matches.sort((m1, m2) -> Long.compare(m2.getMatchId(), m1.getMatchId()));
        for (Match match : matches) {
            if (match.getResult().equals("WIN")) {
                currentStreak++;
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak;
                }
            } else {
                currentStreak = 0;
            }
        }
        return longestStreak;
    }

    public int countLongestLosingStreak(List<Match> matches) {
        int longestStreak = 0;
        int currentStreak = 0;
        matches.sort((m1, m2) -> Long.compare(m2.getMatchId(), m1.getMatchId()));
        for (Match match : matches) {
            if (match.getResult().equals("LOSS")) {
                currentStreak++;
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak;
                }
            } else {
                currentStreak = 0;
            }
        }
        return longestStreak;
    }

    public float calculateKda(List<Match> matches) {
        int totalKills = 0;
        int totalDeaths = 0;
        int totalAssists = 0;
        for (Match match : matches) {
            totalKills += match.getKills();
            totalDeaths += match.getDeaths();
            totalAssists += match.getAssists();
        }
        if (totalDeaths == 0) {
            return (float) (totalKills + totalAssists);
        }

        float kda = (float) (totalKills + totalAssists) / totalDeaths;
        return returnFloatWith2Decimals(kda);
    }

    public int countUniqueChampions(List<Match> matches) {
        return (int) matches.stream().map(Match::getChampion).distinct().count();
    }

    public String countBestChampion(List<Match> matches)
    {
        // count kda for each champion
        Map<String, float[]> kdaMap = matches.stream().collect(
                Collectors.groupingBy(Match::getChampion,
                        Collectors.collectingAndThen(
                                Collectors.reducing(new float[3], m -> new float[]{m.getKills(), m.getDeaths(), m.getAssists()}, (a, b) -> new float[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]}),
                                a -> new float[]{a[0], a[1], a[2], a[1] == 0 ? a[0] + a[2] : (a[0] + a[2]) / a[1]}
                        )
                )
        );

        // only include champions with at least 5 games
        // kdaMap = kdaMap.entrySet().stream().filter(entry -> matches.stream().filter(match -> match.getChampion().equals(entry.getKey())).count() >= 5).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // find the best kda
        float bestKda = 0;
        String bestChampion = "";
        for (Map.Entry<String, float[]> entry : kdaMap.entrySet()) {
            if (entry.getValue()[3] > bestKda) {
                bestKda = entry.getValue()[3];
                bestChampion = entry.getKey();
            }
        }

        return bestChampion;
    }

    public String countWorstChampion(List<Match> matches)
    {
        // count kda for each champion
        Map<String, float[]> kdaMap = matches.stream().collect(
                Collectors.groupingBy(Match::getChampion,
                        Collectors.collectingAndThen(
                                Collectors.reducing(new float[3], m -> new float[]{m.getKills(), m.getDeaths(), m.getAssists()}, (a, b) -> new float[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]}),
                                a -> new float[]{a[0], a[1], a[2], a[1] == 0 ? a[0] + a[2] : (a[0] + a[2]) / a[1]}
                        )
                )
        );

        // find the worst kda
        float worstKda = Float.MAX_VALUE;
        String worstChampion = "";
        for (Map.Entry<String, float[]> entry : kdaMap.entrySet()) {
            if (entry.getValue()[3] < worstKda) {
                worstKda = entry.getValue()[3];
                worstChampion = entry.getKey();
            }
        }

        return worstChampion;
    }

    public int findMostKills(List<Match> matches) {
        return matches.stream().map(Match::getKills).max(Integer::compareTo).orElse(0);
    }

    public int findMostDeaths(List<Match> matches) {
        return matches.stream().map(Match::getDeaths).max(Integer::compareTo).orElse(0);
    }

    public int countWins(List<Match> matches) {
        return (int) matches.stream().filter(match -> match.getResult().equals("WIN")).count();
    }

    public int countLosses(List<Match> matches) {
        return (int) matches.stream().filter(match -> match.getResult().equals("LOSS")).count();
    }

    private boolean checkIfMatchIsInvalid(JSONObject parsedResponse){
        JSONArray participantsArray = parsedResponse.getJSONObject("metadata").getJSONArray("participants");
        if(participantsArray.length() != 10){
            return true;
        }
        if(parsedResponse.getJSONObject("info").getInt("gameDuration") < 240){
            return true;
        }
        return !Objects.equals(parsedResponse.getJSONObject("info").getString("gameMode"), "CLASSIC");
    }

    private String getCurrentRank(String encryptedSummonerId) throws IOException, InterruptedException {
        String url = LEAGUE_BASE_URL + encryptedSummonerId;
        String API_KEY = apiKeyRepository.findAll().get(0).getApiKey();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonObject = new JSONArray(response.body()).getJSONObject(0);
        return jsonObject.getString("tier") + " " + jsonObject.getString("rank") + " " + jsonObject.getInt("leaguePoints") + " LP";
    }

    public float returnFloatWith2Decimals(float num) {
        int multiplier = (int) Math.pow(10, 2); // Multiply by 100 to shift decimal two places
        return (float) (Math.round(num * multiplier) / (double) multiplier); // Round and divide back
    }
}
