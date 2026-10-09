package nz.ac.ara.comp713.booking_service.api;

import jakarta.validation.Valid;
import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.api.dto.CreateShowingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
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
    private final MovieClient movieClient; // used to add showings in movie-service

    public AdminController(BookingService bookingService, MovieClient movieClient) {
        this.bookingService = bookingService;
        this.movieClient = movieClient;
    }

    //GET /api/v1/admin/bookings - admin page checks every booking made by every customer
    //returns a list of BOOKINGRESPONSE, the customer's name is in each one
    @GetMapping("/bookings")
    public List<BookingResponse> allBookings() { return bookingService.listAll(); }

    //POST /api/v1/admin/showings - admin lists a new showing
    //@Valid rejects bad input with 400 before anything is sent on
    //the request is forwarded to movie-service which saves it, 201 on success
    //errors: 409 showing already exists, 503 movie-service down
    @PostMapping(path = "/showings", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse addShowing(@Valid @RequestBody CreateShowingRequest request) {
        return movieClient.createShowing(request);
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
}