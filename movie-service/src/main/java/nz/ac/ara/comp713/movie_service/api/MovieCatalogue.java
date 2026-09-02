package nz.ac.ara.comp713.movie_service.api;

import org.springframework.stereotype.Service;

import nz.ac.ara.comp713.movie_service.domain.Movie;
import nz.ac.ara.comp713.movie_service.repository.MovieRepository;

import java.time.LocalDate;

@Service
public class MovieCatalogue {

    private final MovieRepository repository;

    public MovieCatalogue(MovieRepository repository) {
        this.repository = repository;
    }

    // given movie title, date and time from the controller (originally
    // sent by booking-service), check the database for a matching showing
    public MovieResponse checkBooking(String movieTitle, LocalDate date, String time) {
        Movie movie = repository.findByMovieTitleAndDateAndTime(movieTitle, date, time)
                .orElseThrow(() -> new MovieNotFoundException(movieTitle, date.toString(), time));

        return new MovieResponse(
                movie.getMovieTitle(),
                movie.getDate(),
                movie.getTime(),
                movie.getSeats()
        );
    }
}