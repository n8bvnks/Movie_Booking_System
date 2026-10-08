package nz.ac.ara.comp713.booking_service.api.dto;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String username, @NotBlank String password) { }
