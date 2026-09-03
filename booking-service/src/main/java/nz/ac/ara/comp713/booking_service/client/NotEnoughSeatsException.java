package nz.ac.ara.comp713.booking_service.client;

public class NotEnoughSeatsException extends RuntimeException {

    public NotEnoughSeatsException(String movieTitle) {
        super("Not enough seats remaining for " + movieTitle);
    }
}