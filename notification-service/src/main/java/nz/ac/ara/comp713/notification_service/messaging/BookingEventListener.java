package nz.ac.ara.comp713.notification_service.messaging;

import nz.ac.ara.comp713.notification_service.domain.Notification;
import nz.ac.ara.comp713.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

// consumes "booking.events" from the broker (booking-service's embedded ActiveMQ) and keeps a
// persistent Notification row per booking. This method throwing is what triggers ActiveMQ's
// redelivery: the default JMS listener container uses auto-acknowledge on a clean return, so a
// thrown exception means the message is NOT acknowledged and is redelivered according to the
// RedeliveryPolicy configured in BrokerClientConfig, up to its dead-letter queue.
@Component
public class BookingEventListener {

    private static final Logger log = LoggerFactory.getLogger(BookingEventListener.class);
    public static final String QUEUE = "booking.events";

    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;
    private final FailureSimulator failureSimulator;

    public BookingEventListener(NotificationRepository repository, ObjectMapper objectMapper,
                                 FailureSimulator failureSimulator) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.failureSimulator = failureSimulator;
    }

    @JmsListener(destination = QUEUE)
    public void onBookingConfirmed(String json) throws Exception {
        BookingConfirmedEvent event = objectMapper.readValue(json, BookingConfirmedEvent.class);

        // TEST-ONLY: lets the demo video show a message actually failing and being redelivered,
        // rather than only describing it. Armed via POST /_test/simulate-failures; see FailureSimulator.
        if (failureSimulator.shouldFailThisDelivery()) {
            log.warn("Simulated processing failure for booking {} (event {}) - this delivery will be redelivered",
                    event.bookingId(), event.eventId());
            throw new RuntimeException("Simulated failure for booking " + event.bookingId());
        }

        // idempotency check: ActiveMQ only promises AT-LEAST-ONCE delivery, so the same event
        // can legitimately arrive twice (most commonly right after a simulated/real failure
        // above triggers a redelivery). Without this check a redelivered event would create a
        // second notification for the same booking.
        if (repository.existsByBookingId(event.bookingId())) {
            log.info("Booking {} (event {}) already processed, ignoring duplicate delivery",
                    event.bookingId(), event.eventId());
            return;
        }

        String message = "Booking confirmed: " + event.movieTitle() + " at " + event.timeslot()
                + " on " + event.date() + " for " + event.seats()
                + (event.seats() == 1 ? " seat" : " seats") + ".";

        Notification notification = new Notification(
                event.bookingId(),
                event.userId(),
                event.customerName(),
                event.movieTitle(),
                event.timeslot(),
                LocalDate.parse(event.date()),
                event.seats(),
                message
        );

        try {
            repository.save(notification);
            log.info("Recorded notification for booking {} (event {}), customer {}",
                    event.bookingId(), event.eventId(), event.customerName());
        } catch (DataIntegrityViolationException e) {
            // the unique constraint on booking_id is the real safety net: if two deliveries of
            // the same event were ever handled concurrently and both passed the exists() check
            // above, only one insert can win here - the loser lands in this catch and is
            // treated the same as any other duplicate, not as a failure.
            log.info("Booking {} (event {}) was already recorded by a concurrent delivery, ignoring",
                    event.bookingId(), event.eventId());
        }
    }
}
