package nz.ac.ara.comp713.booking_service.api.dto;

// sent back to the admin page after a showing or a whole movie is deleted
// cancelledBookings is how many customer bookings were cancelled because their showing no longer exists
public record DeleteShowingResult(String message, int cancelledBookings) {
}
