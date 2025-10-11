package com.metro.booking.service;

import com.metro.booking.model.Ticket;
import com.metro.booking.repository.StationRedisRepository;
import com.metro.booking.repository.TicketJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final TicketJpaRepository ticketJpaRepository;
    private final StationRedisRepository stationRedisRepository;

    public String validateTicket(String ticketId, String stationId, String scanType) {
        log.info("Validating ticket [{}] at station [{}] for scanType [{}]", ticketId, stationId, scanType);

        // ✅ Step 1: Quick Redis check
        boolean isActive = stationRedisRepository.isTicketActive(stationId, ticketId);
        if (!isActive) {
            return "❌ Ticket not active at this station or expired.";
        }

        // ✅ Step 2: Retrieve full ticket details
        Ticket ticket = ticketJpaRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            // Remove stale entry if exists in Redis
            stationRedisRepository.removeTicket(stationId, ticketId);
            return "❌ Ticket not found in database.";
        }

        // ✅ Step 3: Expiry check
        if (ticket.getExpiryTime().isBefore(LocalDateTime.now())) {
            stationRedisRepository.removeTicket(stationId, ticketId);
            return "⏰ Ticket expired.";
        }

        // ✅ Step 4: Handle based on scan type
        return switch (scanType.toUpperCase()) {
            case "ENTRY" -> handleEntryScan(ticket, stationId);
            case "EXIT" -> handleExitScan(ticket, stationId);
            default -> "❗ Invalid scan type. Use 'ENTRY' or 'EXIT'.";
        };
    }

    private String handleEntryScan(Ticket ticket, String stationId) {
        if (ticket.isEntryScanned()) {
            return "⚠️ Ticket already scanned at entry.";
        }

        if (!ticket.getSourceStation().equalsIgnoreCase(stationId)) {
            return "🚫 Invalid entry station. Expected: " + ticket.getSourceStation();
        }

        ticket.setEntryScanned(true);
        ticketJpaRepository.save(ticket);

        // ✅ Remove from Redis at source station after entry
        stationRedisRepository.removeTicket(stationId, ticket.getTicketId());

        return "✅ Entry successful at station: " + stationId;
    }

    private String handleExitScan(Ticket ticket, String stationId) {
        if (!ticket.isEntryScanned()) {
            return "🚫 Ticket not scanned at entry.";
        }

        if (ticket.isUsed()) {
            return "⚠️ Ticket already used for exit.";
        }

        if (!ticket.getDestinationStation().equalsIgnoreCase(stationId)) {
            return "🚫 Invalid exit station. Expected: " + ticket.getDestinationStation();
        }

        ticket.setUsed(true);
        ticketJpaRepository.save(ticket);

        // ✅ Remove from Redis on exit
        stationRedisRepository.removeTicket(stationId, ticket.getTicketId());

        return "✅ Exit successful at station: " + stationId;
    }
}
