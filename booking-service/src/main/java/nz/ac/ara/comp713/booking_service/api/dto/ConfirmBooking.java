package nz.ac.ara.comp713.booking_service.api.dto;

import jakarta.validation.constraints.NotBlank;

//confirm booking that takes a customers booking ID to confirm

public record ConfirmBooking(
    @NotBlank
    String BookingId
)
{}

