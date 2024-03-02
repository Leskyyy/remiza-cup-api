package com.odazie.simpleblog.controller;

import com.odazie.simpleblog.model.ApiKey;
import com.odazie.simpleblog.repository.ApiKeyRepository;
import com.odazie.simpleblog.repository.PlayerRepository;
import com.odazie.simpleblog.service.PlayerService;
import com.odazie.simpleblog.service.RefreshingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RefreshController {
    @Autowired
    private RefreshingService refreshingService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @GetMapping("/refresh")
    public ResponseEntity<HttpStatus> refresh() {
        refreshingService.downloadAndSaveAPIKey();
        // playerService.updatePuuids();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
