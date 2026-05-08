package com.cinema.repository;

import com.cinema.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN FETCH b.movie JOIN FETCH b.user WHERE b.user.id = :userId")
    List<Booking> findByUserIdWithDetails(Long userId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.movie JOIN FETCH b.user")
    List<Booking> findAllWithDetails();

    List<Booking> findByUserId(Long userId);
    List<Booking> findByMovieId(Long movieId);

    List<Booking> findByMovieIdAndStatus(Long movieId, String status);

    @Query("SELECT b FROM Booking b WHERE b.movie.id = :movieId AND b.showTime = :showTime AND b.status = :status")
    List<Booking> findByMovieIdAndShowTimeAndStatus(@Param("movieId") Long movieId, @Param("showTime") LocalDateTime showTime, @Param("status") String status);

    @Modifying
    @Query("DELETE FROM Booking b WHERE b.movie.id IN :movieIds")
    void deleteByMovieIdIn(@Param("movieIds") List<Long> movieIds);
}