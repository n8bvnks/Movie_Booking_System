package nz.ac.ara.comp713.moviebooking

// The one place that says where the backend lives.
//   Android emulator -> 10.0.2.2 is the emulator's alias for the Mac running the services.
//   Physical phone   -> use the Mac's Wi-Fi IP instead (e.g. "192.168.1.23"); the phone and
//                       the Mac must be on the same network.
object Config {
    const val HOST = "10.0.2.2"

    const val BOOKING_BASE_URL = "http://$HOST:8082/"       // booking-service
    const val NOTIFICATION_BASE_URL = "http://$HOST:8085/"  // notification-service (message broker consumer)
}
