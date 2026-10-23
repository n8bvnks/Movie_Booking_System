package nz.ac.ara.comp713.movie_service.repository;

import nz.ac.ara.comp713.movie_service.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    //called by create booking in booking service to check if movie with that time and date exists
    Optional<Movie> findByMovieTitleAndDateAndTime(String movieTitle, LocalDate date, String time);

    //NEW - called when an admin deletes a whole movie, finds every showing with this title
    //the method name is turned into a sql query, same as the one above
    List<Movie> findByMovieTitle(String movieTitle);
}
