package nz.ac.ara.comp713.movie_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(
    name = "MovieShowing",
    uniqueConstraints = @UniqueConstraint(columnNames = {"movieTitle", "date", "time"})
)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovieShowing;

    private String movieTitle;

    private String time;

    private LocalDate date;

    private int seats;

    protected Movie() {
        // required by JPA
    }

    public Movie(String movieTitle, String time, LocalDate date, int seats) {
        this.movieTitle = movieTitle;
        this.time = time;
        this.date = date;
        this.seats = seats;
    }

    public Long getIdMovieShowing() {
        return idMovieShowing;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getTime() {
        return time;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getSeats() {
        return seats;
    }

    // called only by MovieCatalogue, never from outside this class
    public void decrementSeats(int amount) {
        this.seats -= amount;
    }
}