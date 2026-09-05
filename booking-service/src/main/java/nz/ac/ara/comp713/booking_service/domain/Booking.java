package nz.ac.ara.comp713.booking_service.domain;

import jakarta.persistence.Column;
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

    @Column (name = "customer_name", nullable = false, length = 40)
    private String name;

    @Column (name = "movie_title", nullable = false, length = 40)
    private String movieTitle;

    @Column (name = "movie_time", nullable = false, length = 40)
    private String timeslot;

    @Column (name = "date_booked", nullable = false, length = 40)
    private LocalDate date;

    @Column (name = "seats_booked", nullable = false, length = 40)
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