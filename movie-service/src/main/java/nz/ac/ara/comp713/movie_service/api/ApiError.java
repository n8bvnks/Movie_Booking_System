package nz.ac.ara.comp713.movie_service.api;

// shape for every error response

public record ApiError(
        String code,
        String message,
        String path
) {
}