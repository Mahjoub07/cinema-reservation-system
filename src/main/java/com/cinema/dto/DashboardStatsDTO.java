package com.cinema.dto;

public class DashboardStatsDTO {
    private long totalUsers;
    private long totalBookings;
    private long totalMovies;
    private long activeBookings;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(long totalUsers, long totalBookings, long totalMovies, long activeBookings) {
        this.totalUsers = totalUsers;
        this.totalBookings = totalBookings;
        this.totalMovies = totalMovies;
        this.activeBookings = activeBookings;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
    public long getTotalMovies() { return totalMovies; }
    public void setTotalMovies(long totalMovies) { this.totalMovies = totalMovies; }
    public long getActiveBookings() { return activeBookings; }
    public void setActiveBookings(long activeBookings) { this.activeBookings = activeBookings; }
}
