package com.metro.booking.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AzureEventHubConfig {

    @Value("${azure.eventhubs.connection-string:}")
    private String connectionString;

    @Value("${azure.eventhubs.namespace:}")
    private String namespace; // e.g., your-namespace.servicebus.windows.net

    @Value("${azure.eventhubs.event-hub-name}")
    private String eventHubName;

    @Bean
    @Profile("local")
    public EventHubProducerClient eventHubProducerClientLocal() {
        return new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }

    @Bean
    @Profile({"dev", "qa", "prod"})
    public EventHubProducerClient eventHubProducerClientManagedIdentity() {
        return new EventHubClientBuilder()
                .fullyQualifiedNamespace(namespace)
                .eventHubName(eventHubName)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildProducerClient();
    }
}
