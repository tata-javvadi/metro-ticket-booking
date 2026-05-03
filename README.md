# Metro Ticket Booking

A Spring Boot application for booking metro tickets and validating them at station entry and exit points.

The core flow uses PostgreSQL to persist tickets and Redis to keep fast, station-wise lookups of active tickets.

## Features

- Book a ticket between a source and destination station
- Generate a unique ticket ID for each booking
- Set a fixed 2-hour validity window per ticket
- Cache active tickets in Redis for quick validation
- Validate `ENTRY` and `EXIT` scans
- Prevent invalid usage such as:
  - same source and destination
  - duplicate entry scans
  - exit before entry
  - exit at the wrong destination
  - expired or inactive tickets

## Tech Stack

- Java 17
- Spring Boot 3.4
- Spring Web
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Redis
- Maven
- Lombok

## Core Modules

```text
src/main/java/com/metro/booking
|-- config
|   |-- RedisConfig.java
|   |-- AzureEventHubConfig.java
|   |-- EventHubConsumerCheckpointConfig.java
|-- controller
|   |-- TicketController.java
|   |-- ValidationController.java
|-- model
|   |-- Ticket.java
|-- registry
|   |-- StationRegistry.java
|-- repository
|   |-- TicketJpaRepository.java
|   |-- StationRedisRepository.java
|-- service
|   |-- TicketService.java
|   |-- ValidationService.java
|   |-- EventHubProducerService.java
|   |-- EventHubCheckpointedConsumerService.java
|-- MetroTicketBookingApplication.java
```

Note: this README focuses on the metro booking and validation workflow. AWS S3 and Azure Blob related code is intentionally not documented here.

## Booking Flow

When a ticket is booked:

1. The service checks that source and destination are different.
2. A UUID-based `ticketId` is created.
3. A `Ticket` entity is stored in PostgreSQL.
4. The ticket ID is added to Redis sets for both the source and destination stations.
5. The Redis entries are given a TTL that matches the ticket expiry time.

## Validation Flow

When a ticket is scanned:

1. Redis is checked first to confirm the ticket is active for that station.
2. PostgreSQL is queried to load the full ticket.
3. Expiry is checked.
4. For `ENTRY`:
   - the station must match the ticket source
   - the ticket is marked as entered
   - the source-station Redis entry is removed
5. For `EXIT`:
   - entry must already be completed
   - the station must match the ticket destination
   - the ticket is marked as used
   - the destination-station Redis entry is removed

## REST API

### Book a ticket

`POST /api/ticket/book`

Query parameters:

- `source`
- `destination`

Example:

```bash
curl -X POST "http://localhost:8080/api/ticket/book?source=ST01&destination=ST11"
```

Sample response:

```json
{
  "ticketId": "0d3dc853-89fd-4cc0-b98e-0d8bfe3dc5a4",
  "sourceStation": "ST01",
  "destinationStation": "ST11",
  "bookingTime": "2026-05-03T16:00:00",
  "expiryTime": "2026-05-03T18:00:00",
  "entryScanned": false,
  "used": false
}
```

### Validate a scan

`GET /api/validation/scan`

Query parameters:

- `ticketId`
- `stationId`
- `scanType` with value `ENTRY` or `EXIT`

Example entry scan:

```bash
curl "http://localhost:8080/api/validation/scan?ticketId=0d3dc853-89fd-4cc0-b98e-0d8bfe3dc5a4&stationId=ST01&scanType=ENTRY"
```

Example exit scan:

```bash
curl "http://localhost:8080/api/validation/scan?ticketId=0d3dc853-89fd-4cc0-b98e-0d8bfe3dc5a4&stationId=ST11&scanType=EXIT"
```

Possible responses:

- `Entry successful at station: ST01`
- `Exit successful at station: ST11`
- `Ticket not active at this station or expired.`
- `Ticket not scanned at entry.`
- `Ticket already used for exit.`
- `Invalid scan type. Use 'ENTRY' or 'EXIT'.`

## Data Model

### Ticket

The `Ticket` entity includes:

- `ticketId`
- `sourceStation`
- `destinationStation`
- `bookingTime`
- `expiryTime`
- `entryScanned`
- `used`

### Redis Key Pattern

Active ticket IDs are stored per station using:

```text
station:<stationId>
```

Each key holds a Redis Set of ticket IDs that are currently valid for that station.

## Station Registry

`StationRegistry` contains a sample in-memory list of Hyderabad Metro station codes and names across the Red, Blue, and Green lines. It can be used as reference data for validating station inputs in future enhancements.

## Configuration

The application reads its settings from [application.properties](/C:/Users/Karthik/IdeaProjects/metro-ticket-booking/src/main/resources/application.properties).

Core configuration used by the booking flow:

```properties
spring.application.name=metro-ticket-service
server.port=8080
spring.profiles.active=local

spring.data.redis.host=...
spring.data.redis.port=...
spring.data.redis.username=...
spring.data.redis.password=...

spring.datasource.url=jdbc:postgresql://...
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

Useful environment variables:

- `DB_USERNAME`
- `DB_PASSWORD`
- `REDIS_USERNAME`
- `REDIS_PASSWORD`

## Running Locally

Prerequisites:

- JDK 17
- Maven or the included Maven wrapper
- A reachable PostgreSQL instance
- A reachable Redis instance

Start on Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Start on macOS/Linux:

```bash
./mvnw spring-boot:run
```

Build the project:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test
```

## Additional Notes

- The default profile is `local`.
- Ticket expiry is currently fixed to 2 hours from booking time.
- The validation flow depends on both PostgreSQL and Redis being available.
- The project also contains Azure Event Hubs related classes, but they are separate from the core ticket booking REST flow described in this README.

## Suggested Next Improvements

- Add request and response DTOs
- Validate station codes using `StationRegistry`
- Add centralized exception handling with proper HTTP status codes
- Add controller and integration tests for booking and validation
- Add fare calculation rules
- Add authentication and ownership checks for tickets
