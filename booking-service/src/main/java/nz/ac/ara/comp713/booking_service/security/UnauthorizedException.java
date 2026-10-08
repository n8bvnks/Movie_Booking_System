package nz.ac.ara.comp713.booking_service.security;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) { super(message); }
}