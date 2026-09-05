package nz.ac.ara.comp713.booking_service.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

//format of a booking request from client
public record BookingRequest(
        @NotBlank
        String name,

        @NotBlank
        String movieTitle,

        @NotBlank
        String timeslot,

        @NotNull
        @FutureOrPresent
        LocalDate date,

        @Positive
        int seats
) {
}