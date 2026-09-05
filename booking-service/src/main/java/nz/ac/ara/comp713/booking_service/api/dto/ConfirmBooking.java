package nz.ac.ara.comp713.booking_service.api.dto;

import java.time.LocalDate;

// this is the response returned by GET /api/v1/bookings/{id} 
// the id itself comes from the URL, not from a request body, so this record doesn't need to hold it
public record ConfirmBooking(
        String message,
        String name,
        String movieTitle,
        String timeslot,
        LocalDate date,
        int seats
) {
}

