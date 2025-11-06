package com.metro.booking.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventHubProducerService {

    @Autowired
    private EventHubProducerClient producerClient;

    public void sendUserOrder(String userId, String orderPayload) {
        CreateBatchOptions options = new CreateBatchOptions().setPartitionKey(userId);
        EventDataBatch batch = producerClient.createBatch(options);
        batch.tryAdd(new EventData(orderPayload));
        producerClient.send(batch);
    }
}
