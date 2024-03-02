package com.odazie.simpleblog.service;

import com.odazie.simpleblog.model.ApiKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.odazie.simpleblog.repository.ApiKeyRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RefreshingService {

    private String API_KEY = "RGAPI-bf38748a-5ec7-42aa-9978-895bc6686ce2";

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    public void downloadAndSaveAPIKey() {
        ApiKey apiKey = ApiKey.builder().apiKey(API_KEY).build();
        apiKeyRepository.save(apiKey);
    }
}
