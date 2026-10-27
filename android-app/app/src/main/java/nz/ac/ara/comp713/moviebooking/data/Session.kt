package nz.ac.ara.comp713.moviebooking.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Who is logged in. Held in memory only: booking-service also keeps its tokens in memory,
// so a restart of the server invalidates them anyway. Being Compose state, the UI switches
// between the login screen and the app automatically when this changes.
object Session {
    var token by mutableStateOf<String?>(null)
        private set
    var username by mutableStateOf<String?>(null)
        private set

    fun start(token: String, username: String) {
        this.token = token
        this.username = username
    }

    fun clear() {
        token = null
        username = null
    }
}
