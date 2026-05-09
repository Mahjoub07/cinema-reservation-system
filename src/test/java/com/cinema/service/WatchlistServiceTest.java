package com.cinema.service;

import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.model.Watchlist;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.UserRepository;
import com.cinema.repository.WatchlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private WatchlistService watchlistService;

    private User testUser;
    private Movie testMovie;
    private Watchlist testWatchlist;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setPassword("password");

        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Inception");
        testMovie.setGenre("Sci-Fi");
        testMovie.setDuration(148);
        testMovie.setAvailableSeats(100);

        testWatchlist = new Watchlist();
        testWatchlist.setId(1L);
        testWatchlist.setUser(testUser);
        testWatchlist.setMovie(testMovie);
        testWatchlist.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldGetUserWatchlistSuccessfully() {
        List<Watchlist> watchlists = Arrays.asList(testWatchlist);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(watchlistRepository.findByUser(testUser)).thenReturn(watchlists);

        List<Watchlist> result = watchlistService.getUserWatchlist("user@test.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getMovie().getTitle());
        verify(userRepository).findByEmail("user@test.com");
    }

    @Test
    void shouldGetUserWatchlistEmpty() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(watchlistRepository.findByUser(testUser)).thenReturn(Arrays.asList());

        List<Watchlist> result = watchlistService.getUserWatchlist("user@test.com");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> watchlistService.getUserWatchlist("nonexistent@test.com"));

        verify(userRepository).findByEmail("nonexistent@test.com");
    }

    @Test
    void shouldAddToWatchlistSuccessfully() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(watchlistRepository.existsByUserAndMovie(testUser, testMovie)).thenReturn(false);
        when(watchlistRepository.save(any(Watchlist.class))).thenReturn(testWatchlist);

        Watchlist result = watchlistService.addToWatchlist("user@test.com", 1L);

        assertNotNull(result);
        assertEquals("Inception", result.getMovie().getTitle());
        verify(watchlistRepository).save(any(Watchlist.class));
    }

    @Test
    void shouldThrowBadRequestWhenMovieAlreadyInWatchlist() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(watchlistRepository.existsByUserAndMovie(testUser, testMovie)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> watchlistService.addToWatchlist("user@test.com", 1L));

        assertEquals("Movie is already in your watchlist", exception.getMessage());
        verify(watchlistRepository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundWhenMovieNotFound() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> watchlistService.addToWatchlist("user@test.com", 999L));

        verify(watchlistRepository, never()).save(any());
    }

    @Test
    void shouldRemoveFromWatchlistSuccessfully() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        watchlistService.removeFromWatchlist("user@test.com", 1L);

        verify(watchlistRepository).deleteByUserAndMovie(testUser, testMovie);
    }

    @Test
    void shouldThrowResourceNotFoundWhenRemovingMovieNotFound() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> watchlistService.removeFromWatchlist("user@test.com", 999L));

        verify(watchlistRepository, never()).deleteByUserAndMovie(any(), any());
    }

    @Test
    void shouldCheckIfMovieInWatchlist() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(watchlistRepository.existsByUserAndMovie(testUser, testMovie)).thenReturn(true);

        boolean result = watchlistService.isInWatchlist("user@test.com", 1L);

        assertTrue(result);
        verify(watchlistRepository).existsByUserAndMovie(testUser, testMovie);
    }

    @Test
    void shouldCheckIfMovieNotInWatchlist() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(watchlistRepository.existsByUserAndMovie(testUser, testMovie)).thenReturn(false);

        boolean result = watchlistService.isInWatchlist("user@test.com", 1L);

        assertFalse(result);
    }

    @Test
    void shouldThrowResourceNotFoundWhenCheckingMovieNotFound() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(testUser));
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> watchlistService.isInWatchlist("user@test.com", 999L));

        verify(watchlistRepository, never()).existsByUserAndMovie(any(), any());
    }
}

