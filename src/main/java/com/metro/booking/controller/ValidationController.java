package com.metro.booking.controller;

import com.metro.booking.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
public class ValidationController {

    private final ValidationService validationService;

    /**
     * Validate ticket scan at a station
     * @param ticketId Ticket ID from frontend QR scan
     * @param stationId Station where user is scanning
     * @param scanType "ENTRY" or "EXIT"
     * @return validation message
     */
    @GetMapping("/scan")
    public String validateTicket(
            @RequestParam String ticketId,
            @RequestParam String stationId,
            @RequestParam String scanType) {

        return validationService.validateTicket(ticketId, stationId, scanType);
    }
}
