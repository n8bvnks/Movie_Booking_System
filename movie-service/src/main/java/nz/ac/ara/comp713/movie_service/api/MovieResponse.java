package nz.ac.ara.comp713.movie_service.api;

// the shape of a movie response for booking service/controller

import java.time.LocalDate;
 
public record MovieResponse(
        String movietitle,
        LocalDate date,
        String time,
        int seats
) {
}
 