package nz.ac.ara.comp713.booking_service.messaging;

import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

// publishes one BookingConfirmed event to the broker after a booking is saved.
// same pattern LiveUpdates already uses for the websocket: this runs AFTER the booking has
// been committed, and a publish failure must never make a successfully saved booking look
// like it failed to the customer - so errors here are only logged, never thrown.
@Component
public class BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BookingEventPublisher.class);

    public static final String QUEUE = "booking.events";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public BookingEventPublisher(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }

    // called by BookingController right after a booking is created
    // userId comes from the logged in user's token (the controller already has it), it is not
    // part of BookingResponse because the web client never needed it
    public void bookingConfirmed(BookingResponse booking, Long userId) {
        try {
            BookingConfirmedEvent event = new BookingConfirmedEvent(
                    UUID.randomUUID().toString(),
                    booking.bookingId(),
                    userId,
                    booking.name(),
                    booking.movieTitle(),
                    booking.timeslot(),
                    booking.date().toString(),
                    booking.seats(),
                    Instant.now().toString()
            );

            String json = objectMapper.writeValueAsString(event);
            jmsTemplate.convertAndSend(QUEUE, json);

            log.info("Published BookingConfirmed event {} for booking {}", event.eventId(), booking.bookingId());
        } catch (Exception e) {
            log.warn("Could not publish BookingConfirmed event for booking {}: {}", booking.bookingId(), e.getMessage());
        }
    }
}
