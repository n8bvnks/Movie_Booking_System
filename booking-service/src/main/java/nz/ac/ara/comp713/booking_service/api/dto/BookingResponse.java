package nz.ac.ara.comp713.booking_service.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.sql.Date;

public record BookingResponse(
    @NotBlank 
    String BookingId, //generated, auto increment from last

    String name,
    String MovieTitle,
    String timeslot,
    Date date

) {
}