package nz.ac.ara.comp713.booking_service.api.dto;

import java.util.List;

// the full current state fetched by the browser when it connects or reconnects
// version is the live version at the moment the snapshot was taken, only events with a higher version are applied after it
public record LiveSnapshot(long version, List<MovieResponse> showings) {
}