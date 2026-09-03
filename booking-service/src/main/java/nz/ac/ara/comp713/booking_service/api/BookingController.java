package nz.ac.ara.comp713.booking_service.api;

import jakarta.validation.Valid;
import nz.ac.ara.comp713.booking_service.api.dto.BookingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.api.dto.ConfirmBooking;
import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
import nz.ac.ara.comp713.booking_service.service.BookingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    // GET /api/v1/movies - pass-through, so the landing page only ever
    // talks to booking-service, never directly to movie-service
    @GetMapping("/movies")
    public List<MovieResponse> listMovies() {
        return movieClient.listMovies();
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        BookingResponse created = bookingService.createBooking(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/api/v1/bookings/{id}")
                .buildAndExpand(created.bookingId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/bookings/{id}")
    public ConfirmBooking getByBookingNumber(@PathVariable long id) {
        return bookingService.findByBookingNumber(id);
    }
}