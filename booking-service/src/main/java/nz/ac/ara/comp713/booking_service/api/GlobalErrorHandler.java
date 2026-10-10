package nz.ac.ara.comp713.booking_service.api;
import nz.ac.ara.comp713.booking_service.security.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import nz.ac.ara.comp713.booking_service.api.dto.ApiError;
import nz.ac.ara.comp713.booking_service.client.MovieNotFoundException;
import nz.ac.ara.comp713.booking_service.client.MovieServiceUnavailableException;
import nz.ac.ara.comp713.booking_service.client.NotEnoughSeatsException;
import nz.ac.ara.comp713.booking_service.client.ShowingAlreadyExistsException;
import nz.ac.ara.comp713.booking_service.service.AlreadyBookedException;
import nz.ac.ara.comp713.booking_service.service.BookingNotFoundException;
import nz.ac.ara.comp713.booking_service.service.InvalidUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import nz.ac.ara.comp713.booking_service.security.ForbiddenException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN", exception.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException exception, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", exception.getMessage(), request);
    }

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

    // NEW - admin tried to add a showing that is already listed (movie-service answered 409)
    @ExceptionHandler(ShowingAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleShowingExists(
            ShowingAlreadyExistsException exception,
            HttpServletRequest request) {

        return error(HttpStatus.CONFLICT, "SHOWING_ALREADY_EXISTS", exception.getMessage(), request);
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



    // NEW - a required query parameter is missing, e.g. no date on the admin showing bookings request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
                "Missing required parameter: " + exception.getParameterName(), request);
    }

    // NEW - a query parameter is the wrong format, e.g. date=15-10-2026 instead of 2026-10-15
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleBadParameter(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
                "Parameter '" + exception.getName() + "' has an invalid value", request);
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