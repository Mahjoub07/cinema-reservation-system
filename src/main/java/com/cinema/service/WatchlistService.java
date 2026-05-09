package com.cinema.service;

import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import com.cinema.model.User;
import com.cinema.model.Watchlist;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.UserRepository;
import com.cinema.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public WatchlistService(WatchlistRepository watchlistRepository,
                           UserRepository userRepository,
                           MovieRepository movieRepository) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public List<Watchlist> getUserWatchlist(String email) {
        User user = findUserByEmail(email);
        return watchlistRepository.findByUser(user);
    }

    @Transactional
    public Watchlist addToWatchlist(String email, Long movieId) {
        User user = findUserByEmail(email);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        if (watchlistRepository.existsByUserAndMovie(user, movie)) {
            throw new BadRequestException("Movie is already in your watchlist");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setMovie(movie);
        return watchlistRepository.save(watchlist);
    }

    @Transactional
    public void removeFromWatchlist(String email, Long movieId) {
        User user = findUserByEmail(email);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        watchlistRepository.deleteByUserAndMovie(user, movie);
    }

    @Transactional(readOnly = true)
    public boolean isInWatchlist(String email, Long movieId) {
        User user = findUserByEmail(email);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        return watchlistRepository.existsByUserAndMovie(user, movie);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
