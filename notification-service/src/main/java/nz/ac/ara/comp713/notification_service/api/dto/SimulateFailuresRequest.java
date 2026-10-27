package nz.ac.ara.comp713.notification_service.api.dto;

import jakarta.validation.constraints.Min;

// TEST-ONLY request body for POST /api/v1/notifications/_test/simulate-failures
public record SimulateFailuresRequest(@Min(0) int count) {
}
