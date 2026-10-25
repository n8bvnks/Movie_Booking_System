package nz.ac.ara.comp713.notification_service.api;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.ara.comp713.notification_service.api.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// same shape as booking-service's and movie-service's GlobalErrorHandler, kept consistent
// across all three services even though each is implemented independently
@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {

        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(
            MissingServletRequestParameterException exception, HttpServletRequest request) {

        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST",
                "Missing required parameter: " + exception.getParameterName(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
        exception.printStackTrace();
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "The request could not be completed", request);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String code, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiError(code, message, request.getRequestURI()));
    }
}
