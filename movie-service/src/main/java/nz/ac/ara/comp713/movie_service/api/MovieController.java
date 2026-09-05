package nz.ac.ara.comp713.movie_service.api;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/movies", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieController {

    private final MovieCatalogue catalogue;

    public MovieController(MovieCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    // GET /api/v1/movies - every showing, used by the landing page
    @GetMapping
    public List<MovieResponse> listMovies() {
        return catalogue.listAll();
    }

    // called by check availiability
    @GetMapping("/{title}")
    public MovieResponse getMovie(
            @PathVariable String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time) {

        return catalogue.checkBooking(title, date, time);
    }

    @PostMapping("/seats")
    public MovieResponse decrementSeats(
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String time,
            @RequestParam int seats) {

        return catalogue.decrementSeats(title, date, time, seats);
    }
}