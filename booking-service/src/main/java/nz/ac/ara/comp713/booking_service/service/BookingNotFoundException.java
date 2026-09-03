package nz.ac.ara.comp713.booking_service.service;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(long bookingId) {
        super("Booking " + bookingId + " was not found");
    }
}