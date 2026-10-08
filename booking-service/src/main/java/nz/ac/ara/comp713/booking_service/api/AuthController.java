package nz.ac.ara.comp713.booking_service.api;

import jakarta.validation.Valid;
import nz.ac.ara.comp713.booking_service.api.dto.LoginRequest;
import nz.ac.ara.comp713.booking_service.api.dto.LoginResponse;
import nz.ac.ara.comp713.booking_service.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}