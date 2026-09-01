package nz.ac.ara.comp713.movie_service.api;

// the shape of a movie sent out over HTTP

public record MovieResponse(
        String title,
        int runtime, // in minutes
        String timeslot,
        String genre,
        String description
) {
}