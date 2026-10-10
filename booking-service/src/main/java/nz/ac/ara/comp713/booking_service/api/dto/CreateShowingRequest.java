package nz.ac.ara.comp713.booking_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

//format of a new showing sent by the admin to POST /api/v1/admin/showings, every field is checked before anything is saved
//validated here first so bad input is rejected before movie-service is called
//movie-service validates again with the same rules, so keep the two CreateShowingRequest files the same
//the sizes match the database columns, so a very long description gives a 400 instead of a database error
public record CreateShowingRequest(
        @NotBlank @Size(max = 255)
        String movieTitle,

        @NotBlank @Size(max = 255)
        String genre,

        @NotBlank @Size(max = 255)
        String description,

        //runtime in minutes, must be more than 0
        @Positive
        int runtime,

        //time of day like 12pm or 5pm, same format the seeded showings use
        @NotBlank @Size(max = 255)
        String time,

        @NotNull
        LocalDate date,

        //starting number of seats, must be more than 0
        @Positive
        int seats
) {
}