package com.safaritrip.management.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
public class BookingDto {

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must be in the present or future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @NotEmpty(message = "Time slot is required")
    private String timeSlot;

    @NotNull(message = "National Park is required")
    private Long nationalParkId;

    @Min(value = 1, message = "There must be at least 1 person")
    private int numberOfPeople;

    private boolean guideOption;

    @Min(value = 1, message = "Ride must be at least 1 hour")
    private int rideHours;
}