package nz.ac.ara.comp713.booking_service.api.dto;

import java.time.LocalDate;

// returned once, right after POST /api/v1/bookings succeeds
// bookingId is what the customer needs to save to use confirmbooking
public record BookingResponse(
        Long bookingId,
        String name,
        String movieTitle,
        String timeslot,
        LocalDate date,
        int seats
) {
}