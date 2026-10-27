# Movie Booking System - Web APP

A distributed movie booking application built with Spring Boot, split into three independently running services that communicate over HTTP.

# GITHUB

- **Repo** - https://github.com/n8bvnks/Movie_Booking_System

# Architecture

- **movie-service** (port 8083) — owns movie data in a single `MovieShowing` table: title, genre, description, runtime, date, time, and remaining seats. 

- **booking-service** (port 8082) — owns booking data in a `Booking` table, and is the only service the client talks to directly. Before saving a booking, it calls movie-service to confirm the showing exists and has enough seats. After saving, it calls movie-service again to decrement the seat count. Updating or deleting a booking calls movie-service to adjusts seats.

- **notification-service** (port 8085) — owns notification data for the android app. Allows users on the adnroid app to view notifications which return their bookings.

- **Websocket** (port 8084) — Receives events when seats change (a user books, or updates their seats), a new showing is created by admin or a showing is deleted by admin. The changes happen live if any user has the relevant web pages open.

Owns `users` table, holds user accoutns and provides distinct roles for each user.  The two roles are admin and user.

- The 3 services use separate databases.
- The client is a set of plain HTML pages served as static resources from booking-service

- **Checkpoints** - checkpoints are tagged in the github repo. Checkpoints achieved are websocket and messgae broker. Checkpoint attempted is mobile specific feature (partially achieved)
## Tech stack

- Java 21, Spring Boot 4.0.8
- Spring Web, Spring Data JPA, Spring Validation, Spring Boot Actuator
- MySQL (remote, via AWS RDS)
- Plain HTML/JavaScript (no frontend framework)

## GITHUB

- See link for development process: https://github.com/n8bvnks/Movie_Booking_System

## Setup

All services need the database password, for each service in each terminal window before running: $env:DB_PASSWORD = "password"


## Running

Start movie-service:

```
cd movie-service
$env:DB_PASSWORD = "..."
.\mvnw.cmd spring-boot:run
```

Then booking-service, in a separate terminal:

```
cd booking-service
$env:DB_PASSWORD = "..."
.\mvnw.cmd spring-boot:run
```

Then notification-service in a seperate terminal
```
cd notification-service
$env:DB_PASSWORD = "..."
.\mvnw.cmd spring-boot:run
```

Once both all running, open `http://localhost:8082/login.html` in a browser.

## Client pages

- `index.html` — landing page, lists every showing grouped by movie, click the button to expand showtimes
- `booking-client.html` — booking form with cascading movie/date/time dropdowns returns a Booking ID as a confirmation code
- `admin.html` — admin landing pagem gives additional functionality (add movies, view all bookings for a showtime, delete showtimes or whole movie)
- `login.html` — log in page
- `my-bookings.html` — customer view of their own bookings
- `update-booking.html` — check, update, or delete an existing booking for a customer

## API endpoints


## Booking Service

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/login` | Checks the username and password and returns an authentication token. |
| `GET` | `/api/v1/movies` | Lists every movie showing with the number of seats remaining. |
| `POST` | `/api/v1/bookings` | Creates a booking for the logged-in user and deducts the seats from the showing. |
| `GET` | `/api/v1/bookings/mine` | Lists only the logged-in customer's bookings. |
| `GET` | `/api/v1/bookings/{id}` | Returns the details of a specific booking. Only the booking owner or an admin can access it; otherwise, returns `403 Forbidden`. |
| `POST` | `/api/v1/bookings/{id}/update` | Changes the number of seats on a booking and adjusts the showing's remaining seats accordingly. |
| `POST` | `/api/v1/bookings/{id}/delete` | Cancels a booking and restores its seats to the showing. |
| `GET` | `/api/v1/live/snapshot` | Returns the current showings and live version number so a page can catch up after connecting. |
| `GET` | `/api/v1/admin/bookings` | **Admin only:** Lists every booking in the system. |
| `POST` | `/api/v1/admin/showings` | **Admin only:** Adds a new showing and sends a live update. |
| `GET` | `/api/v1/admin/showings/bookings?movieTitle=&date=&time=` | **Admin only:** Lists the bookings for a specific showing, including booking ID, customer name, and number of seats. |
| `DELETE` | `/api/v1/admin/showings?movieTitle=&date=&time=` | **Admin only:** Deletes a specific showing, cancels its bookings, and sends a live update. |
| `DELETE` | `/api/v1/admin/movies?movieTitle=` | **Admin only:** Deletes every showing of a movie, cancels all associated bookings, and sends a live update for each deletion. |

## Movie Service

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/movies` | Lists every showing in the `MovieShowing` table. |
| `GET` | `/api/v1/movies/{title}?date=&time=` | Returns a specific showing or `404 Not Found` if it does not exist. Used to check availability. |
| `POST` | `/api/v1/movies` | Creates a new showing. Returns `409 Conflict` if a showing with the same title, date, and time already exists. |
| `POST` | `/api/v1/movies/seats` | Reduces the number of seats remaining when a booking is made. |
| `POST` | `/api/v1/movies/seats/restore` | Restores seats when a booking is reduced or cancelled. |
| `DELETE` | `/api/v1/movies/{title}?date=&time=` | Deletes a specific showing. |
| `DELETE` | `/api/v1/movies/{title}/all` | Deletes every showing of a movie. |

## Notification Service

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/notifications/mine?username=` | Returns the booking confirmations stored for the specified user, newest first. Used by the Android Notifications tab. |


## Testing

Request JSON files for every test case are in `requests/`. Example:

```
curl.exe -i -X POST http://localhost:8082/api/v1/bookings -H "Content-Type: application/json" --data-binary "@requests/create-valid-booking.json"
```

Seed data covers 5 movies, showing daily from 21-30 September 2026 at 12pm and 5pm, 10 seats each (`movie-service/src/main/resources/data.sql`).

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
