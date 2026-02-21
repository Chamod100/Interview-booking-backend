package edu.icet.Repository;

import edu.icet.model.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByCandidateIdAndSlotStartTime(Long candidateId, LocalDateTime startTime);
    Optional<Booking> findBySlotId(Long slotId);
}
