package com.metro.booking.service;

import com.metro.booking.model.Ticket;
import com.metro.booking.repository.StationRedisRepository;
import com.metro.booking.repository.TicketJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketJpaRepository ticketJpaRepository;
    private final StationRedisRepository stationRedisRepository;

    public Ticket bookTicket(String source, String destination) {
        // 1️⃣ Basic validation
        if (source.equalsIgnoreCase(destination)) {
            throw new IllegalArgumentException("Source and destination cannot be the same.");
        }

        String ticketId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(2);

        Ticket ticket = Ticket.builder()
                .ticketId(ticketId)
                .sourceStation(source)
                .destinationStation(destination)
                .bookingTime(now)
                .expiryTime(expiry)
                .entryScanned(false)
                .used(false)
                .build();

        // 2️⃣ Save to PostgreSQL
        ticketJpaRepository.save(ticket);

        // 3️⃣ Store active ticket for both stations in Redis
        Duration ttl = Duration.between(LocalDateTime.now(), expiry);
        stationRedisRepository.addActiveTicket(source, ticketId, ttl);
        stationRedisRepository.addActiveTicket(destination, ticketId, ttl);

        return ticket;
    }
}
