package com.cinema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Movie entity.
 * Contains validation rules to ensure data integrity when creating/updating movies.
 */
public class MovieDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    // Optional: Movie plot summary
    private String description;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    @NotNull(message = "Show time is required")
    private LocalDateTime showTime;

    @NotNull(message = "Available seats is required")
    @Min(value = 1, message = "At least 1 seat is required")
    private Integer availableSeats;

    // Optional: URL to movie poster image (uploaded to Supabase Storage)
    private String posterUrl;

    // Optional: URL to movie backdrop image (horizontal, for carousel banners)
    private String backdropUrl;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    public MovieDTO() {}

    public MovieDTO(Long id, String title, String description, String genre,
                   Integer duration, LocalDateTime showTime, Integer availableSeats, String posterUrl, String backdropUrl, Double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.showTime = showTime;
        this.availableSeats = availableSeats;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getBackdropUrl() { return backdropUrl; }
    public void setBackdropUrl(String backdropUrl) { this.backdropUrl = backdropUrl; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
