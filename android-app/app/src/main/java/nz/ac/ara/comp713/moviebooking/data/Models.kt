package nz.ac.ara.comp713.moviebooking.data

// These mirror the JSON records the backend sends and receives. Dates stay as plain
// "yyyy-MM-dd" strings, which is all the UI needs.

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val token: String, val username: String, val role: String)

// booking-service calls this MovieResponse; to a customer it is one showing of a movie
data class Showing(
    val movieTitle: String,
    val genre: String,
    val description: String,
    val runtime: Int,
    val date: String,
    val time: String,
    val seats: Int
)

data class BookingRequest(
    val movieTitle: String,
    val timeslot: String,
    val date: String,
    val seats: Int
)

data class Booking(
    val message: String?,
    val bookingId: Long,
    val name: String,
    val movieTitle: String,
    val timeslot: String,
    val date: String,
    val seats: Int
)

// one row produced by notification-service after it consumed a BookingConfirmed event
data class AppNotification(
    val id: Long,
    val bookingId: Long,
    val customerName: String,
    val movieTitle: String,
    val timeslot: String,
    val date: String,
    val seats: Int,
    val message: String,
    val createdAt: String?
)

// the error body every backend service returns
data class ApiError(val code: String?, val message: String?, val path: String?)
