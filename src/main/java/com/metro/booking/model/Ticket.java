package com.metro.booking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    private String ticketId;

    private String sourceStation;
    private String destinationStation;

    private LocalDateTime bookingTime;
    private LocalDateTime expiryTime;

    private boolean entryScanned;
    private boolean used;
}
