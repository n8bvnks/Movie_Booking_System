package nz.ac.ara.comp713.notification_service.api;

import jakarta.validation.Valid;
import nz.ac.ara.comp713.notification_service.api.dto.NotificationResponse;
import nz.ac.ara.comp713.notification_service.api.dto.SimulateFailuresRequest;
import nz.ac.ara.comp713.notification_service.messaging.FailureSimulator;
import nz.ac.ara.comp713.notification_service.repository.NotificationRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final NotificationRepository repository;
    private final FailureSimulator failureSimulator;

    public NotificationController(NotificationRepository repository, FailureSimulator failureSimulator) {
        this.repository = repository;
        this.failureSimulator = failureSimulator;
    }

    // GET /api/v1/notifications/mine?username=alice - the Android app's Notifications screen.
    //
    // NOTE on identity: this service has no access to booking-service's in-memory TokenStore
    // (each service's auth/session state is its own, by design), so it cannot independently
    // verify who is asking - it trusts the username the already-logged-in Android app sends,
    // the same username booking-service already put on the JWT-free bearer token at login.
    // This is a known, documented simplification for this coursework system (see the report's
    // Limitations section), not a production auth model.
    @GetMapping("/mine")
    public List<NotificationResponse> mine(@RequestParam String username) {
        return repository.findByCustomerNameOrderByCreatedAtDesc(username.trim())
                .stream().map(NotificationResponse::from).toList();
    }

    // POST /api/v1/notifications/_test/simulate-failures {"count": 2}
    // TEST-ONLY - arms the next N deliveries to fail on purpose, so the broker's redelivery
    // behaviour (configured in BrokerClientConfig) can be demonstrated on demand instead of
    // waiting for a real failure. Has no effect on real traffic when count is 0 (the default).
    @PostMapping("/_test/simulate-failures")
    public Map<String, Integer> simulateFailures(@Valid @RequestBody SimulateFailuresRequest request) {
        failureSimulator.arm(request.count());
        return Map.of("armed", failureSimulator.remaining());
    }
}
