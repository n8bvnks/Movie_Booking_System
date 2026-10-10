package nz.ac.ara.comp713.booking_service.api;

import jakarta.validation.Valid;
import nz.ac.ara.comp713.booking_service.api.dto.BookingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.api.dto.ConfirmBooking;
import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;
import nz.ac.ara.comp713.booking_service.api.dto.UpdateBookingRequest;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
import nz.ac.ara.comp713.booking_service.security.AuthInterceptor;
import nz.ac.ara.comp713.booking_service.security.AuthUser;
import nz.ac.ara.comp713.booking_service.service.BookingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {

    private final BookingService bookingService;
    private final MovieClient movieClient;

    public BookingController(BookingService bookingService, MovieClient movieClient) {
        this.bookingService = bookingService;
        this.movieClient = movieClient;
    }

    // GET /api/v1/movies - landing page to display the available list of movies from movie client db
    @GetMapping("/movies")
    public List<MovieResponse> listMovies() {
        return movieClient.listMovies();
    }

    //POST /api/v1/bookings, creates a booking response obj - send to booking service
    //takes a booking request and sends to booking service
    //the user is the logged in person, the interceptor puts them on the request after checking the token
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> create(
            @Valid @RequestBody BookingRequest request,
            @RequestAttribute(AuthInterceptor.USER_ATTRIBUTE) AuthUser user) {
        BookingResponse created = bookingService.createBooking(request, user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/api/v1/bookings/{id}")
                .buildAndExpand(created.bookingId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    //NEW - GET /api/v1/bookings/mine - the My Bookings page shows the logged in customer's own bookings
    //returns a list of BOOKINGRESPONSE, only bookings with this user's id
    //spring picks this fixed path before /bookings/{id}, so "mine" is not read as a booking number
    @GetMapping("/bookings/mine")
    public List<BookingResponse> myBookings(
            @RequestAttribute(AuthInterceptor.USER_ATTRIBUTE) AuthUser user) {
        return bookingService.listMine(user);
    }

    //GET /api/v1/bookings/booking id - booking id page checks a customers booking when given a booking
    //id number, returns a CONFIRMBOOKING response
    //the user is passed on so the service can check the booking belongs to them
    @GetMapping("/bookings/{id}")
    public ConfirmBooking getByBookingNumber(
            @PathVariable long id,
            @RequestAttribute(AuthInterceptor.USER_ATTRIBUTE) AuthUser user) {
        return bookingService.findByBookingNumber(id, user);
    }

    // POST /api/v1/bookings/{id}/update - update name and/or seat count
    //only the owner of the booking or an admin can update it
    @PostMapping("/bookings/{id}/update")
    public BookingResponse update(
            @PathVariable long id,
            @RequestBody UpdateBookingRequest request,
            @RequestAttribute(AuthInterceptor.USER_ATTRIBUTE) AuthUser user) {
        return bookingService.updateBooking(id, request, user);
    }

    // POST /api/v1/bookings/{id}/delete - delete a booking, restores seats
    //only the owner of the booking or an admin can delete it
    @PostMapping("/bookings/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable long id,
            @RequestAttribute(AuthInterceptor.USER_ATTRIBUTE) AuthUser user) {
        bookingService.deleteBooking(id, user);
        return ResponseEntity.noContent().build();
    }
}