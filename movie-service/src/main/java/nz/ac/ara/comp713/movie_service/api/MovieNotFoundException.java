package nz.ac.ara.comp713.movie_service.api;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String title) {
        super("Movie " + title + " was not found");
    }
}