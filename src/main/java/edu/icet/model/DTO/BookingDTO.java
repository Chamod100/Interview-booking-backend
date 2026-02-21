package edu.icet.model.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long slotId;
    private LocalDateTime startTime;
    private String candidateName;
    private String candidateEmail;
    private String idNumber;
    private String contact;
    private String specialization;
}

