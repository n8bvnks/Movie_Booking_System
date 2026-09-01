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

    //constructor takes catalogue variable
    public MovieController(MovieCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    //get map the title
    @GetMapping("/{title}")
    //take title using pathvariable

    //get movie will return in the shape of movie response
    public MovieResponse getMovie(@PathVariable String title) {
        
        //call findbytitle in movie catalogue
        //will return the movie name if exists, or return a movie not found exception
        return catalogue.findByTitle(title);
    }
}