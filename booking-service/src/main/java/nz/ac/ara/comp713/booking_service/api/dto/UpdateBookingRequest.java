package nz.ac.ara.comp713.booking_service.api.dto;

public record UpdateBookingRequest(
        String name,          // optional - null or blank means no change
        Integer seatsChange   // optional - positive to add seats, negative to remove, null/0 for no change
) {
}