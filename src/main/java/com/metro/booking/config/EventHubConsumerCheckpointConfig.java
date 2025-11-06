package com.metro.booking.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EventHubConsumerCheckpointConfig {

    @Value("${azure.eventhubs.connection-string:}")
    private String eventHubConnectionString;

    @Value("${azure.eventhubs.namespace:}")
    private String namespace;

    @Value("${azure.eventhubs.event-hub-name}")
    private String eventHubName;

    @Value("${azure.eventhubs.consumer-group:$Default}")
    private String consumerGroup;

    @Value("${azure.blob.checkpoint.connection-string:}")
    private String checkpointBlobConnectionString;

    @Value("${azure.blob.checkpoint.container-name}")
    private String checkpointBlobContainer;

    // Local: use connection string for Event Hub & Blob Storage
    @Bean
    @Profile("local")
    public EventProcessorClient eventProcessorClientLocal() {
        BlobContainerAsyncClient blobContainerAsyncClient = new BlobContainerClientBuilder()
                .connectionString(checkpointBlobConnectionString)
                .containerName(checkpointBlobContainer)
                .buildAsyncClient();

        return new EventProcessorClientBuilder()
                .connectionString(eventHubConnectionString, eventHubName)
                .consumerGroup(consumerGroup)
                .processEvent(eventContext -> {
                    String eventBody = eventContext.getEventData().getBodyAsString();
                    System.out.println("Received event: " + eventBody);
                    eventContext.updateCheckpoint();
                })
                .processError(errorContext -> {
                    System.err.println("Error: " + errorContext.getThrowable());
                })
                .checkpointStore(new BlobCheckpointStore(blobContainerAsyncClient))
                .buildEventProcessorClient();
    }


    @Bean
    @Profile({"dev", "qa", "prod"})
    public EventProcessorClient eventProcessorClientManagedIdentity() {
        // Compute the full blob storage endpoint
        String blobEndpoint = "https://" + namespace.replace(".servicebus.windows.net", "") + ".blob.core.windows.net";
        BlobContainerAsyncClient blobContainerAsyncClient = new BlobContainerClientBuilder()
                .endpoint(blobEndpoint)
                .containerName(checkpointBlobContainer)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildAsyncClient();

        return new EventProcessorClientBuilder()
                .fullyQualifiedNamespace(namespace)
                .eventHubName(eventHubName)
                .consumerGroup(consumerGroup)
                .credential(new DefaultAzureCredentialBuilder().build())
                .processEvent(eventContext -> {
                    String eventBody = eventContext.getEventData().getBodyAsString();
                    System.out.println("Received event: " + eventBody);
                    eventContext.updateCheckpoint();
                })
                .processError(errorContext -> {
                    System.err.println("Error: " + errorContext.getThrowable());
                })
                .checkpointStore(new BlobCheckpointStore(blobContainerAsyncClient))
                .buildEventProcessorClient();
    }
}
