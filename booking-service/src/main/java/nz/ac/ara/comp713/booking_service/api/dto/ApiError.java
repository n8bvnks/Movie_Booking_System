package nz.ac.ara.comp713.booking_service.api.dto;

public record ApiError(
        String code,
        String message,
        String path
) {
}

