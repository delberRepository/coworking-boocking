package com.delber.coworking_booking.repository;
import com.delber.coworking_booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface IBookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b
        WHERE b.resource.id = :resourceId
        AND b.status = 'CONFIRMED'
        AND (
            :start < b.endTime AND :end > b.startTime
        )
    """)
    List<Booking> findOverlappingBookings(
            Long resourceId,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
        SELECT b FROM Booking b
        WHERE b.resource.id = :resourceId
        AND b.id <> :bookingId
        AND b.status = 'CONFIRMED'
        AND (
            :start < b.endTime AND :end > b.startTime
        )
    """)
    List<Booking> findOverlappingBookingsExcludingCurrent(
            Long resourceId,
            Long bookingId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findByUserId(Long userId);
    List<Booking> findByResourceId(Long resourceId);

}
