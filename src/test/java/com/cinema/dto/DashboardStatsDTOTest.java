package com.cinema.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DashboardStatsDTOTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        DashboardStatsDTO dto = new DashboardStatsDTO();
        assertNotNull(dto);
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        DashboardStatsDTO dto = new DashboardStatsDTO(1L, 2L, 3L, 4L);
        assertEquals(1L, dto.getTotalUsers());
        assertEquals(2L, dto.getTotalBookings());
        assertEquals(3L, dto.getTotalMovies());
        assertEquals(4L, dto.getActiveBookings());
    }

    @Test
    void shouldSetAndGetFields() {
        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setTotalUsers(10L);
        dto.setTotalBookings(20L);
        dto.setTotalMovies(5L);
        dto.setActiveBookings(8L);

        assertEquals(10L, dto.getTotalUsers());
        assertEquals(20L, dto.getTotalBookings());
        assertEquals(5L, dto.getTotalMovies());
        assertEquals(8L, dto.getActiveBookings());
    }
}
