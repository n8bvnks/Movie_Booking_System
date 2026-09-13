package nz.ac.ara.comp713.booking_service.api;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.ara.comp713.booking_service.api.dto.ApiError;
import nz.ac.ara.comp713.booking_service.client.MovieNotFoundException;
import nz.ac.ara.comp713.booking_service.client.MovieServiceUnavailableException;
import nz.ac.ara.comp713.booking_service.client.NotEnoughSeatsException;
import nz.ac.ara.comp713.booking_service.service.AlreadyBookedException;
import nz.ac.ara.comp713.booking_service.service.BookingNotFoundException;
import nz.ac.ara.comp713.booking_service.service.InvalidUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, request);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiError> handleBookingNotFound(
            BookingNotFoundException exception,
            HttpServletRequest request) {

        return error(HttpStatus.NOT_FOUND, "BOOKING_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(AlreadyBookedException.class)
    public ResponseEntity<ApiError> handleAlreadyBooked(
            AlreadyBookedException exception,
            HttpServletRequest request) {

        return error(HttpStatus.CONFLICT, "ALREADY_BOOKED", exception.getMessage(), request);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ApiError> handleMovieNotFound(
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

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<ApiError> handleInvalidUpdate(
            InvalidUpdateException exception,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage(), request);
    }

    @ExceptionHandler(MovieServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleMovieUnavailable(
            MovieServiceUnavailableException exception,
            HttpServletRequest request) {

        return error(HttpStatus.SERVICE_UNAVAILABLE, "MOVIE_SERVICE_UNAVAILABLE", exception.getMessage(), request);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(
            org.springframework.http.converter.HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request body is not valid JSON", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {

        exception.printStackTrace();

        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "The request could not be completed", request);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status, String code, String message, HttpServletRequest request) {

        return ResponseEntity.status(status).body(new ApiError(
                code, message, request.getRequestURI()
        ));
    }
}