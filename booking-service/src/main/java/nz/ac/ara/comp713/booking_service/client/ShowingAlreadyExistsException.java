package nz.ac.ara.comp713.booking_service.client;

// thrown when movie-service answers 409 to a create showing request
// same title + date + time already exists
public class ShowingAlreadyExistsException extends RuntimeException {

    public ShowingAlreadyExistsException(String movieTitle, String date, String time) {
        super("A showing of " + movieTitle + " already exists on " + date + " at " + time);
    }
}