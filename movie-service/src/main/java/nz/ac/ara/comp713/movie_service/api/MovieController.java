package nz.ac.ara.comp713.movie_service.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/movies", produces = MediaType.APPLICATION_JSON_VALUE)
public class MovieController {

    private final MovieCatalogue catalogue;

    public MovieController(MovieCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    // movie controller: get movie name, call catalogue, return whatever comes back
    @GetMapping("/{title}")
    public MovieResponse getMovie(@PathVariable String title) {
        return catalogue.findByTitle(title);
    }
}