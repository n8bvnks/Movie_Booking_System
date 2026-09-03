package nz.ac.ara.comp713.booking_service.repository;

import nz.ac.ara.comp713.booking_service.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByNameAndMovieTitleAndTimeslotAndDate(
            String name, String movieTitle, String timeslot, LocalDate date);
}