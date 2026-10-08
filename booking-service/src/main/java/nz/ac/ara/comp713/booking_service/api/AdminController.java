package nz.ac.ara.comp713.booking_service.api;

import nz.ac.ara.comp713.booking_service.api.dto.BookingResponse;
import nz.ac.ara.comp713.booking_service.service.BookingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//everything under /api/v1/admin is for admins only
//the AuthInterceptor checks the role before a request gets here, a customer gets 403, no token gets 401
@RestController
@RequestMapping(path = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final BookingService bookingService;

    public AdminController(BookingService bookingService) { this.bookingService = bookingService; }

    //GET /api/v1/admin/bookings - admin page checks every booking made by every customer
    //returns a list of BOOKINGRESPONSE, the customer's name is in each one
    @GetMapping("/bookings")
    public List<BookingResponse> allBookings() { return bookingService.listAll(); }
}