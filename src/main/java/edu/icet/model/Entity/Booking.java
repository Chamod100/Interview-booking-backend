package edu.icet.model.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime bookingDate;
    private String status;

    @ManyToOne
    private Candidate candidate;

    @OneToOne
    private InterviewSlot slot;
}
