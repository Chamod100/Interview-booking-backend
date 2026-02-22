package edu.icet.Repository;

import edu.icet.model.Entity.InterviewSlot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Long> {

    @EntityGraph(attributePaths = {"interviewer"})
    List<InterviewSlot> findAll();

    boolean existsByInterviewerIdAndStartTime(Long interviewerId, java.time.LocalDateTime startTime);
}