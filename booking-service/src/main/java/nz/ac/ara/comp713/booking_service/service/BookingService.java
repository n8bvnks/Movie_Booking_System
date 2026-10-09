package nz.ac.ara.comp713.booking_service.service;

import nz.ac.ara.comp713.booking_service.api.dto.BookingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.api.dto.ConfirmBooking;
import nz.ac.ara.comp713.booking_service.api.dto.UpdateBookingRequest;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
import nz.ac.ara.comp713.booking_service.domain.Booking;
import nz.ac.ara.comp713.booking_service.repository.BookingRepository;
import nz.ac.ara.comp713.booking_service.security.AuthUser;
import nz.ac.ara.comp713.booking_service.security.ForbiddenException;

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

    //called by GET /api/v1/admin/bookings in the admin controller
    //readOnly because it only reads from the database and changes nothing
    @Transactional(readOnly = true)
    public java.util.List<BookingResponse> listAll() {
        //get every booking from the repository, no filter by user because only admins reach this
        //turn each booking into a booking response so the admin sees the customer name, movie, time, date and seats
        return repository.findAll().stream().map(this::toResponse).toList();
    }

        //called by GET /api/v1/admin/showings/bookings in the admin controller
    //returns every booking made for one showing so the admin can see who is coming
    @Transactional(readOnly = true)
    public java.util.List<BookingResponse> listForShowing(String movieTitle, java.time.LocalDate date, String time) {
        String title = movieTitle.trim();
        String timeslot = time.trim();

        //confirm the showing exists first, movie client throws movie not found (404) if it does not
        //so an admin who mistypes a title gets an error instead of an empty list
        movieClient.checkAvailability(title, date, timeslot);

        //get the bookings for this title, time and date, turn each into a booking response
        return repository.findByMovieTitleAndTimeslotAndDate(title, timeslot, date)
                .stream().map(this::toResponse).toList();
    }
    //called by POST /api/v1/bookings in controller, the user is the logged in person
    //the interceptor worked out from their token
    @Transactional
    public BookingResponse createBooking(BookingRequest request, AuthUser user) {
        //the name now comes from the logged in user, not from the booking form
        String name = user.username();
        String movieTitle = request.movieTitle().trim();
        String timeslot = request.timeslot().trim();

        //confirm the showing exists call movie client, either returns
        movieClient.checkAvailability(movieTitle, request.date(), timeslot);

        //if the showing exists, check if this user already has the same booking for same title, date and time
        //if duplicate found throw already booked exception
        if (repository.existsByUserIdAndMovieTitleAndTimeslotAndDate(
                user.id(), movieTitle, timeslot, request.date())) {
            throw new AlreadyBookedException(name, movieTitle, timeslot, request.date());
        }

        //if no duplicate booking, save booking with the user's id and a unique booking id
        Booking saved = repository.save(
                new Booking(user.id(), name, movieTitle, timeslot, request.date(), request.seats()));

        //Once saved decrement seats in movie client repository, if seats available < than the amount of seats requested throw not enough seat
        //exception
        movieClient.decrementSeats(movieTitle, request.date(), timeslot, request.seats());

        return toResponse(saved);
    }

    //called by controller - when client confirms booking with booking id number
    //checks repository, if not found throw booking not found exception
    //else return their name, movie, time, date and amount of seats booked for
    @Transactional(readOnly = true)
    public ConfirmBooking findByBookingNumber(long bookingId, AuthUser user) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        //customers can only see their own booking, admins can see any, otherwise throw forbidden exception
        checkOwner(booking, user);

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
    public BookingResponse updateBooking(long bookingId, UpdateBookingRequest request, AuthUser user) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        //customers can only update their own booking, admins can update any
        checkOwner(booking, user);

        if (request.name() != null && !request.name().isBlank()) {
            booking.setName(request.name().trim());
        }

        if (request.seatsChange() != null && request.seatsChange() != 0) {
            int change = request.seatsChange();
            int newSeatCount = booking.getSeats() + change;

            if (newSeatCount < 1) {
                throw new InvalidUpdateException("The amount of seats you are reducing is less than you have booked for.");
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
    public void deleteBooking(long bookingId, AuthUser user) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        //customers can only delete their own booking, admins can delete any
        checkOwner(booking, user);

        repository.deleteById(bookingId);

        movieClient.restoreSeats(
                booking.getMovieTitle(), booking.getDate(), booking.getTimeslot(), booking.getSeats());
    }

    //used by find, update and delete to make sure the logged in person is allowed to touch this booking
    //admins are allowed to touch any booking, customers only the ones with their own user id
    //if not allowed throw forbidden exception, the error handler turns this into a 403
    private void checkOwner(Booking booking, AuthUser user) {
        if (!user.isAdmin() && !booking.getUserId().equals(user.id())) {
            throw new ForbiddenException("You can only access your own bookings");
        }
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