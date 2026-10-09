package nz.ac.ara.comp713.booking_service.repository;

import nz.ac.ara.comp713.booking_service.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //called to confirm booking
    //find the given variables in the database by user id, movie title, timeslot and Date
    //variables seperated by Capitals, generates a sql query
    //checks by user id instead of name so two different users with the same name do not clash
    boolean existsByUserIdAndMovieTitleAndTimeslotAndDate(
            Long userId, String movieTitle, String timeslot, LocalDate date);


    //called by the admin showing bookings endpoint
    //finds every booking for one showing - a showing is identified by its title, timeslot and date
    //the method name is turned into a sql query, same as the exists method above
    List<Booking> findByMovieTitleAndTimeslotAndDate(String movieTitle, String timeslot, LocalDate date);

}