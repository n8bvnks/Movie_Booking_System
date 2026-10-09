package nz.ac.ara.comp713.movie_service.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

//format of a new showing sent by the admin, every field is checked before anything is saved
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