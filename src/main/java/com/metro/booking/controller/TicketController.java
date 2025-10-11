package com.metro.booking.controller;


import com.metro.booking.model.Ticket;
import com.metro.booking.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/book")
    public Ticket bookTicket(@RequestParam String source,
                             @RequestParam String destination) {
        return ticketService.bookTicket(source, destination);
    }
}
