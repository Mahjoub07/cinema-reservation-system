package com.cinema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class BookingRequestDTO {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least 1 seat is required")
    private Integer seats;

    @NotNull(message = "Seat numbers are required")
    @NotEmpty(message = "At least one seat must be selected")
    private List<Integer> seatNumbers;

    @NotNull(message = "Show time is required")
    private LocalDateTime showTime;

    public BookingRequestDTO() {}

    public BookingRequestDTO(Long movieId, Integer seats, List<Integer> seatNumbers, LocalDateTime showTime) {
        this.movieId = movieId;
        this.seats = seats;
        this.seatNumbers = seatNumbers;
        this.showTime = showTime;
    }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
    public List<Integer> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<Integer> seatNumbers) { this.seatNumbers = seatNumbers; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
}
