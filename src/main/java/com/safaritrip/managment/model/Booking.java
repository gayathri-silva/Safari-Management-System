package com.safaritrip.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tourist_id", nullable = false)
    private User tourist;

    @ManyToOne
    @JoinColumn(name = "national_park_id", nullable = false)
    private NationalPark nationalPark;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false)
    private String timeSlot;

    @Column(nullable = false)
    private int numberOfPeople;

    @Column(nullable = false)
    private boolean guideOption;

    @Column(nullable = false)
    private int rideHours;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Feedback feedback;

    // Add these two new fields for assignments
    @ManyToOne
    @JoinColumn(name = "guide_id") // This can be null
    private Guide guide;

    @ManyToOne
    @JoinColumn(name = "vehicle_id") // This can be null
    private Vehicle vehicle;
}