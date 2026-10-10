package nz.ac.ara.comp713.booking_service.client;

import nz.ac.ara.comp713.booking_service.api.dto.CreateShowingRequest;
import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

@Component
public class MovieClient {

    private final RestClient restClient;

    public MovieClient(@Value("${movie.service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    //lists movies from get/api/v1/movies
    public List<MovieResponse> listMovies() {
        try {
            return restClient.get()
                    .uri("/api/v1/movies")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MovieResponse>>() {});

        } catch (ResourceAccessException ex) {
            throw new MovieServiceUnavailableException();
        }
    }

    //check availability called by booking service
    public MovieResponse checkAvailability(String movieTitle, LocalDate date, String time) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/movies/{title}")
                            .queryParam("date", date)
                            .queryParam("time", time)
                            .build(movieTitle))
                    .retrieve()
                    .body(MovieResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new MovieNotFoundException(movieTitle, date.toString(), time);

        } catch (ResourceAccessException ex) {
            throw new MovieServiceUnavailableException();
        }
    }

    public void decrementSeats(String movieTitle, LocalDate date, String time, int seats) {
        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/movies/seats")
                            .queryParam("title", movieTitle)
                            .queryParam("date", date)
                            .queryParam("time", time)
                            .queryParam("seats", seats)
                            .build())
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.Conflict ex) {

            int available = checkAvailability(movieTitle, date, time).seats();

            throw new NotEnoughSeatsException(movieTitle, seats, available);

        } catch (ResourceAccessException ex) {
            throw new MovieServiceUnavailableException();
        }
    }

    // called when a booking is cancelled/deleted, or its seat count is
    // reduced during an update - gives the seats back to movie-service
    public void restoreSeats(String movieTitle, LocalDate date, String time, int seats) {
        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/movies/seats/restore")
                            .queryParam("title", movieTitle)
                            .queryParam("date", date)
                            .queryParam("time", time)
                            .queryParam("seats", seats)
                            .build())
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new MovieNotFoundException(movieTitle, date.toString(), time);

        } catch (ResourceAccessException ex) {
            throw new MovieServiceUnavailableException();
        }
    }

    // new - called by the admin endpoint to add a showing
    // forwards the admin's request to POST /api/v1/movies on movie-service
    // movie-service owns the showings, so booking-service never writes them itself
    public MovieResponse createShowing(CreateShowingRequest request) {
        try {
            return restClient.post()
                    .uri("/api/v1/movies")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MovieResponse.class);

        // 409 from movie-service = same title/date/time already listed
        } catch (HttpClientErrorException.Conflict ex) {
            throw new ShowingAlreadyExistsException(
                    request.movieTitle(), request.date().toString(), request.time());

        // movie-service is down or unreachable
        } catch (ResourceAccessException ex) {
            throw new MovieServiceUnavailableException();
        }
    }
}