package nz.ac.ara.comp713.booking_service.security;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    private final Map<String, AuthUser> tokens = new ConcurrentHashMap<>();

    public String issue(AuthUser user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user);
        return token;
    }

    public Optional<AuthUser> find(String token) {
        return Optional.ofNullable(tokens.get(token));
    }
}