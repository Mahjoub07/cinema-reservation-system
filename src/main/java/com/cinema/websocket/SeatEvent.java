package com.cinema.websocket;

import java.time.LocalDateTime;

public class SeatEvent {

    public enum Action {
        SELECTED,
        RELEASED,
        LOCKED,
        BOOKED,
        TIMEOUT
    }

    private Long movieId;
    private String showTime;
    private String seatNumber;
    private Action action;
    private String sessionId;
    private LocalDateTime timestamp;

    public SeatEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public SeatEvent(Long movieId, String showTime, String seatNumber, Action action, String sessionId) {
        this();
        this.movieId = movieId;
        this.showTime = showTime;
        this.seatNumber = seatNumber;
        this.action = action;
        this.sessionId = sessionId;
    }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
