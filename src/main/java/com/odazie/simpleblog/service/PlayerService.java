package com.odazie.simpleblog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odazie.simpleblog.model.Player;
import com.odazie.simpleblog.repository.ApiKeyRepository;
import com.odazie.simpleblog.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    // CHANGE TO EUW
    private static final String BASE_URL = "https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/";
    @Autowired
    private ApiKeyRepository apiKeyRepository;

    public void updatePuuids() {
        List<Player> players = playerRepository.findAll();

        for (Player player : players) {
            try {
                List<String> downloadedIds = downloadPuuid(player.getPlayerName());

                Player existingPlayer = playerRepository.findByPlayerName(player.getPlayerName());
                if (existingPlayer != null) {
                    existingPlayer.setPuuid(downloadedIds.get(0));
                    existingPlayer.setEncryptedAccountId(downloadedIds.get(1));
                    playerRepository.save(existingPlayer);
                }
            } catch (Exception e) {
                log.error("Error while updating puuid for player: " + player.getPlayerName());
            }
            playerRepository.save(player);
        }
    }

    private List<String> downloadPuuid(String playerName) throws IOException, InterruptedException {
        String url = BASE_URL + playerName;
        String API_KEY = apiKeyRepository.findAll().get(0).getApiKey();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = mapper.readValue(response.body(), Map.class);

        return List.of(data.get("puuid"), data.get("id"));
    }
}
