package edu.icet.service;

import edu.icet.Repository.BookingRepository;
import edu.icet.Repository.CandidateRepository;
import edu.icet.Repository.InterviewSlotRepository;
import edu.icet.model.DTO.BookingDTO;
import edu.icet.model.DTO.SlotDTO;
import edu.icet.model.Entity.Booking;
import edu.icet.model.Entity.Candidate;
import edu.icet.model.Entity.InterviewSlot;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final InterviewSlotRepository slotRepo;
    private final CandidateRepository candidateRepo;

    @Transactional
    public Booking bookSlot(BookingDTO request) {

        InterviewSlot slot = slotRepo.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Error: Interview slot not found!"));

        Candidate candidate = candidateRepo.findById(request.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Error: Candidate not found!"));

        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Validation Error: Cannot book a slot in the past.");
        }

        if (slot.isBooked()) {
            throw new RuntimeException("Conflict Error: This slot has already been booked by another user.");
        }

        boolean exists = bookingRepo.existsByCandidateIdAndSlotStartTime(candidate.getId(), slot.getStartTime());
        if (exists) {
            throw new RuntimeException("Conflict Error: You already have another interview scheduled at this time.");
        }

        slot.setBooked(true);
        slotRepo.save(slot);

        Booking booking = new Booking();
        booking.setCandidate(candidate);
        booking.setSlot(slot);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepo.save(booking);

        simulateEmail(candidate.getEmail(), slot.getStartTime());

        return savedBooking;
    }

    private void simulateEmail(String email, LocalDateTime time) {
        System.out.println("--------------------------------------------------");
        System.out.println("LOG: Email sent to: " + email);
        System.out.println("LOG: Interview confirmed for: " + time);
        System.out.println("--------------------------------------------------");
    }

    public List<SlotDTO> getAllAvailableSlots() {
        return slotRepo.findByIsBookedFalse().stream().map(slot -> {
            SlotDTO dto = new SlotDTO();
            dto.setId(slot.getId());
            dto.setStartTime(slot.getStartTime());
            dto.setInterviewerName(slot.getInterviewer().getName());
            dto.setBooked(slot.isBooked());
            return dto;
        }).collect(Collectors.toList());
    }
}
