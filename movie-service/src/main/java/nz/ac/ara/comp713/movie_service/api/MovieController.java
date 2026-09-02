package nz.ac.ara.comp713.movie_service.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/movies", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieController {

    private final MovieCatalogue catalogue;

    //constructor takes catalogue variable
    public MovieController(MovieCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    //get map the title
    @GetMapping("/{title}")
    //take title using pathvariable, date and time as query params from booking-service

    //get movie will return in the shape of movie response
    public MovieResponse getMovie(
            @PathVariable String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {

        //call checkBooking in movie catalogue
        //will return the showing if it exists, or throw a movie not found exception
        return catalogue.checkBooking(title, date, time);
    }

    // PATCH /api/v1/movies/seats?title=...&date=...&time=...&seats=...
    // called by booking-service AFTER a booking is saved
    @PatchMapping("/seats")
    public MovieResponse decrementSeats(
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time,
            @RequestParam int seats) {

        return catalogue.decrementSeats(title, date, time, seats);
    }
}