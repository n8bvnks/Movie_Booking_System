package nz.ac.ara.comp713.notification_service.repository;

import nz.ac.ara.comp713.notification_service.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // used by the listener to tell a redelivered event apart from a genuinely new one
    boolean existsByBookingId(Long bookingId);

    // used by GET /api/v1/notifications/mine - newest first, same ordering style as
    // BookingRepository.findByUserIdOrderByDateAsc in booking-service
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // the Android app only has the logged-in username (not a userId - see NotificationController
    // for why), so this is the lookup it actually uses
    List<Notification> findByCustomerNameOrderByCreatedAtDesc(String customerName);
}
