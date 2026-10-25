package nz.ac.ara.comp713.notification_service.api.dto;

import nz.ac.ara.comp713.notification_service.domain.Notification;

import java.time.Instant;
import java.time.LocalDate;

public record NotificationResponse(
        Long id,
        Long bookingId,
        String customerName,
        String movieTitle,
        String timeslot,
        LocalDate date,
        int seats,
        String message,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getBookingId(), n.getCustomerName(), n.getMovieTitle(),
                n.getTimeslot(), n.getDate(), n.getSeats(), n.getMessage(), n.getCreatedAt());
    }
}
