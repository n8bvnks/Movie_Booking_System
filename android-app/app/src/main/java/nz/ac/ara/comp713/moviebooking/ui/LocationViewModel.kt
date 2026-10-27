package nz.ac.ara.comp713.moviebooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nz.ac.ara.comp713.moviebooking.location.Origin

// Holds where the customer is travelling from, however we found out (GPS or picked by hand).
class LocationViewModel : ViewModel() {

    var origin by mutableStateOf<Origin?>(null)
        private set
    var fetching by mutableStateOf(false)
        private set

    // why the device location wasn't used, shown with the manual picker
    var notice by mutableStateOf<String?>(null)
        private set
    var showPicker by mutableStateOf(false)

    fun fetchStarted() {
        fetching = true
        notice = null
    }

    fun useDevice(lat: Double, lng: Double) {
        origin = Origin("your location", lat, lng, fromDevice = true)
        fetching = false
        showPicker = false
    }

    // permission denied / location off / no fix: offer the manual fallback
    fun deviceUnavailable(reason: String) {
        fetching = false
        notice = reason
        showPicker = true
    }

    fun useManual(area: Origin) {
        origin = area
        showPicker = false
    }
}
