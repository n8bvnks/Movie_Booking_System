package nz.ac.ara.comp713.notification_service.messaging;

// must stay the same shape as booking-service's BookingConfirmedEvent - the two services do not
// share code, only this message contract (the same pattern the project already uses for
// MovieResponse between movie-service and booking-service)
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
