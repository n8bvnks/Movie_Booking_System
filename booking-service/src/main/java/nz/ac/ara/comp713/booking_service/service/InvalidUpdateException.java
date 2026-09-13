package nz.ac.ara.comp713.booking_service.service;

public class InvalidUpdateException extends RuntimeException {

    public InvalidUpdateException(String message) {
        super(message);
    }
}