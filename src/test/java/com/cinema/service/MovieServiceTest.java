package com.cinema.service;

import com.cinema.dto.MovieDTO;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import com.cinema.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private MovieDTO movieDTO;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setDuration(148);
        movie.setAvailableSeats(100);

        movieDTO = new MovieDTO(1L, "Inception", "A mind-bending thriller", "Sci-Fi", 148, null, 100, null);
    }

    @Test
    void shouldAddMovieSuccessfully() {
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieDTO result = movieService.addMovie(movieDTO);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void shouldGetAllMovies() {
        when(movieRepository.findAll()).thenReturn(Arrays.asList(movie));

        List<MovieDTO> result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void shouldGetMovieById() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenMovieNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> movieService.getMovieById(99L));

        assertEquals("Movie not found", exception.getMessage());
    }

    @Test
    void shouldSearchMoviesByTitle() {
        when(movieRepository.findByTitleContainingIgnoreCase("inc"))
                .thenReturn(Arrays.asList(movie));

        List<MovieDTO> result = movieService.searchByTitle("inc");

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void shouldDeleteMovie() {
        doNothing().when(movieRepository).deleteById(1L);

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldUpdateMovie() {
        MovieDTO updated = new MovieDTO(1L, "Inception 2", "Sequel", "Sci-Fi", 160, null, 80, null);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieDTO result = movieService.updateMovie(1L, updated);

        assertNotNull(result);
        assertEquals("Inception 2", result.getTitle());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void shouldSearchMoviesByGenre() {
        when(movieRepository.findByGenre("Sci-Fi")).thenReturn(Arrays.asList(movie));

        List<MovieDTO> result = movieService.getByGenre("Sci-Fi");

        assertEquals(1, result.size());
        assertEquals("Sci-Fi", result.get(0).getGenre());
    }
}