package edu.icet.Repository;

import edu.icet.model.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByCandidateIdAndSlotStartTime(Long candidateId, LocalDateTime startTime);
}
