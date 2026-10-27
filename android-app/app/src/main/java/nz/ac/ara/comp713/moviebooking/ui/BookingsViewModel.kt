package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.ara.comp713.moviebooking.data.ApiClient
import nz.ac.ara.comp713.moviebooking.data.Booking
import nz.ac.ara.comp713.moviebooking.data.apiCall

class BookingsViewModel : ViewModel() {

    var bookings by mutableStateOf<List<Booking>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var message by mutableStateOf<String?>(null)

    // booking-service only ever returns the logged-in customer's own bookings
    fun refresh() {
        if (loading) return
        loading = true
        error = null
        viewModelScope.launch {
            apiCall { ApiClient.booking.myBookings() }
                .onSuccess { bookings = it }
                .onFailure { error = it.message }
            loading = false
        }
    }

    fun cancel(booking: Booking) {
        viewModelScope.launch {
            apiCall { ApiClient.booking.cancel(booking.bookingId) }
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        message = "Booking #${booking.bookingId} cancelled"
                        refresh()
                    } else {
                        message = "Could not cancel the booking (${response.code()})"
                    }
                }
                .onFailure { message = it.message }
        }
    }
}
