package nz.ac.ara.comp713.movie_service.repository;

import nz.ac.ara.comp713.movie_service.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByMovieTitleAndDateAndTime(String movieTitle, LocalDate date, String time);
}