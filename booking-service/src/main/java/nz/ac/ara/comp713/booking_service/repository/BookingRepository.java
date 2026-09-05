package nz.ac.ara.comp713.booking_service.repository;

import nz.ac.ara.comp713.booking_service.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //called to confirm booking
    //find the given variables in the database by name movie title, timeslot and Date
    //variables seperated by Capitals, generates a sql query
    boolean existsByNameAndMovieTitleAndTimeslotAndDate(
            String name, String movieTitle, String timeslot, LocalDate date);
}