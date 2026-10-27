package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.ara.comp713.moviebooking.data.ApiClient
import nz.ac.ara.comp713.moviebooking.data.AppNotification
import nz.ac.ara.comp713.moviebooking.data.Session
import nz.ac.ara.comp713.moviebooking.data.apiCall

// Notifications are created by notification-service when it consumes a BookingConfirmed
// event from the message broker - this app only reads them.
class NotificationsViewModel : ViewModel() {

    var notifications by mutableStateOf<List<AppNotification>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun refresh() {
        val username = Session.username ?: return
        if (loading) return
        loading = true
        error = null
        viewModelScope.launch {
            apiCall { ApiClient.notifications.mine(username) }
                .onSuccess { notifications = it }
                .onFailure { error = it.message }
            loading = false
        }
    }
}
