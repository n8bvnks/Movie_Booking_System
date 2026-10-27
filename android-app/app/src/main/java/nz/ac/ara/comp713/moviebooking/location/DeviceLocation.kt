package nz.ac.ara.comp713.moviebooking.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

// One location fix, or null when none can be had (location switched off, no signal, no Google
// Play services...). Callers treat null as "use the manual fallback".
@SuppressLint("MissingPermission") // callers check hasLocationPermission() first
suspend fun getDeviceLocation(context: Context): Location? {
    val client = LocationServices.getFusedLocationProviderClient(context)
    return try {
        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token).await()
            ?: client.lastLocation.await()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
}
