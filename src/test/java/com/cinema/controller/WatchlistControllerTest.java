package com.cinema.controller;

import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.model.Watchlist;
import com.cinema.service.WatchlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistControllerTest {

    @Mock
    private WatchlistService watchlistService;

    @InjectMocks
    private WatchlistController watchlistController;

    private User testUser;
    private Movie testMovie;
    private Watchlist testWatchlist;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");

        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Inception");
        testMovie.setGenre("Sci-Fi");

        testWatchlist = new Watchlist();
        testWatchlist.setId(1L);
        testWatchlist.setUser(testUser);
        testWatchlist.setMovie(testMovie);
        testWatchlist.setCreatedAt(LocalDateTime.now());

        authentication = new UsernamePasswordAuthenticationToken("user@test.com", null);
    }

    @Test
    void shouldGetMyWatchlist() {
        List<Watchlist> watchlists = Arrays.asList(testWatchlist);
        when(watchlistService.getUserWatchlist("user@test.com")).thenReturn(watchlists);

        ResponseEntity<List<Watchlist>> response = watchlistController.getMyWatchlist(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Inception", response.getBody().get(0).getMovie().getTitle());
        verify(watchlistService).getUserWatchlist("user@test.com");
    }

    @Test
    void shouldGetMyWatchlistEmpty() {
        when(watchlistService.getUserWatchlist("user@test.com")).thenReturn(Arrays.asList());

        ResponseEntity<List<Watchlist>> response = watchlistController.getMyWatchlist(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void shouldAddToWatchlist() {
        when(watchlistService.addToWatchlist("user@test.com", 1L)).thenReturn(testWatchlist);

        ResponseEntity<Watchlist> response = watchlistController.addToWatchlist(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Inception", response.getBody().getMovie().getTitle());
        verify(watchlistService).addToWatchlist("user@test.com", 1L);
    }

    @Test
    void shouldRemoveFromWatchlist() {
        doNothing().when(watchlistService).removeFromWatchlist("user@test.com", 1L);

        ResponseEntity<Map<String, String>> response = watchlistController.removeFromWatchlist(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Removed from watchlist", response.getBody().get("message"));
        verify(watchlistService).removeFromWatchlist("user@test.com", 1L);
    }

    @Test
    void shouldCheckWatchlistWhenMovieInWatchlist() {
        when(watchlistService.isInWatchlist("user@test.com", 1L)).thenReturn(true);

        ResponseEntity<Map<String, Boolean>> response = watchlistController.checkWatchlist(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("inWatchlist"));
        verify(watchlistService).isInWatchlist("user@test.com", 1L);
    }

    @Test
    void shouldCheckWatchlistWhenMovieNotInWatchlist() {
        when(watchlistService.isInWatchlist("user@test.com", 1L)).thenReturn(false);

        ResponseEntity<Map<String, Boolean>> response = watchlistController.checkWatchlist(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().get("inWatchlist"));
    }
}

