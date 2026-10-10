package nz.ac.ara.comp713.booking_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// what the admin sends to POST /api/v1/admin/showings
// validated here first so bad input is rejected before movie-service is called
// movie-service validates again with the same rules 
public record CreateShowingRequest(
        @NotBlank @Size(max = 255) String movieTitle,
        @NotBlank @Size(max = 255) String genre,
        @NotBlank @Size(max = 255) String description,
        @Positive int runtime,
        @NotNull LocalDate date,
        @NotBlank @Size(max = 255) String time,
        @Positive int seats
) {
}