package nz.ac.ara.comp713.booking_service.api;

import nz.ac.ara.comp713.booking_service.api.dto.LiveSnapshot;
import nz.ac.ara.comp713.booking_service.client.MovieClient;
import nz.ac.ara.comp713.booking_service.live.LiveServer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/live", produces = MediaType.APPLICATION_JSON_VALUE)
public class LiveController {

    private final LiveServer liveServer;
    private final MovieClient movieClient;

    public LiveController(LiveServer liveServer, MovieClient movieClient) {
        this.liveServer = liveServer;
        this.movieClient = movieClient;
    }

    // GET /api/v1/live/snapshot - every showing as it is now, plus the live version number
    // the browser calls this straight after the websocket says "subscribed", and again after every reconnect
    // the version is read BEFORE the showings, so an event that happens in between is never missed,
    // at worst the browser applies it a second time and the result is the same because events carry the full showing
    @GetMapping("/snapshot")
    public LiveSnapshot snapshot() {
        long version = liveServer.currentVersion();
        return new LiveSnapshot(version, movieClient.listMovies());
    }
}