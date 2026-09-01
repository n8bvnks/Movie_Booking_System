package nz.ac.ara.comp713.booking_service.client;

import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class MovieClient {

    private final RestClient restClient;

    public MovieClient(
            RestClient.Builder builder,
            @Value("${movie.service.url}") String baseUrl) {

        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public MovieResponse getMovie(String title) {
        try {
            return restClient.get()
                    .uri("/api/v1/movies/{title}", title)
                    .retrieve()
                    .body(MovieResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new MovieNotFoundException(title);

        } catch (ResourceAccessException ex) {
            throw new MovieServiceUnavailableException();
        }
    }
}