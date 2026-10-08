package nz.ac.ara.comp713.booking_service.service;

import nz.ac.ara.comp713.booking_service.api.dto.LoginRequest;
import nz.ac.ara.comp713.booking_service.api.dto.LoginResponse;
import nz.ac.ara.comp713.booking_service.domain.User;
import nz.ac.ara.comp713.booking_service.repository.UserRepository;
import nz.ac.ara.comp713.booking_service.security.AuthUser;
import nz.ac.ara.comp713.booking_service.security.TokenStore;
import nz.ac.ara.comp713.booking_service.security.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final TokenStore tokenStore;

    public AuthService(UserRepository users, TokenStore tokenStore) {
        this.users = users;
        this.tokenStore = tokenStore;
    }

    public LoginResponse login(LoginRequest request) {
        // same message for unknown user and wrong password
        User user = users.findByUsername(request.username().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.getPassword().equals(request.password())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = tokenStore.issue(new AuthUser(user.getId(), user.getUsername(), user.getRole()));
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }
}