package nz.ac.ara.comp713.booking_service.service;

import java.time.LocalDate;

public class AlreadyFoundException extends RuntimeException {

    public AlreadyFoundException(String name, String movieTitle, String timeslot, LocalDate date) {
        super(name + " already has a booking for " + movieTitle
                + " at " + timeslot + " on " + date);
    }
}