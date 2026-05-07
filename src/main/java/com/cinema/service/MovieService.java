package com.cinema.service;

import com.cinema.dto.MovieDTO;
import com.cinema.exception.BadRequestException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.Movie;
import com.cinema.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private static final String UPLOAD_DIR = "uploads/posters/";
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
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
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public void updateMovieSeats(Movie movie) {
        movieRepository.save(movie);
    }

    public String uploadPoster(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only image files (JPEG, PNG, GIF, WebP) are allowed");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = file.getOriginalFilename();
        String safeName = originalName != null ? originalName.replaceAll("[^a-zA-Z0-9.\\-]", "_") : "poster";
        String fileName = UUID.randomUUID() + "_" + safeName;
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return "/uploads/posters/" + fileName;
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
        movie.setPrice(dto.getPrice());
        return movie;
    }
}