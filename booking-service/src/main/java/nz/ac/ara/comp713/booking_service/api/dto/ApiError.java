package nz.ac.ara.comp713.booking_service.api.dto;

//format of an error used in global error handler
public record ApiError(
        String code,
        String message,
        String path
) {
}

