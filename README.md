# Movie Booking System

A distributed movie booking application built with Spring Boot, split into two independently running services that communicate over HTTP.

# Architecture

- **movie-service** (port 8083) — owns movie data in a single `MovieShowing` table: title, genre, description, runtime, date, time, and remaining seats. 

- **booking-service** (port 8082) — owns booking data in a `Booking` table, and is the only service the client talks to directly. Before saving a booking, it calls movie-service to confirm the showing exists and has enough seats. After saving, it calls movie-service again to decrement the seat count. Updating or deleting a booking calls movie-service to adjusts seats.
- The two services use separate databases.
- The client is a set of plain HTML pages served as static resources from booking-service

## Tech stack

- Java 21, Spring Boot 4.0.8
- Spring Web, Spring Data JPA, Spring Validation, Spring Boot Actuator
- MySQL (remote, via AWS RDS)
- Plain HTML/JavaScript (no frontend framework)

## Setup

Both services need the database password, for each service in each terminal window before running: $env:DB_PASSWORD = "password"


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

Once both are running, open `http://localhost:8082/index.html` in a browser.

## Client pages

- `index.html` — landing page, lists every showing grouped by movie, click the button to expand showtimes
- `booking-client.html` — booking form with cascading movie/date/time dropdowns returns a Booking ID as a confirmation code
- `update-booking.html` — check, update, or delete an existing booking by ID

## API endpoints

**movie-service**
- `GET /api/v1/movies` — list every showing
- `GET /api/v1/movies/{title}?date=&time=` — check one specific showing
- `POST /api/v1/movies/seats` — decrement seats (called by booking-service only)
- `POST /api/v1/movies/seats/restore` — restore seats (called by booking-service only)

**booking-service**
- `GET /api/v1/movies` — pass-through to movie-service's list (used by the landing page)
- `POST /api/v1/bookings` — create a booking
- `GET /api/v1/bookings/{id}` — check a booking
- `POST /api/v1/bookings/{id}/update` — update a booking's name and/or seat count
- `POST /api/v1/bookings/{id}/delete` — delete a booking, restores its seats

## Testing

Request JSON files for every test case are in `requests/`. Example:

```
curl.exe -i -X POST http://localhost:8082/api/v1/bookings -H "Content-Type: application/json" --data-binary "@requests/create-valid-booking.json"
```

Seed data covers 5 movies, showing daily from 21-30 September 2026 at 12pm and 5pm, 10 seats each (`movie-service/src/main/resources/data.sql`).

