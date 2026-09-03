package nz.ac.ara.comp713.booking_service.client;

public class MovieServiceUnavailableException extends RuntimeException {

    public MovieServiceUnavailableException() {
        super("Movie service is temporarily unavailable");
    }
}