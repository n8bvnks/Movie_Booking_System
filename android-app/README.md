# Movie Booking - Android app (customers)

Native Android client for the Movie Booking System, built with **Kotlin + Jetpack Compose**.
It is customer-only: admins are rejected at login and keep using the web portal.

| Screen | What it does | Backend |
|---|---|---|
| Login | Customer sign-in (bearer token) | booking-service `POST /api/v1/login` |
| Showings | Browse showings, see seats left, book 1-10 seats | `GET /api/v1/movies`, `POST /api/v1/bookings` |
| My bookings | Only the logged-in customer's bookings; cancel one | `GET /api/v1/bookings/mine`, `POST /api/v1/bookings/{id}/delete` |
| Notifications | Booking confirmations created by the message-broker consumer | notification-service `GET /api/v1/notifications/mine` |

## Mobile-specific feature: device location with manual fallback

The Showings screen shows how far the customer is from the cinema (haversine distance) and the
booking dialog says how long to allow for the trip.

1. Tapping **Use my location** asks for the location permission (fine or coarse) and reads one fix
   through Google's fused location provider.
2. If the permission is denied, location is switched off, or no fix comes back, the app does **not**
   dead-end: it explains why and offers **Choose area** - a list of Auckland suburbs to travel from.
3. The customer can switch between the two at any time (**Update** / **Choose area**).

The maths lives in `location/Geo.kt` and is covered by unit tests (`GeoTest`).

## Setup on a Mac (first time)

1. **Install Android Studio** from https://developer.android.com/studio (Apple Silicon or Intel
   download as appropriate), drag it into Applications and open it. Accept the default setup wizard -
   it installs the Android SDK and an emulator. It bundles its own JDK, so you do not need to install Java for the app.
2. **Open the project**: *File > Open* and choose the `android-app` folder (not the repo root).
   Let Gradle sync finish (first sync downloads a lot - a few minutes). If Studio offers to
   upgrade the Android Gradle Plugin, you can decline.
3. **Create an emulator**: *Tools > Device Manager > Create Device*, pick a Pixel, and choose a system
   image that says **Google APIs** (or Google Play) - the location feature needs Google Play services.
4. **Start the backend** (three terminals, from the repo root):
   ```bash
   cd movie-service && export DB_PASSWORD="..." && ./mvnw spring-boot:run
   cd booking-service && export DB_PASSWORD="..." && ./mvnw spring-boot:run
   cd notification-service && export DB_PASSWORD="..." && ./mvnw spring-boot:run
   ```
5. **Run the app**: pick the emulator in the toolbar and press the green Run button.
   Log in with a customer account, e.g. `alice` / `alice123`.

### Using a real phone instead

Set `HOST` in `app/src/main/java/nz/ac/ara/comp713/moviebooking/Config.kt` to your Mac's Wi-Fi IP
(*System Settings > Wi-Fi > Details*), keep the phone on the same Wi-Fi, and enable
*Developer options > USB debugging* on the phone. The emulator default `10.0.2.2` means "the Mac".

### Trying the location feature in the emulator

The emulator's default GPS position is California, so the distance will show thousands of km.
Open the emulator's **... (Extended controls) > Location**, search for "Auckland" and click
**Set location** - then tap **Update** in the app. To demo the fallback, deny the permission
(or switch location off in the emulator's quick settings) and use **Choose area**.

## Tests

`./gradlew test` (or right-click `GeoTest` > Run) runs the distance / drive-time unit tests.
