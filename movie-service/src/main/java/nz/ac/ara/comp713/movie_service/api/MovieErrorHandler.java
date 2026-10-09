package nz.ac.ara.comp713.movie_service.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

    //called when the same movie, date and time already exists, gives a 409
    @ExceptionHandler(ShowingAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleShowingExists(
            ShowingAlreadyExistsException exception,
            HttpServletRequest request) {

        return error(HttpStatus.CONFLICT, "SHOWING_ALREADY_EXISTS", exception.getMessage(), request);
    }

    //called when a new showing fails the checks in CreateShowingRequest, lists each field that was wrong, gives a 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, request);
    }

    //called when the body is not valid JSON, or a date is in the wrong format, gives a 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request body is not valid JSON", request);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status, String code, String message, HttpServletRequest request) {

        return ResponseEntity.status(status).body(new ApiError(
                code, message, request.getRequestURI()
        ));
    }
}