package nz.ac.ara.comp713.booking_service.security;
import nz.ac.ara.comp713.booking_service.domain.Role;

public record AuthUser(Long id, String username, Role role) {
    public boolean isAdmin() { return role == Role.ADMIN; }
}