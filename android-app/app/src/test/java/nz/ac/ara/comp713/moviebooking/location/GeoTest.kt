package nz.ac.ara.comp713.moviebooking.location

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeoTest {

    @Test
    fun distanceToSamePointIsZero() {
        assertEquals(0.0, distanceKm(-36.85, 174.76, -36.85, 174.76), 0.0001)
    }

    @Test
    fun distanceIsSymmetric() {
        val ab = distanceKm(-36.8485, 174.7633, -41.2866, 174.7756)
        val ba = distanceKm(-41.2866, 174.7756, -36.8485, 174.7633)
        assertEquals(ab, ba, 0.0001)
    }

    @Test
    fun aucklandToWellingtonIsAbout494Km() {
        assertEquals(494.0, distanceKm(-36.8485, 174.7633, -41.2866, 174.7756), 5.0)
    }

    @Test
    fun everyManualAreaIsReasonablyCloseToTheCinema() {
        // they are all Auckland suburbs, so none should be further than ~50 km
        serviceAreas.forEach { assertTrue("${it.name} too far", it.distanceToCinemaKm() < 50.0) }
    }

    @Test
    fun driveMinutesRoundsUpAndNeverBelowOne() {
        assertEquals(1, driveMinutes(0.0))
        assertEquals(20, driveMinutes(10.0))   // 10 km at 30 km/h = 20 min
        assertEquals(21, driveMinutes(10.2))   // 20.4 min rounds up
    }

    @Test
    fun farAwayTripsAreNotDrivable() {
        assertTrue(isDrivable(25.0))
        assertFalse(isDrivable(9000.0))
    }

    @Test
    fun formatDistanceUsesMetresForShortTrips() {
        assertEquals("450 m", formatDistance(0.45))
        assertEquals("12.3 km", formatDistance(12.34))
        assertEquals("494 km", formatDistance(494.2))
    }
}
