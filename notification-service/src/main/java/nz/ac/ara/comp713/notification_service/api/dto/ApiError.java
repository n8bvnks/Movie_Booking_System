package nz.ac.ara.comp713.notification_service.api.dto;

public record ApiError(String code, String message, String path) {
}
