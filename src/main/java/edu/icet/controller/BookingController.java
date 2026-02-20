package edu.icet.controller;

import edu.icet.model.DTO.BookingDTO;
import edu.icet.model.DTO.SlotDTO;
import edu.icet.model.DTO.StandardResponse;
import edu.icet.model.Entity.Booking;
import edu.icet.model.Entity.Candidate;
import edu.icet.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/book-new")
    public ResponseEntity<StandardResponse> bookForNewCandidate(@RequestBody BookingDTO request) {
        try {
            Booking booking = bookingService.bookForNewCandidate(request);
            return new ResponseEntity<>(
                    new StandardResponse(200, "Success: Booking Confirmed", booking),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new StandardResponse(400, "Error: Booking Failed", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/slots")
    public ResponseEntity<StandardResponse> createSlot(@RequestBody SlotDTO dto) {
        try {
            Object res = bookingService.createNewSlot(dto);
            return new ResponseEntity<>(
                    new StandardResponse(201, "Success: Slot Created", res),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new StandardResponse(400, "Error: Creation Failed", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/slots")
    public ResponseEntity<StandardResponse> getSlots(@RequestParam String date) {
        try {
            List<SlotDTO> slots = bookingService.getAllSlots(date);
            return new ResponseEntity<>(
                    new StandardResponse(200, "Success", slots),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new StandardResponse(500, "Internal Server Error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/details/{slotId}")
    public ResponseEntity<StandardResponse> getBookingDetails(@PathVariable Long slotId) {
        try {
            Candidate candidate = bookingService.getBookingDetails(slotId);
            return new ResponseEntity<>(
                    new StandardResponse(200, "Success", candidate),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new StandardResponse(404, "Not Found", "No booking found for slot ID: " + slotId),
                    HttpStatus.NOT_FOUND
            );
        }
    }
}