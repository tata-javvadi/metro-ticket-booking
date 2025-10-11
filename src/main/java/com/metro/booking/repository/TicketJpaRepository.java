package com.metro.booking.repository;

import com.metro.booking.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository extends JpaRepository<Ticket, String> {

    // You can add custom queries later if needed, e.g.:
    // List<Ticket> findBySourceStation(String sourceStation);
    // List<Ticket> findByUsedFalseAndExpiryTimeAfter(LocalDateTime now);
}
