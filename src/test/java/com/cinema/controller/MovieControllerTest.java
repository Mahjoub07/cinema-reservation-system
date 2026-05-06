package com.cinema.controller;

import com.cinema.dto.MovieDTO;
import com.cinema.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private MovieDTO movieDTO;

    @BeforeEach
    void setUp() {
        movieDTO = new MovieDTO(1L, "Inception", "A mind-bending thriller", "Sci-Fi", 148, null, 100, null);
    }

    @Test
    void shouldGetAllMovies() {
        when(movieService.getAllMovies()).thenReturn(Arrays.asList(movieDTO));

        List<MovieDTO> result = movieController.getAllMovies().getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void shouldGetMovieById() {
        when(movieService.getMovieDTOById(1L)).thenReturn(movieDTO);

        MovieDTO result = movieController.getMovieById(1L).getBody();

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
    }

    @Test
    void shouldAddMovie() {
        when(movieService.addMovie(any(MovieDTO.class))).thenReturn(movieDTO);

        MovieDTO result = movieController.addMovie(movieDTO).getBody();

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        verify(movieService, times(1)).addMovie(movieDTO);
    }

    @Test
    void shouldDeleteMovie() {
        doNothing().when(movieService).deleteMovie(1L);

        movieController.deleteMovie(1L);

        verify(movieService, times(1)).deleteMovie(1L);
    }

    @Test
    void shouldSearchMovies() {
        when(movieService.searchByTitle("inc")).thenReturn(Arrays.asList(movieDTO));

        List<MovieDTO> result = movieController.searchMovies("inc").getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }
}