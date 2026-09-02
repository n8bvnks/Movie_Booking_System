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
        Movie movie = findOrThrow(movieTitle, date, time);

        return new MovieResponse(
                movie.getMovieTitle(),
                movie.getDate(),
                movie.getTime(),
                movie.getSeats()
        );
    }

    // called after booking-service has already saved a booking - actually
    // reduces the remaining seats for this showing
    public MovieResponse decrementSeats(String movieTitle, LocalDate date, String time, int seatsRequested) {
        Movie movie = findOrThrow(movieTitle, date, time);

        if (movie.getSeats() < seatsRequested) {
            throw new NotEnoughSeatsException(movieTitle, seatsRequested, movie.getSeats());
        }

        movie.decrementSeats(seatsRequested);
        Movie saved = repository.save(movie);

        return new MovieResponse(
                saved.getMovieTitle(),
                saved.getDate(),
                saved.getTime(),
                saved.getSeats()
        );
    }

    private Movie findOrThrow(String movieTitle, LocalDate date, String time) {
        return repository.findByMovieTitleAndDateAndTime(movieTitle, date, time)
                .orElseThrow(() -> new MovieNotFoundException(movieTitle, date.toString(), time));
    }
}