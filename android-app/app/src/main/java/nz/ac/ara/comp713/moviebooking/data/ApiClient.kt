package nz.ac.ara.comp713.moviebooking.data

import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import nz.ac.ara.comp713.moviebooking.Config
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    private val http: OkHttpClient = OkHttpClient.Builder()
        // attach the bearer token to every request once logged in
        .addInterceptor { chain ->
            val token = Session.token
            val request = if (token != null) {
                chain.request().newBuilder().header("Authorization", "Bearer $token").build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(http)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val booking: BookingApi by lazy { retrofit(Config.BOOKING_BASE_URL).create(BookingApi::class.java) }
    val notifications: NotificationApi by lazy { retrofit(Config.NOTIFICATION_BASE_URL).create(NotificationApi::class.java) }
}

// Runs a network call and turns every failure into a readable message, so view models never
// deal with exceptions directly. The backend's own error message (e.g. "Not enough seats")
// is passed through when there is one.
suspend fun <T> apiCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        if (e.code() == 401 && Session.token != null) Session.clear() // token expired: back to login
        val backendMessage = try {
            Gson().fromJson(e.response()?.errorBody()?.string(), ApiError::class.java)?.message
        } catch (_: Exception) {
            null
        }
        Result.failure(Exception(backendMessage ?: "Request failed (${e.code()})"))
    } catch (e: IOException) {
        Result.failure(Exception("Can't reach the server. Is it running, and is Config.HOST correct?"))
    } catch (e: Exception) {
        Result.failure(e)
    }
