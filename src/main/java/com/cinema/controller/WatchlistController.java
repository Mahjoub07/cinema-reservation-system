package com.cinema.controller;

import com.cinema.model.Watchlist;
import com.cinema.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public ResponseEntity<List<Watchlist>> getMyWatchlist(Authentication authentication) {
        String email = authentication.getName();
        List<Watchlist> watchlist = watchlistService.getUserWatchlist(email);
        return ResponseEntity.ok(watchlist);
    }

    @PostMapping("/{movieId}")
    public ResponseEntity<Watchlist> addToWatchlist(
            @PathVariable Long movieId,
            Authentication authentication) {
        String email = authentication.getName();
        Watchlist item = watchlistService.addToWatchlist(email, movieId);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Map<String, String>> removeFromWatchlist(
            @PathVariable Long movieId,
            Authentication authentication) {
        String email = authentication.getName();
        watchlistService.removeFromWatchlist(email, movieId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Removed from watchlist");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{movieId}")
    public ResponseEntity<Map<String, Boolean>> checkWatchlist(
            @PathVariable Long movieId,
            Authentication authentication) {
        String email = authentication.getName();
        boolean isInWatchlist = watchlistService.isInWatchlist(email, movieId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("inWatchlist", isInWatchlist);
        return ResponseEntity.ok(response);
    }
}
