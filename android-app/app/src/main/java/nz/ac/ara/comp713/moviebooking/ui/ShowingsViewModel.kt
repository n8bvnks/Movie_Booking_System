package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.ara.comp713.moviebooking.data.ApiClient
import nz.ac.ara.comp713.moviebooking.data.BookingRequest
import nz.ac.ara.comp713.moviebooking.data.Showing
import nz.ac.ara.comp713.moviebooking.data.apiCall

class ShowingsViewModel : ViewModel() {

    var showings by mutableStateOf<List<Showing>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // a one-off result ("Booked!" / "Not enough seats") that the screen shows in a snackbar
    // and then clears
    var message by mutableStateOf<String?>(null)

    init {
        refresh()
    }

    fun refresh() {
        if (loading) return
        loading = true
        error = null
        viewModelScope.launch {
            apiCall { ApiClient.booking.showings() }
                .onSuccess { showings = it }
                .onFailure { error = it.message }
            loading = false
        }
    }

    fun book(showing: Showing, seats: Int) {
        viewModelScope.launch {
            // the booking's timeslot is the showing's time, exactly as the web client sends it
            apiCall { ApiClient.booking.book(BookingRequest(showing.movieTitle, showing.time, showing.date, seats)) }
                .onSuccess { booking ->
                    message = "Booked ${booking.seats} seat(s) for ${booking.movieTitle}. Booking #${booking.bookingId}"
                    refresh() // seats left has changed
                }
                .onFailure { message = it.message }
        }
    }
}
