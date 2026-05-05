package com.cinema.repository;

import com.cinema.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN FETCH b.movie JOIN FETCH b.user WHERE b.user.id = :userId")
    List<Booking> findByUserIdWithDetails(Long userId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.movie JOIN FETCH b.user")
    List<Booking> findAllWithDetails();

    List<Booking> findByUserId(Long userId);
    List<Booking> findByMovieId(Long movieId);
}