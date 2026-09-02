package nz.ac.ara.comp713.movie_service.api;

public class MovieNotFoundException extends RuntimeException {

    // used when the specific movie + date + time combination doesn't exist
    public MovieNotFoundException(String movieTitle, String date, String time) {
        super("No showing found for " + movieTitle + " on " + date + " at " + time);
    }
}