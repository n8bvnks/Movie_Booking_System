package nz.ac.ara.comp713.movie_service.api;

//if trying to add a showing throw a duplicate if it already exists
public class ShowingAlreadyExistsException extends RuntimeException {

    // used when the same movie + date + time is already in the database
    public ShowingAlreadyExistsException(String movieTitle, String date, String time) {
        super("A showing already exists for " + movieTitle + " on " + date + " at " + time);
    }
}