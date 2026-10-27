package nz.ac.ara.comp713.moviebooking.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// booking-service (port 8082). Only the customer endpoints - this app has no admin features.
interface BookingApi {
    @POST("api/v1/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("api/v1/movies")
    suspend fun showings(): List<Showing>

    @POST("api/v1/bookings")
    suspend fun book(@Body body: BookingRequest): Booking

    @GET("api/v1/bookings/mine")
    suspend fun myBookings(): List<Booking>

    @POST("api/v1/bookings/{id}/delete")
    suspend fun cancel(@Path("id") id: Long): Response<Unit>
}

// notification-service (port 8085)
interface NotificationApi {
    @GET("api/v1/notifications/mine")
    suspend fun mine(@Query("username") username: String): List<AppNotification>
}
