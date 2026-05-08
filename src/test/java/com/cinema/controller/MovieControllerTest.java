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
        movieDTO = new MovieDTO(1L, "Inception", "A mind-bending thriller", "Sci-Fi", 148, null, 100, null, null, null);
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

    @Test
    void shouldGetMoviesByGenre() {
        when(movieService.getByGenre("Sci-Fi")).thenReturn(Arrays.asList(movieDTO));

        List<MovieDTO> result = movieController.getMoviesByGenre("Sci-Fi").getBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getTitle());
    }

    @Test
    void shouldUpdateMovie() {
        when(movieService.updateMovie(eq(1L), any(MovieDTO.class))).thenReturn(movieDTO);

        MovieDTO result = movieController.updateMovie(1L, movieDTO).getBody();

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        verify(movieService, times(1)).updateMovie(1L, movieDTO);
    }

    @Test
    void shouldBulkDeleteMovies() {
        doNothing().when(movieService).bulkDeleteMovies(anyList());

        var response = movieController.bulkDeleteMovies(Arrays.asList(1L, 2L));

        assertEquals(204, response.getStatusCode().value());
        verify(movieService, times(1)).bulkDeleteMovies(Arrays.asList(1L, 2L));
    }

    @Test
    void shouldUploadPoster() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(movieService.uploadPoster(file)).thenReturn("/uploads/posters/test.jpg");

        var response = movieController.uploadPoster(file);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("/uploads/posters/test.jpg", response.getBody().get("url"));
    }

    @Test
    void shouldReturnErrorWhenUploadPosterValidationFails() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(movieService.uploadPoster(file)).thenThrow(new RuntimeException("Invalid file type"));

        assertThrows(RuntimeException.class, () -> movieController.uploadPoster(file));
    }

    @Test
    void shouldUploadBackdrop() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(movieService.uploadBackdrop(file)).thenReturn("/uploads/backdrops/test.jpg");

        var response = movieController.uploadBackdrop(file);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("/uploads/backdrops/test.jpg", response.getBody().get("url"));
    }

    @Test
    void shouldReturnErrorWhenUploadBackdropValidationFails() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(movieService.uploadBackdrop(file)).thenThrow(new RuntimeException("Invalid file type"));

        assertThrows(RuntimeException.class, () -> movieController.uploadBackdrop(file));
    }
}