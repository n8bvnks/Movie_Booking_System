package nz.ac.ara.comp713.booking_service.messaging;

// the message published to the "booking.events" queue once a booking is saved.
// notification-service keeps its own copy of this same shape (same idea as MovieResponse
// being duplicated between movie-service and booking-service) because the two services must
// not share code or a database table, only the message contract between them.
// eventId lets a consumer recognise two DELIVERIES of the same event (a redelivery after a
// processing failure) and tell that apart from two different events for the same booking.
public record BookingConfirmedEvent(
        String eventId,
        Long bookingId,
        Long userId,
        String customerName,
        String movieTitle,
        String timeslot,
        String date,
        int seats,
        String confirmedAt
) {
}
