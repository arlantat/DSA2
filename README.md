### README.md

# Weather Aggregation System

This project is a Weather Aggregation System that consists of three main components:
1. **AggregationServer** (AS): Aggregates weather data from multiple content servers and receive queries from so-called GET-clients.
2. **ContentServer** (CS): Sends weather data to the AggregationServer.
3. **GETClient** (GC): Fetches weather data from the AggregationServer.

## Considerations
1. Each time GETClient.java is run, it's treated as one GET request and immediately terminate when AS gives a thumbs up. The same for CS - the main way to renew is to run the CS multiple times and not a periodical design. The only thing listening infinitely is AS.
2. Instead of providing a filePath for every CS, random ID from 0 to 20 is chosen. If the system doesn't detect an associated file entry, it attempts to create a new one.
3. Instead of providing URL comprising of name + port, I separated these two.
4. Every 5 seconds, AS checks if a weather entry should be expired, so it wouldn't be exactly 30 seconds since the last PUT/GET request. This concerns Lamport Clock implementation.
5. Used LRU Cache to implement limit 20, but use another map to implement 30 seconds - operation involving the latter scales linearly with entries.
6. Used custom JSONTransformer. Gson is just there to catch exceptions. 

## Prerequisites

- Java 21
- Gradle 8.8 or higher

## Setup

**Build the project:**
```sh
gradle build
```

## Running the Servers and Clients

### AggregationServer

The AggregationServer aggregates weather data from multiple content servers. It runs on a specified port (default: 4567).

**To run the AggregationServer:**
```sh
gradle runAggregationServer
```

### ContentServer

The ContentServer sends weather data to the AggregationServer. It requires the AggregationServer's address and port.

**To run the ContentServer:**
```sh
gradle runContentServer
```

### GETClient

The GETClient fetches weather data from the AggregationServer. It requires the AggregationServer's address and port.

**To run the GETClient:**
```sh
// This takes the latest WeatherData by default, but you can specify stationId using custom means gradle task is here to simplify things. 
gradle runGETClient
```

## Project Structure

- **src/main/java/assignment2/**
  - **AggregationServer.java**: Aggregates weather data from multiple content servers.
  - **ContentServer.java**: Sends weather data to the AggregationServer.
  - **GETClient.java**: Fetches weather data from the AggregationServer.
  - **LamportClock.java**: Implements the Lamport clock for logical time synchronization.
  - **WeatherData.java**: Represents the weather data model.

- **src/main/resources/**: Contains weather data files for the ContentServer.

## Configuration

### 

build.gradle



The 

build.gradle

 file includes tasks to run the AggregationServer, ContentServer, and GETClient:

```gradle
task runAggregationServer(type: JavaExec) {
    main = 'assignment2.AggregationServer'
    args = ['4567']
    classpath = sourceSets.main.runtimeClasspath
}

task runContentServer(type: JavaExec) {
    main = 'assignment2.ContentServer'
    args = ['localhost', '4567', 'weather.json']
    classpath = sourceSets.main.runtimeClasspath
}

task runGETClient(type: JavaExec) {
    main = 'assignment2.GETClient'
    args = ['localhost', '4567']
    classpath = sourceSets.main.runtimeClasspath
}
```

## Testing

To run the unit tests, use the following command:
```sh
gradle test
```
