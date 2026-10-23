package nz.ac.ara.comp713.booking_service.live;

import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;

// one showing as it is sent to the browsers inside a live event
// the date is a plain string like 2026-10-15 so it is written to json the same way the REST api writes it
public record ShowingData(
        String movieTitle,
        String genre,
        String description,
        int runtime,
        String date,
        String time,
        int seats
) {
    // turns the movie-service response into the shape sent over the websocket
    public static ShowingData from(MovieResponse movie) {
        return new ShowingData(
                movie.movieTitle(),
                movie.genre(),
                movie.description(),
                movie.runtime(),
                movie.date().toString(),
                movie.time(),
                movie.seats()
        );
    }
}