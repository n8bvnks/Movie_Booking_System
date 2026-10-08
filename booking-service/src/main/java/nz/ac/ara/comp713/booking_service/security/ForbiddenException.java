package nz.ac.ara.comp713.booking_service.security;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}