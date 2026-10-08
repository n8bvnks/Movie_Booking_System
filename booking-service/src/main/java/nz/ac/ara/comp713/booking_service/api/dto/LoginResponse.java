package nz.ac.ara.comp713.booking_service.api.dto;
import nz.ac.ara.comp713.booking_service.domain.Role;

public record LoginResponse(String token, String username, Role role) { }