package nz.ac.ara.comp713.booking_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(
    name = "bookings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "movie_title", "timeslot", "date"})
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private String name;

    private String movieTitle;

    private String timeslot;

    private LocalDate date;

    private int seats;

    protected Booking() {
        // required by JPA
    }

    public Booking(String name, String movieTitle, String timeslot, LocalDate date, int seats) {
        this.name = name;
        this.movieTitle = movieTitle;
        this.timeslot = timeslot;
        this.date = date;
        this.seats = seats;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getName() {
        return name;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getSeats() {
        return seats;
    }
}