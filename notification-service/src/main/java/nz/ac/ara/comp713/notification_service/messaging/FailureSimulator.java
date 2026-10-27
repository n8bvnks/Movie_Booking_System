package nz.ac.ara.comp713.notification_service.messaging;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

// TEST-ONLY helper used to demonstrate the broker's retry/redelivery behaviour on demand.
// Armed through POST /api/v1/notifications/_test/simulate-failures (see NotificationController),
// then the NEXT message(s) the listener receives will deliberately fail processing, so the
// redelivery (and, if armed for more than maximumRedeliveries, the dead-letter) behaviour
// configured in BrokerClientConfig can be shown on camera instead of only described.
// This has no effect on real traffic when it is not armed (the default, count = 0).
@Component
public class FailureSimulator {

    private final AtomicInteger failuresRemaining = new AtomicInteger(0);

    public void arm(int count) {
        failuresRemaining.set(Math.max(count, 0));
    }

    // called by the listener before it does any real work - returns true (and consumes one
    // "charge") if this delivery should be made to fail on purpose
    public boolean shouldFailThisDelivery() {
        return failuresRemaining.getAndUpdate(current -> current > 0 ? current - 1 : 0) > 0;
    }

    public int remaining() {
        return failuresRemaining.get();
    }
}
