package nz.ac.ara.comp713.movie_service.api;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MovieCatalogue {

    // hardcoded movie data for now, no database connected yet
    private final Map<String, MovieResponse> movies = Map.of(
            "Inception", new MovieResponse(
                    "Inception", 148, "8am", "Sci-Fi",
                    "A thief who steals corporate secrets through dream-sharing technology."),
            "Dune", new MovieResponse(
                    "Dune", 155, "10am", "Sci-Fi",
                    "A noble family becomes embroiled in a war for control over a desert planet."),
            "The Grand Budapest Hotel", new MovieResponse(
                    "The Grand Budapest Hotel", 99, "8am", "Comedy",
                    "The adventures of a legendary concierge and his protege at a famous European hotel."),
            "Parasite", new MovieResponse(
                    "Parasite", 132, "10am", "Thriller",
                    "Greed and class discrimination threaten the newly formed symbiotic relationship between two families.")
    );

    public MovieResponse findByTitle(String rawTitle) {
        String title = rawTitle.trim();
        MovieResponse movie = movies.get(title);
        if (movie == null) {
            throw new MovieNotFoundException(rawTitle);
        }
        return movie;
    }
}