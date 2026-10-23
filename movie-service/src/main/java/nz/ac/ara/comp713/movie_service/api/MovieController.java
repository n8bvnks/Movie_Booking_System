package nz.ac.ara.comp713.movie_service.api;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/movies", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieController {

    private final MovieCatalogue catalogue;

    public MovieController(MovieCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    // GET /api/v1/movies - every showing, used by the landing page
    @GetMapping
    public List<MovieResponse> listMovies() {
        return catalogue.listAll();
    }

    // POST /api/v1/movies - adds a new showing, called by the booking service when an admin adds one
    // returns 201 with the saved showing, 400 if a field is wrong, 409 if the showing already exists
    @PostMapping
    public ResponseEntity<MovieResponse> createShowing(@Valid @RequestBody CreateShowingRequest request) {
        MovieResponse created = catalogue.createShowing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // checks the movie exists before saving booking, called by movie client before saving a booking
    @GetMapping("/{title}")
    public MovieResponse getMovie(
            @PathVariable String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {

        return catalogue.checkBooking(title, date, time);
    }

    //decrement seats after a booking has been confirmed, called by movie client
    @PostMapping("/seats")
    public MovieResponse decrementSeats(
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time,
            @RequestParam int seats) {

        return catalogue.decrementSeats(title, date, time, seats);
    }

    // called when booking-service cancels/deletes a booking, or reduces
    // its seat count during an update - gives seats back
    @PostMapping("/seats/restore")
    public MovieResponse restoreSeats(
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time,
            @RequestParam int seats) {

        return catalogue.incrementSeats(title, date, time, seats);
    }

    // NEW - DELETE /api/v1/movies/{title}?date=2026-10-15&time=3pm - deletes one showing
    // called by the booking service when an admin deletes a showing
    // returns 204 on success and 404 if there is no such showing
    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteShowing(
            @PathVariable String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {

        catalogue.deleteShowing(title, date, time);
        return ResponseEntity.noContent().build();
    }

    // NEW - DELETE /api/v1/movies/{title}/all - deletes every showing of a movie
    // returns 204 on success and 404 if no showing has that title
    @DeleteMapping("/{title}/all")
    public ResponseEntity<Void> deleteMovie(@PathVariable String title) {
        catalogue.deleteMovie(title);
        return ResponseEntity.noContent().build();
    }
}