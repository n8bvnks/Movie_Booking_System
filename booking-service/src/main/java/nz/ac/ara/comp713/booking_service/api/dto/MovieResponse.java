package nz.ac.ara.comp713.booking_service.api.dto;

import java.time.LocalDate;

// local copy - used by MovieClient to read movie-service's replies
public record MovieResponse(
        String movieTitle,
        String genre,
        String description,
        int runtime,
        LocalDate date,
        String time,
        int seats
) {
}