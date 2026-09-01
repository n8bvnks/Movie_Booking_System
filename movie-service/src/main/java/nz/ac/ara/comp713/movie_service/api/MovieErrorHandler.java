package nz.ac.ara.comp713.movie_service.api;
 
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
 
@RestControllerAdvice
public class MovieErrorHandler {
 
    // catches MovieNotFoundException, turns it into a 404 + ApiError JSON body
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            MovieNotFoundException exception,
            HttpServletRequest request) {
 
        ApiError error = new ApiError(
                "MOVIE_NOT_FOUND",
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}