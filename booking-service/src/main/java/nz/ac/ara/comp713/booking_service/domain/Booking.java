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
    //a user can only have one booking for the same movie, time and date, it used to check the name instead
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_title", "timeslot", "date"})
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    //the id of the user who made the booking, set from the login, used to check who owns the booking
    @Column(nullable = false)
    private Long userId;

    private String name;

    private String movieTitle;

    private String timeslot;

    private LocalDate date;

    private int seats;

    protected Booking() {
        // required by JPA
    }

    //the user id comes first, it is the logged in user's id from the token
    public Booking(Long userId, String name, String movieTitle, String timeslot, LocalDate date, int seats) {
        this.userId = userId;
        this.name = name;
        this.movieTitle = movieTitle;
        this.timeslot = timeslot;
        this.date = date;
        this.seats = seats;
    }

    public Long getBookingId() {
        return bookingId;
    }

    //used by the owner check in the booking service
    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setSeats(int seats) {
        this.seats = seats;
    }
}