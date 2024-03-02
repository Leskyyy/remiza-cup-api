package com.odazie.simpleblog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odazie.simpleblog.dto.LeagueV4ApiDto;
import com.odazie.simpleblog.repository.ApiKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@Slf4j
public class StatsService {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    private static final String BASE_URL = "https://euw1.api.riotgames.com/lol/league/v4/entries/by-summoner/";
    public LeagueV4ApiDto getLeagueV4ApiDto(String encryptedSummonerId) throws IOException, InterruptedException {
        String url = BASE_URL + encryptedSummonerId;
        String API_KEY = apiKeyRepository.findAll().get(0).getApiKey();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // read fields tier, rank, leaguePoints, hotStreak from the response and use builder from LeagueV4ApiDto to create a new object
        String responseBody = response.body();
        JSONArray jsonArray = new JSONArray(responseBody);
        if (jsonArray.length() == 0) {
            return LeagueV4ApiDto.builder()
                    .tier("NO RANK")
                    .rank("")
                    .leaguePoints(0)
                    .hotStreak(false)
                    .build();
        }
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        return LeagueV4ApiDto.builder()
                .tier(jsonObject.getString("tier"))
                .rank(jsonObject.getString("rank"))
                .leaguePoints(jsonObject.getInt("leaguePoints"))
                .hotStreak(jsonObject.getBoolean("hotStreak"))
                .build();
    }
}
