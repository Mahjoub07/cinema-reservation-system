package com.cinema.dto;

import java.time.LocalDateTime;

public class MovieDTO {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private Integer duration;
    private LocalDateTime showTime;
    private Integer availableSeats;
    private String posterUrl;
    private Double price;

    public MovieDTO() {}

    public MovieDTO(Long id, String title, String description, String genre,
                   Integer duration, LocalDateTime showTime, Integer availableSeats, String posterUrl, Double price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.showTime = showTime;
        this.availableSeats = availableSeats;
        this.posterUrl = posterUrl;
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
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
