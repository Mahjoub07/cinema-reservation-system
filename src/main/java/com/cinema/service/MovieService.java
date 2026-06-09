package com.cinema.service;

import com.cinema.dto.MovieDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
// movie service
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final BookingRepository bookingRepository;
    private final SupabaseStorageService supabaseStorageService;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public MovieService(MovieRepository movieRepository, BookingRepository bookingRepository, SupabaseStorageService supabaseStorageService) {
        this.movieRepository = movieRepository;
        this.bookingRepository = bookingRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    public MovieDTO addMovie(MovieDTO movieDTO) {
        Movie movie = convertToEntity(movieDTO);
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
    }

    public MovieDTO getMovieDTOById(Long id) {
        return convertToDTO(getMovieById(id));
    }

    public List<MovieDTO> searchByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO updateMovie(Long id, MovieDTO movieDTO) {
        Movie movie = getMovieById(id);
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setGenre(movieDTO.getGenre());
        movie.setDuration(movieDTO.getDuration());
        movie.setShowTime(movieDTO.getShowTime());
        movie.setAvailableSeats(movieDTO.getAvailableSeats());
        movie.setPrice(movieDTO.getPrice());
        movie.setPosterUrl(movieDTO.getPosterUrl());
        movie.setBackdropUrl(movieDTO.getBackdropUrl());
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        bookingRepository.deleteByMovieIdIn(List.of(id));
        movieRepository.deleteById(id);
    }

    @Transactional
    public void bulkDeleteMovies(List<Long> ids) {
        bookingRepository.deleteByMovieIdIn(ids);
        movieRepository.deleteAllById(ids);
    }

    public void updateMovieSeats(Movie movie) {
        movieRepository.save(movie);
    }

    public String uploadPoster(MultipartFile file) {
        return uploadImage(file, "poster");
    }

    public String uploadBackdrop(MultipartFile file) {
        return uploadImage(file, "backdrop");
    }

    private String uploadImage(MultipartFile file, String defaultName) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only image files (JPEG, PNG, GIF, WebP) are allowed");
        }

        String originalName = file.getOriginalFilename();
        String safeName = originalName != null ? originalName.replaceAll("[^a-zA-Z0-9.\\-]", "_") : defaultName;
        String fileName = UUID.randomUUID() + "_" + safeName;

        try {
            return supabaseStorageService.uploadFile("movies", fileName, file.getBytes(), contentType);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read uploaded file: " + e.getMessage());
        }
    }

    private MovieDTO convertToDTO(Movie movie) {
        return new MovieDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getDescription(),
            movie.getGenre(),
            movie.getDuration(),
            movie.getShowTime(),
            movie.getAvailableSeats(),
            movie.getPosterUrl(),
            movie.getBackdropUrl(),
            movie.getPrice()
        );
    }

    private Movie convertToEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDuration(dto.getDuration());
        movie.setShowTime(dto.getShowTime());
        movie.setAvailableSeats(dto.getAvailableSeats());
        movie.setPosterUrl(dto.getPosterUrl());
        movie.setBackdropUrl(dto.getBackdropUrl());
        movie.setPrice(dto.getPrice());
        return movie;
    }
}