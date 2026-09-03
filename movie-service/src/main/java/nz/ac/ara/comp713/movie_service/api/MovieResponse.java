package nz.ac.ara.comp713.movie_service.api;

import java.time.LocalDate;

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