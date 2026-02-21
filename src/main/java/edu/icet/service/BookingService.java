package edu.icet.service;

import edu.icet.Repository.BookingRepository;
import edu.icet.Repository.CandidateRepository;
import edu.icet.Repository.InterviewSlotRepository;
import edu.icet.Repository.InterviewerRepository;
import edu.icet.model.DTO.BookingDTO;
import edu.icet.model.DTO.SlotDTO;
import edu.icet.model.Entity.Booking;
import edu.icet.model.Entity.Candidate;
import edu.icet.model.Entity.InterviewSlot;
import edu.icet.model.Entity.Interviewer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final InterviewSlotRepository slotRepo;
    private final CandidateRepository candidateRepo;
    private final InterviewerRepository interviewerRepo;
    private final JavaMailSender mailSender; // නිවැරදිව Inject කිරීම

    @Transactional
    public InterviewSlot createNewSlot(SlotDTO dto) {
        boolean exists = slotRepo.existsByInterviewerIdAndStartTime(dto.getInterviewerId(), dto.getStartTime());
        if (exists) {
            throw new RuntimeException("Conflict: This interviewer already has a slot at this time!");
        }

        InterviewSlot slot = new InterviewSlot();
        slot.setStartTime(dto.getStartTime());
        slot.setEndTime(dto.getStartTime().plusMinutes(30));
        slot.setBooked(false);

        Interviewer interviewer = interviewerRepo.findById(dto.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Error: Interviewer not found!"));
        slot.setInterviewer(interviewer);

        return slotRepo.save(slot);
    }

    @Transactional
    public Booking bookForNewCandidate(BookingDTO request) {
        Candidate candidate = candidateRepo.findByEmail(request.getCandidateEmail())
                .orElseGet(() -> {
                    Candidate newCandidate = new Candidate();
                    newCandidate.setName(request.getCandidateName());
                    newCandidate.setEmail(request.getCandidateEmail());
                    newCandidate.setIdNumber(request.getIdNumber());
                    newCandidate.setContactNumber(request.getContact());
                    newCandidate.setSpecialization(request.getSpecialization());
                    return candidateRepo.save(newCandidate);
                });

        InterviewSlot slot;

        if (request.getSlotId() == null) {
            slot = new InterviewSlot();
            slot.setStartTime(request.getStartTime());
            slot.setEndTime(request.getStartTime().plusMinutes(30));
            slot.setBooked(true);

            Interviewer defaultInterviewer = interviewerRepo.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No interviewers available!"));
            slot.setInterviewer(defaultInterviewer);
            slot = slotRepo.save(slot);
        } else {
            slot = slotRepo.findById(request.getSlotId())
                    .orElseThrow(() -> new RuntimeException("Error: Slot not found!"));
            if (slot.isBooked()) {
                throw new RuntimeException("Conflict: Already booked!");
            }
            slot.setBooked(true);
            slot = slotRepo.save(slot);
        }

        // 3. බුකින් එක සේව් කිරීම
        Booking booking = new Booking();
        booking.setCandidate(candidate);
        booking.setSlot(slot);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepo.save(booking);

        // ඇත්තටම ඊමේල් එකක් යැවීම
        sendEmail(candidate.getEmail(), candidate.getName(), slot.getStartTime());

        return savedBooking;
    }

    public void sendEmail(String toEmail, String candidateName, LocalDateTime time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Interview Confirmation - ProHire");
            message.setText("Hi " + candidateName + ",\n\nYour interview is scheduled for: " + time);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }
    }

    public List<SlotDTO> getAllSlots(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        return slotRepo.findAll().stream()
                .filter(slot -> !slot.getStartTime().isBefore(startOfDay) && !slot.getStartTime().isAfter(endOfDay))
                .sorted(Comparator.comparing(InterviewSlot::getStartTime))
                .map(slot -> {
                    SlotDTO dto = new SlotDTO();
                    dto.setId(slot.getId());
                    dto.setStartTime(slot.getStartTime());
                    if (slot.getInterviewer() != null) {
                        dto.setInterviewerName(slot.getInterviewer().getName());
                    }
                    dto.setBooked(slot.isBooked());
                    return dto;
                }).collect(Collectors.toList());
    }

    public Candidate getBookingDetails(Long slotId) {
        return bookingRepo.findBySlotId(slotId)
                .map(Booking::getCandidate)
                .orElseThrow(() -> new RuntimeException("No booking found"));
    }
}