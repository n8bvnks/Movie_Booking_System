package nz.ac.ara.comp713.booking_service.service;

import nz.ac.ara.comp713.booking_service.api.dto.BookingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.api.dto.ConfirmBooking;
import nz.ac.ara.comp713.booking_service.api.dto.UpdateBookingRequest;
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

    //called by POST /api/v1/bookings in controller,
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        String name = request.name().trim();
        String movieTitle = request.movieTitle().trim();
        String timeslot = request.timeslot().trim();

        //confirm the showing exists call movie client, either returns
        movieClient.checkAvailability(movieTitle, request.date(), timeslot);

        //if the showing exists, check if there is the same booking for same name, title, date and time
        //if duplicate found throw already booked exception
        if (repository.existsByNameAndMovieTitleAndTimeslotAndDate(
                name, movieTitle, timeslot, request.date())) {
            throw new AlreadyBookedException(name, movieTitle, timeslot, request.date());
        }

        //if no duplicate booking, save booking in repository with unique booking id
        Booking saved = repository.save(
                new Booking(name, movieTitle, timeslot, request.date(), request.seats()));

        //Once saved decrement seats in movie client repository, if seats available < than the amount of seats requested throw not enough seat
        //exception
        movieClient.decrementSeats(movieTitle, request.date(), timeslot, request.seats());

        return toResponse(saved);
    }

    //called by controller - when client confirms booking with booking id number
    //checks repository, if not found throw booking not found exception
    //else return their name, movie, time, date and amount of seats booked for
    @Transactional(readOnly = true)
    public ConfirmBooking findByBookingNumber(long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        String message = "Booking confirmed for " + booking.getName()
                + " for movie " + booking.getMovieTitle()
                + " at " + booking.getTimeslot()
                + " on " + booking.getDate()
                + " for " + booking.getSeats() + " people";

        return new ConfirmBooking(
                message,
                booking.getName(),
                booking.getMovieTitle(),
                booking.getTimeslot(),
                booking.getDate(),
                booking.getSeats()
        );
    }

    // updates a booking's name and/or seat count
    @Transactional
    public BookingResponse updateBooking(long bookingId, UpdateBookingRequest request) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (request.name() != null && !request.name().isBlank()) {
            booking.setName(request.name().trim());
        }

        if (request.seatsChange() != null && request.seatsChange() != 0) {
            int change = request.seatsChange();
            int newSeatCount = booking.getSeats() + change;

            if (newSeatCount < 1) {
                throw new InvalidUpdateException("A booking must have at least 1 seat");
            }

            if (change > 0) {
                // asking for MORE seats - check movie-service has enough spare capacity
                movieClient.decrementSeats(
                        booking.getMovieTitle(), booking.getDate(), booking.getTimeslot(), change);
            } else {
                // giving seats back
                movieClient.restoreSeats(
                        booking.getMovieTitle(), booking.getDate(), booking.getTimeslot(), -change);
            }

            booking.setSeats(newSeatCount);
        }

        Booking saved = repository.save(booking);
        return toResponse(saved);
    }

    // deletes a booking, restores its seats to movie-service
    @Transactional
    public void deleteBooking(long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        repository.deleteById(bookingId);

        movieClient.restoreSeats(
                booking.getMovieTitle(), booking.getDate(), booking.getTimeslot(), booking.getSeats());
    }

    //used when booking is created or updated, to build the response
    private BookingResponse toResponse(Booking booking) {
        String message = "Thank you for booking " + booking.getName()
                + " for movie " + booking.getMovieTitle()
                + " at " + booking.getTimeslot()
                + " on " + booking.getDate()
                + " for " + booking.getSeats() + " people. Your booking id is "
                + booking.getBookingId();

        return new BookingResponse(
                message,
                booking.getBookingId(),
                booking.getName(),
                booking.getMovieTitle(),
                booking.getTimeslot(),
                booking.getDate(),
                booking.getSeats()
        );
    }
}