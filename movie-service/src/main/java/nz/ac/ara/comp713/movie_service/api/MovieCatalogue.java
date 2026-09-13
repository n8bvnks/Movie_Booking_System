package nz.ac.ara.comp713.movie_service.api;

import org.springframework.stereotype.Service;

import nz.ac.ara.comp713.movie_service.domain.Movie;
import nz.ac.ara.comp713.movie_service.repository.MovieRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class MovieCatalogue {

    private final MovieRepository repository;

    public MovieCatalogue(MovieRepository repository) {
        this.repository = repository;
    }

    // used by the landing page - every showing currently in the database, ordered by date
    public List<MovieResponse> listAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(MovieResponse::date))
                .toList();
    }

    //called by - check availability, to movie client - movie controller to check a booking
    //find in repository or throw MovieNotFoundException, method at bottom of file
    //returns movie response
    public MovieResponse checkBooking(String movieTitle, LocalDate date, String time) {
        Movie movie = findOrThrow(movieTitle, date, time);
        return toResponse(movie);
    }

    //called by booking service if booking is valid, checks enough seats, if not throw not enough seats exception
    public MovieResponse decrementSeats(String movieTitle, LocalDate date, String time, int seatsRequested) {
        Movie movie = findOrThrow(movieTitle, date, time);

        if (movie.getSeats() < seatsRequested) {
            throw new NotEnoughSeatsException(movieTitle, seatsRequested, movie.getSeats());
        }

        movie.decrementSeats(seatsRequested);
        return toResponse(repository.save(movie));
    }

    // called when a booking is cancelled/deleted, or its seat count reduced
    // during an update - gives the seats back
    public MovieResponse incrementSeats(String movieTitle, LocalDate date, String time, int seats) {
        Movie movie = findOrThrow(movieTitle, date, time);
        movie.incrementSeats(seats);
        return toResponse(repository.save(movie));
    }

    private Movie findOrThrow(String movieTitle, LocalDate date, String time) {
        return repository.findByMovieTitleAndDateAndTime(movieTitle, date, time)
                .orElseThrow(() -> new MovieNotFoundException(movieTitle, date.toString(), time));
    }

    private MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getMovieTitle(),
                movie.getGenre(),
                movie.getDescription(),
                movie.getRuntime(),
                movie.getDate(),
                movie.getTime(),
                movie.getSeats()
        );
    }
}