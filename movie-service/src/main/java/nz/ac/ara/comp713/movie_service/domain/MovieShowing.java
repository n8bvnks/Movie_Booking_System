package nz.ac.ara.comp713.movie_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

//Entity reflecting MovieShowing database table
@Entity
@Table(name = "movies_showing")
public class MovieShowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long movieId;

    private String movietitle;

    private LocalDate date;

    private String time;

    private int seats;

    protected MovieShowing() {
        
    }

    public MovieShowing(Long movieId, String movietitle, LocalDate date, String time, int seats) {
        this.movieId = movieId;
        this.movietitle = movietitle;
        this.date = date;
        this.time = time;
        this.seats = seats;
    }

    public Long getId() {
        return id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public String getmovietitle() {
        return movietitle;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getSeats() {
        return seats;
    }

    // called by the service layer to decrement amounr of seats
    void decrementSeats(int amount) {
        this.seats -= amount;
    }
}