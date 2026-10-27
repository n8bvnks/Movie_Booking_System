package nz.ac.ara.comp713.moviebooking.location

import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// The cinema every showing is held at (demo location in the Auckland CBD).
object Cinema {
    const val NAME = "the cinema"
    const val LAT = -36.8509
    const val LNG = 174.7645
}

// Where the customer is travelling from: either a real GPS fix or an area picked by hand.
data class Origin(val name: String, val lat: Double, val lng: Double, val fromDevice: Boolean)

// Manual fallback, used when location permission is denied or the device can't give a fix.
val serviceAreas: List<Origin> = listOf(
    Origin("Auckland CBD", -36.8485, 174.7633, false),
    Origin("Ponsonby", -36.8560, 174.7380, false),
    Origin("Mt Eden", -36.8780, 174.7640, false),
    Origin("Newmarket", -36.8700, 174.7780, false),
    Origin("Takapuna", -36.7870, 174.7740, false),
    Origin("Albany", -36.7270, 174.7050, false),
    Origin("Henderson", -36.8800, 174.6240, false),
    Origin("Manukau", -36.9930, 174.8790, false),
    Origin("Howick", -36.8920, 174.9320, false),
    Origin("Papakura", -37.0650, 174.9440, false)
)

private const val EARTH_RADIUS_KM = 6371.0088

// Great-circle distance between two points (haversine formula), in kilometres.
fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2) * sin(dLng / 2)
    return EARTH_RADIUS_KM * 2 * atan2(sqrt(a), sqrt(1 - a))
}

fun Origin.distanceToCinemaKm(): Double = distanceKm(lat, lng, Cinema.LAT, Cinema.LNG)

// Rough city driving time at an average 30 km/h. Only meaningful for a plausible trip, so
// callers should check isDrivable() before showing it.
fun driveMinutes(km: Double): Int = max(1, ceil(km * 2.0).toInt()) // 30 km/h = 2 minutes per km

fun isDrivable(km: Double): Boolean = km <= 200.0

fun formatDistance(km: Double): String = when {
    km < 1.0 -> "${(km * 1000).roundToInt()} m"
    km < 100.0 -> "%.1f km".format(km)
    else -> "%.0f km".format(km)
}
