package com.cinema.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private Integer numberOfSeats;
    private LocalDateTime bookingDate;
    private String status;
    private Double totalPrice;
    private List<Integer> seatNumbers;
    private LocalDateTime showTime;
    private String verificationToken;
    private String qrCode;

    public BookingDTO() {}

    public BookingDTO(Long id, Long userId, String userEmail, Long movieId,
                     String movieTitle, Integer numberOfSeats, LocalDateTime bookingDate, String status, Double totalPrice, List<Integer> seatNumbers, LocalDateTime showTime, String verificationToken) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.numberOfSeats = numberOfSeats;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.seatNumbers = seatNumbers;
        this.showTime = showTime;
        this.verificationToken = verificationToken;
    }

    public BookingDTO(Long id, Long userId, String userEmail, String userName, Long movieId,
                     String movieTitle, String moviePosterUrl, Integer numberOfSeats, LocalDateTime bookingDate,
                     String status, Double totalPrice, List<Integer> seatNumbers, LocalDateTime showTime,
                     String verificationToken, String qrCode) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.numberOfSeats = numberOfSeats;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.seatNumbers = seatNumbers;
        this.showTime = showTime;
        this.verificationToken = verificationToken;
        this.qrCode = qrCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getMoviePosterUrl() { return moviePosterUrl; }
    public void setMoviePosterUrl(String moviePosterUrl) { this.moviePosterUrl = moviePosterUrl; }
    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public List<Integer> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<Integer> seatNumbers) { this.seatNumbers = seatNumbers; }
    public LocalDateTime getShowTime() { return showTime; }
    public void setShowTime(LocalDateTime showTime) { this.showTime = showTime; }
    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
