package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nz.ac.ara.comp713.moviebooking.data.ApiClient
import nz.ac.ara.comp713.moviebooking.data.LoginRequest
import nz.ac.ara.comp713.moviebooking.data.Session
import nz.ac.ara.comp713.moviebooking.data.apiCall

class LoginViewModel : ViewModel() {

    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun login(username: String, password: String) {
        if (loading) return
        loading = true
        error = null
        viewModelScope.launch {
            apiCall { ApiClient.booking.login(LoginRequest(username.trim(), password)) }
                .onSuccess { response ->
                    // this app is for customers only; the session is never started for an admin
                    if (response.role == "CUSTOMER") {
                        Session.start(response.token, response.username)
                    } else {
                        error = "This app is for customers only. Admins please use the web portal."
                    }
                }
                .onFailure { error = it.message }
            loading = false
        }
    }
}
