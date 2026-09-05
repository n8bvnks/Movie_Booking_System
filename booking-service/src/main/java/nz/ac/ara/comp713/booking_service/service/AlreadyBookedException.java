package nz.ac.ara.comp713.booking_service.service;

import java.time.LocalDate;

public class AlreadyBookedException extends RuntimeException {

    public AlreadyBookedException(String name, String movieTitle, String timeslot, LocalDate date) {
        super(name + " already has a booking for " + movieTitle
                + " at " + timeslot + " on " + date);
    }
}