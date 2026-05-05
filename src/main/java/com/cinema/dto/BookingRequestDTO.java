package com.cinema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingRequestDTO {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least 1 seat is required")
    private Integer seats;

    public BookingRequestDTO() {}

    public BookingRequestDTO(Long movieId, Integer seats) {
        this.movieId = movieId;
        this.seats = seats;
    }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
}
