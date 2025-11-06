package com.metro.booking.service;

import com.azure.messaging.eventhubs.EventProcessorClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventHubCheckpointedConsumerService {

    @Autowired
    private EventProcessorClient eventProcessorClient;

    @PostConstruct
    public void startConsumer() {
        eventProcessorClient.start();
        System.out.println("Event Processor Client started for consuming and checkpointing events.");
    }
}
