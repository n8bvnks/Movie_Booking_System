package nz.ac.ara.comp713.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

// a small, independently deployable service: owns one table (notifications), has no REST
// dependency on booking-service or movie-service, and learns about new bookings only by
// consuming BookingConfirmed events from the broker. Run separately from booking-service with
// ./mvnw spring-boot:run, same as the other two services.
@SpringBootApplication
@EnableJms
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
