package nz.ac.ara.comp713.booking_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ATTRIBUTE = "authUser";

    private final TokenStore tokenStore;

    public AuthInterceptor(TokenStore tokenStore) { this.tokenStore = tokenStore; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Login required");
        }

        AuthUser user = tokenStore.find(header.substring(7).trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired token, please log in again"));

        if (request.getRequestURI().startsWith("/api/v1/admin/") && !user.isAdmin()) {
            throw new ForbiddenException("Admin access required");
        }

        request.setAttribute(USER_ATTRIBUTE, user);
        return true;
    }
}