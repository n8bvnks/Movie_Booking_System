package nz.ac.ara.comp713.notification_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.time.LocalDate;

// one row per BookingConfirmed event this service has actually processed.
// bookingId has a UNIQUE constraint - this is what makes processing idempotent: ActiveMQ only
// promises at-least-once delivery, so the same event can arrive twice (for example after a
// processing failure triggers a redelivery). The listener checks existsByBookingId first, but
// the constraint is the real safety net if two deliveries are ever handled concurrently.
@Entity
@Table(name = "notifications", uniqueConstraints = @UniqueConstraint(columnNames = "booking_id"))
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String movieTitle;

    @Column(nullable = false)
    private String timeslot;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int seats;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    protected Notification() {
        // required by JPA
    }

    public Notification(Long bookingId, Long userId, String customerName, String movieTitle,
                         String timeslot, LocalDate date, int seats, String message) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.customerName = customerName;
        this.movieTitle = movieTitle;
        this.timeslot = timeslot;
        this.date = date;
        this.seats = seats;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public Long getUserId() { return userId; }
    public String getCustomerName() { return customerName; }
    public String getMovieTitle() { return movieTitle; }
    public String getTimeslot() { return timeslot; }
    public LocalDate getDate() { return date; }
    public int getSeats() { return seats; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
