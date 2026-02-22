package edu.icet.model.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SlotDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long interviewerId;
    private String interviewerName;
    private boolean isBooked;
}
