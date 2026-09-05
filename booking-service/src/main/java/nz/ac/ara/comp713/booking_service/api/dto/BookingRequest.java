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
        //date can be past date - though this is not practical in real life this uses seeded data 
        //in the past before assignment is submitted

        //Otherwise I would add @FutureOrPresent
        LocalDate date,

        @Positive
        int seats
) {
}