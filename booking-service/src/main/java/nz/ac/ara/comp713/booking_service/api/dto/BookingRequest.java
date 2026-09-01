package nz.ac.ara.comp713.booking_service.api.dto;

import java.sql.Date;

public record BookingRequest(
    String name,
    String timeslot,
    String MovieTitle,
    Date date

) {
}
