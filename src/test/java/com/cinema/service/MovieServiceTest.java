package com.cinema.service;

import com.cinema.dto.MovieDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import com.cinema.repository.BookingRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private com.cinema.service.SupabaseStorageService supabaseStorageService;

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

        movieDTO = new MovieDTO(1L, "Inception", "A mind-bending thriller", "Sci-Fi", 148, null, 100, null, null, null);
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
        doNothing().when(bookingRepository).deleteByMovieIdIn(anyList());
        doNothing().when(movieRepository).deleteById(1L);

        movieService.deleteMovie(1L);

        verify(bookingRepository, times(1)).deleteByMovieIdIn(anyList());
        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldBulkDeleteMovies() {
        doNothing().when(bookingRepository).deleteByMovieIdIn(anyList());
        doNothing().when(movieRepository).deleteAllById(anyList());

        movieService.bulkDeleteMovies(Arrays.asList(1L, 2L));

        verify(bookingRepository, times(1)).deleteByMovieIdIn(Arrays.asList(1L, 2L));
        verify(movieRepository, times(1)).deleteAllById(Arrays.asList(1L, 2L));
    }

    @Test
    void shouldUpdateMovie() {
        MovieDTO updated = new MovieDTO(1L, "Inception 2", "Sequel", "Sci-Fi", 160, null, 80, null, null, null);

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

    @Test
    void shouldUpdateMovieWithPriceAndPosterUrl() {
        MovieDTO updated = new MovieDTO(1L, "Inception", "Updated", "Sci-Fi", 160, null, 80, "/uploads/poster.jpg", "/uploads/backdrop.jpg", 15.0);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        MovieDTO result = movieService.updateMovie(1L, updated);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        assertEquals("Sci-Fi", result.getGenre());
    }

    @Test
    void shouldThrowExceptionWhenUploadingNullFile() {
        assertThrows(BadRequestException.class, () -> movieService.uploadPoster(null));
    }

    @Test
    void shouldThrowExceptionWhenUploadingInvalidFileType() {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");

        assertThrows(BadRequestException.class, () -> movieService.uploadPoster(file));
    }

    @Test
    void shouldThrowExceptionWhenUploadingEmptyFile() {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> movieService.uploadPoster(file));
    }

    @Test
    void shouldThrowExceptionWhenUploadingFileWithNullContentType() {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(null);

        assertThrows(BadRequestException.class, () -> movieService.uploadPoster(file));
    }

    @Test
    void shouldUploadPosterSuccessfully() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("poster.jpg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(supabaseStorageService.uploadFile(anyString(), anyString(), any(byte[].class), anyString()))
                .thenReturn("https://storage.com/posters/test.jpg");

        String result = movieService.uploadPoster(file);

        assertEquals("https://storage.com/posters/test.jpg", result);
    }

    @Test
    void shouldUploadBackdropSuccessfully() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("backdrop.jpg");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(supabaseStorageService.uploadFile(anyString(), anyString(), any(byte[].class), anyString()))
                .thenReturn("https://storage.com/backdrops/test.jpg");

        String result = movieService.uploadBackdrop(file);

        assertEquals("https://storage.com/backdrops/test.jpg", result);
    }

    @Test
    void shouldThrowExceptionWhenUploadingNullBackdrop() {
        assertThrows(BadRequestException.class, () -> movieService.uploadBackdrop(null));
    }

    @Test
    void shouldThrowExceptionWhenUploadingEmptyBackdrop() {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> movieService.uploadBackdrop(file));
    }

    @Test
    void shouldThrowExceptionWhenUploadingBackdropWithInvalidType() {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");

        assertThrows(BadRequestException.class, () -> movieService.uploadBackdrop(file));
    }

    @Test
    void shouldThrowExceptionWhenUploadingBackdropWithNullContentType() {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(null);

        assertThrows(BadRequestException.class, () -> movieService.uploadBackdrop(file));
    }

    @Test
    void shouldGetMovieDTOById() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        MovieDTO result = movieService.getMovieDTOById(1L);

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
        assertEquals(1L, result.getId());
        assertEquals("Sci-Fi", result.getGenre());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingMovieDTOByIdNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieDTOById(99L));
    }

    @Test
    void shouldUpdateMovieSeats() {
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        movieService.updateMovieSeats(movie);

        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void shouldThrowExceptionWhenUploadingPosterWithIOException() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("poster.jpg");
        when(file.getBytes()).thenThrow(new java.io.IOException("Read error"));

        assertThrows(BadRequestException.class, () -> movieService.uploadPoster(file));
    }

    @Test
    void shouldThrowExceptionWhenUploadingBackdropWithIOException() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("backdrop.jpg");
        when(file.getBytes()).thenThrow(new java.io.IOException("Read error"));

        assertThrows(BadRequestException.class, () -> movieService.uploadBackdrop(file));
    }

    @Test
    void shouldHandleFileNameWithSpecialCharacters() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("poster@#$%.png");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(supabaseStorageService.uploadFile(anyString(), anyString(), any(byte[].class), anyString()))
                .thenReturn("https://storage.com/posters/test.png");

        String result = movieService.uploadPoster(file);

        assertEquals("https://storage.com/posters/test.png", result);
        verify(supabaseStorageService, times(1)).uploadFile(anyString(), anyString(), any(byte[].class), anyString());
    }

    @Test
    void shouldHandleFileNameWithNullOriginalFilename() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/webp");
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(supabaseStorageService.uploadFile(anyString(), anyString(), any(byte[].class), anyString()))
                .thenReturn("https://storage.com/posters/test.webp");

        String result = movieService.uploadPoster(file);

        assertEquals("https://storage.com/posters/test.webp", result);
    }

    @Test
    void shouldHandleFileNameWithNullOriginalFilenameForBackdrop() throws Exception {
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/gif");
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(supabaseStorageService.uploadFile(anyString(), anyString(), any(byte[].class), anyString()))
                .thenReturn("https://storage.com/backdrops/test.gif");

        String result = movieService.uploadBackdrop(file);

        assertEquals("https://storage.com/backdrops/test.gif", result);
    }

    @Test
    void shouldGetMovieDTOByIdWithAllFields() {
        Movie movieWithAllFields = new Movie();
        movieWithAllFields.setId(2L);
        movieWithAllFields.setTitle("Interstellar");
        movieWithAllFields.setDescription("A complex sci-fi adventure");
        movieWithAllFields.setGenre("Sci-Fi");
        movieWithAllFields.setDuration(169);
        movieWithAllFields.setAvailableSeats(50);
        movieWithAllFields.setPrice(15.99);
        movieWithAllFields.setPosterUrl("/uploads/interstellar-poster.jpg");
        movieWithAllFields.setBackdropUrl("/uploads/interstellar-backdrop.jpg");

        when(movieRepository.findById(2L)).thenReturn(Optional.of(movieWithAllFields));

        MovieDTO result = movieService.getMovieDTOById(2L);

        assertNotNull(result);
        assertEquals("Interstellar", result.getTitle());
        assertEquals("A complex sci-fi adventure", result.getDescription());
        assertEquals(169, result.getDuration());
        assertEquals(50, result.getAvailableSeats());
        assertEquals(15.99, result.getPrice());
        assertEquals("/uploads/interstellar-poster.jpg", result.getPosterUrl());
        assertEquals("/uploads/interstellar-backdrop.jpg", result.getBackdropUrl());
    }

    @Test
    void shouldDeleteMovieCallBothRepositories() {
        doNothing().when(bookingRepository).deleteByMovieIdIn(List.of(1L));
        doNothing().when(movieRepository).deleteById(1L);

        movieService.deleteMovie(1L);

        verify(bookingRepository, times(1)).deleteByMovieIdIn(List.of(1L));
        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldBulkDeleteMoviesWithMultipleIds() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        doNothing().when(bookingRepository).deleteByMovieIdIn(ids);
        doNothing().when(movieRepository).deleteAllById(ids);

        movieService.bulkDeleteMovies(ids);

        verify(bookingRepository, times(1)).deleteByMovieIdIn(ids);
        verify(movieRepository, times(1)).deleteAllById(ids);
    }

    @Test
    void shouldReturnEmptyListWhenNoMoviesFound() {
        when(movieRepository.findAll()).thenReturn(Arrays.asList());

        List<MovieDTO> result = movieService.getAllMovies();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenSearchingByTitleWithNoResults() {
        when(movieRepository.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(Arrays.asList());

        List<MovieDTO> result = movieService.searchByTitle("NonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenSearchingByGenreWithNoResults() {
        when(movieRepository.findByGenre("Horror")).thenReturn(Arrays.asList());

        List<MovieDTO> result = movieService.getByGenre("Horror");

        assertTrue(result.isEmpty());
    }
}