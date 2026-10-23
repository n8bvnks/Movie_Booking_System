package nz.ac.ara.comp713.booking_service.api;

import jakarta.validation.Valid;
import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.api.dto.CreateShowingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.DeleteShowingResult;
import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
import nz.ac.ara.comp713.booking_service.client.MovieNotFoundException;
import nz.ac.ara.comp713.booking_service.live.LiveUpdates;
import nz.ac.ara.comp713.booking_service.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

//everything under /api/v1/admin is for admins only
//the AuthInterceptor checks the role before a request gets here, a customer gets 403, no token gets 401
@RestController
@RequestMapping(path = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final BookingService bookingService;
    private final MovieClient movieClient; // NEW - used to add and delete showings in movie-service
    private final LiveUpdates liveUpdates; // NEW - tells open browser tabs about a new showing

    public AdminController(BookingService bookingService, MovieClient movieClient, LiveUpdates liveUpdates) {
        this.bookingService = bookingService;
        this.movieClient = movieClient;
        this.liveUpdates = liveUpdates;
    }

    //GET /api/v1/admin/bookings - admin page checks every booking made by every customer
    //returns a list of BOOKINGRESPONSE, the customer's name is in each one
    @GetMapping("/bookings")
    public List<BookingResponse> allBookings() { return bookingService.listAll(); }

    //NEW - POST /api/v1/admin/showings - admin lists a new showing
    //@Valid rejects bad input with 400 before anything is sent on
    //the request is forwarded to movie-service which saves it, 201 on success
    //errors = 409 showing already exists and 503 movie-service down
    @PostMapping(path = "/showings", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse addShowing(@Valid @RequestBody CreateShowingRequest request) {
        MovieResponse created = movieClient.createShowing(request);

        //NEW - the showing is saved, so send the ShowingCreated event to every open browser tab
        liveUpdates.showingCreated(created);

        return created;
    }

    //NEW - GET /api/v1/admin/showings/bookings?movieTitle=...&date=2026-10-15&time=12pm
    //admin sees every booking for one showing
    //a showing has no id of its own, so it is identified by title + date + time like everywhere else in the project
    //errors: 400 missing or badly formatted parameter, 404 no such showing, 503 movie-service down
    @GetMapping("/showings/bookings")
    public List<BookingResponse> showingBookings(
            @RequestParam String movieTitle,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {
        return bookingService.listForShowing(movieTitle, date, time);
    }

    //NEW - DELETE /api/v1/admin/showings?movieTitle=...&date=2026-10-15&time=12pm - admin deletes one showing
    //a showing has no id of its own, so it is identified by title + date + time like everywhere else in the project
    //movie-service deletes the showing, then every booking for it is cancelled, then ShowingDeleted is sent live
    //returns 200 with how many bookings were cancelled, errors: 404 no such showing, 503 movie-service down
    @DeleteMapping("/showings")
    public DeleteShowingResult deleteShowing(
            @RequestParam String movieTitle,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {
        String title = movieTitle.trim();
        String timeslot = time.trim();

        //look the showing up first, movie client throws movie not found (404) if it does not exist
        //it is also kept so the live event can say which showing was removed
        MovieResponse showing = movieClient.checkAvailability(title, date, timeslot);

        movieClient.deleteShowing(title, date, timeslot);

        //the showing is gone, so cancel the bookings that were made for it
        int cancelled = bookingService.cancelBookingsForShowing(title, date, timeslot);

        liveUpdates.showingDeleted(showing);

        return new DeleteShowingResult(
                "Deleted " + title + " on " + date + " at " + timeslot + ", " + cancelled + " bookings cancelled",
                cancelled);
    }

    //NEW - DELETE /api/v1/admin/movies?movieTitle=... - admin deletes a whole movie, every showing of it
    //each showing gets its bookings cancelled and its own ShowingDeleted event
    //errors: 404 no showing has that title, 503 movie-service down
    @DeleteMapping("/movies")
    public DeleteShowingResult deleteMovie(@RequestParam String movieTitle) {
        String title = movieTitle.trim();

        //find the showings first, so we know which bookings to cancel and which events to send
        List<MovieResponse> showings = movieClient.listMovies().stream()
                .filter(showing -> showing.movieTitle().equals(title))
                .toList();

        if (showings.isEmpty()) {
            throw new MovieNotFoundException(title);
        }

        movieClient.deleteMovie(title);

        int cancelled = 0;
        for (MovieResponse showing : showings) {
            cancelled += bookingService.cancelBookingsForShowing(title, showing.date(), showing.time());
            liveUpdates.showingDeleted(showing);
        }

        return new DeleteShowingResult(
                "Deleted " + title + " (" + showings.size() + " showings), " + cancelled + " bookings cancelled",
                cancelled);
    }
}
