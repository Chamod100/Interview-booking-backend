package edu.icet.controller;

import edu.icet.model.DTO.BookingDTO;
import edu.icet.model.DTO.SlotDTO;
import edu.icet.model.Entity.Booking;
import edu.icet.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
//@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO request) {
        try {
            Booking booking = bookingService.bookSlot(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/slots")
    public List<SlotDTO> getAvailableSlots() {
        return bookingService.getAllAvailableSlots();    }
}