package com.safaritrip.management.service;

import com.safaritrip.management.dto.BookingDto;
import com.safaritrip.management.model.Booking;
import com.safaritrip.management.model.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDto bookingDto, String username);

    List<Booking> findBookingsByUsername(String username);

    List<Booking> findAllBookings();

    void updateBookingStatus(Long bookingId, BookingStatus status);

    // Add this new method
    void assignGuideAndVehicle(Long bookingId, Long guideId, Long vehicleId);
}