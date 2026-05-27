package com.safaritrip.management.repository;

import com.safaritrip.management.model.Booking;
import com.safaritrip.management.model.BookingStatus; // Import BookingStatus
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByTouristId(Long touristId);
    List<Booking> findAllByGuideId(Long guideId);

    // New method to count bookings by their status
    long countByStatus(BookingStatus status);
}