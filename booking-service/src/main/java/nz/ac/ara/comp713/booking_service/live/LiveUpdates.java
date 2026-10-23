package nz.ac.ara.comp713.booking_service.live;

import nz.ac.ara.comp713.booking_service.api.dto.MovieResponse;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// the two live events, called by the controllers once a change has been saved
// a failed notification must never make a saved booking or showing look like it failed, so errors are only logged
@Component
public class LiveUpdates {

    private static final Logger log = LoggerFactory.getLogger(LiveUpdates.class);

    private final LiveServer liveServer;
    private final MovieClient movieClient;

    public LiveUpdates(LiveServer liveServer, MovieClient movieClient) {
        this.liveServer = liveServer;
        this.movieClient = movieClient;
    }

    // ShowingCreated - the admin added a new showing, sent with the showing movie-service saved
    public void showingCreated(MovieResponse showing) {
        try {
            liveServer.publish("ShowingCreated", ShowingData.from(showing));
        } catch (RuntimeException e) {
            log.warn("Could not publish ShowingCreated: {}", e.getMessage());
        }
    }

    // SeatsChanged - a booking was made, changed or cancelled, so the seats left for that showing changed
    // movie-service is asked for the seats left now, so the event carries the real number and not a guess
    public void seatsChanged(String movieTitle, LocalDate date, String time) {
        try {
            MovieResponse latest = movieClient.checkAvailability(movieTitle, date, time);
            liveServer.publish("SeatsChanged", ShowingData.from(latest));
        } catch (RuntimeException e) {
            log.warn("Could not publish SeatsChanged: {}", e.getMessage());
        }
    }
}