package nz.ac.ara.comp713.booking_service.client;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String movieTitle, String date, String time) {
        super("No showing found for " + movieTitle + " on " + date + " at " + time);
    }
}