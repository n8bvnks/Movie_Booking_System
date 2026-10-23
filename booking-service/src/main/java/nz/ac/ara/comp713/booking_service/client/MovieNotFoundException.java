package nz.ac.ara.comp713.booking_service.client;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String movieTitle, String date, String time) {
        super("No showing found for " + movieTitle + " on " + date + " at " + time);
    }

    // NEW - used when an admin deletes a whole movie and no showing has that title
    public MovieNotFoundException(String movieTitle) {
        super("No showings found for " + movieTitle);
    }
}
