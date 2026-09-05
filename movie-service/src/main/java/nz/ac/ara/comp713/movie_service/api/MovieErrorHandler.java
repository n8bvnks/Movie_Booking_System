package nz.ac.ara.comp713.movie_service.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MovieErrorHandler {

    // automatically gets called if Exceptions are thrown

    
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            MovieNotFoundException exception,
            HttpServletRequest request) {

        return error(HttpStatus.NOT_FOUND, "MOVIE_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(NotEnoughSeatsException.class)
    public ResponseEntity<ApiError> handleNotEnoughSeats(
            NotEnoughSeatsException exception,
            HttpServletRequest request) {

        return error(HttpStatus.CONFLICT, "NOT_ENOUGH_SEATS", exception.getMessage(), request);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status, String code, String message, HttpServletRequest request) {

        return ResponseEntity.status(status).body(new ApiError(
                code, message, request.getRequestURI()
        ));
    }
}