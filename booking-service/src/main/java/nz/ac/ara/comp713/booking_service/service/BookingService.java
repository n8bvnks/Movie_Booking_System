package nz.ac.ara.comp713.booking_service.service;

import nz.ac.ara.comp713.booking_service.api.dto.BookingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.api.dto.ConfirmBooking;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
import nz.ac.ara.comp713.booking_service.domain.Booking;
import nz.ac.ara.comp713.booking_service.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final MovieClient movieClient;

    public BookingService(BookingRepository repository, MovieClient movieClient) {
        this.repository = repository;
        this.movieClient = movieClient;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        String name = request.name().trim();
        String movieTitle = request.movieTitle().trim();
        String timeslot = request.timeslot().trim();

        //confirm the showing exists, a network call nothing saved yet
        movieClient.checkAvailability(movieTitle, request.date(), timeslot);

        // 2. check for a duplicate booking - a local database check
        if (repository.existsByNameAndMovieTitleAndTimeslotAndDate(
                name, movieTitle, timeslot, request.date())) {
            throw new AlreadyFoundException(name, movieTitle, timeslot, request.date());
        }

        // 3. save the booking locally
        Booking saved = repository.save(
                new Booking(name, movieTitle, timeslot, request.date(), request.seats()));

        // 4. only after saving succeeds, tell movie-service to reduce the seats
        movieClient.decrementSeats(movieTitle, request.date(), timeslot, request.seats());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ConfirmBooking findByBookingNumber(long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        return new ConfirmBooking(
                booking.getName(),
                booking.getMovieTitle(),
                booking.getTimeslot(),
                booking.getDate(),
                booking.getSeats()
        );
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getName(),
                booking.getMovieTitle(),
                booking.getTimeslot(),
                booking.getDate(),
                booking.getSeats()
        );
    }
}