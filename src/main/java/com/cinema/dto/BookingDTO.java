package com.cinema.dto;

import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private Long movieId;
    private String movieTitle;
    private Integer numberOfSeats;
    private LocalDateTime bookingDate;
    private String status;
    private Double totalPrice;

    public BookingDTO() {}

    public BookingDTO(Long id, Long userId, String userEmail, Long movieId,
                     String movieTitle, Integer numberOfSeats, LocalDateTime bookingDate, String status, Double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.numberOfSeats = numberOfSeats;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
