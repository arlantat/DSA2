package assignment2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class IntegrationTest {

    private static final int SERVER_PORT = 4567;
    private ExecutorService executorService;
    private AggregationServer aggregationServer;
    private static final Logger logger = Logger.getLogger(IntegrationTest.class.getName());

    @BeforeEach
    public void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        aggregationServer = new AggregationServer(SERVER_PORT);
        startAggregationServer();
    }

    private void startAggregationServer() {
        executorService.submit(() -> {
            aggregationServer.start();
        });
    }

    @Test
    public void testContentServerAndGetClientInteraction() throws InterruptedException {
        // Start ContentServer and send PUT request
        ContentServer contentServer = new ContentServer("localhost", SERVER_PORT);
        contentServer.sendPutRequest();

        // Wait for the server to process the request
        Thread.sleep(1000);

        // Start GETClient and send GET request
        GETClient getClient = new GETClient("localhost", SERVER_PORT, contentServer.getStationId());
        getClient.sendGetRequest();

        // Wait for the server to process the request
        Thread.sleep(1000);

        // Verify that the data was stored and retrieved correctly
        assertTrue(aggregationServer.getWeatherDataStore().containsKey(contentServer.getStationId()), "WeatherData should be stored in the map");
    }

    @Test
    public void testMultipleContentServersAndGetClientInteraction() throws InterruptedException {
        // Start multiple ContentServers and send PUT requests
        ContentServer contentServer1 = new ContentServer("localhost", SERVER_PORT);
        contentServer1.sendPutRequest();
        logger.info("ContentServer1 sent PUT request");

        ContentServer contentServer2 = new ContentServer("localhost", SERVER_PORT);
        contentServer2.sendPutRequest();
        logger.info("ContentServer2 sent PUT request");

        // Wait for the server to process the requests
        Thread.sleep(2000);

        // Start GETClient and send GET request for the first ContentServer
        GETClient getClient1 = new GETClient("localhost", SERVER_PORT, contentServer1.getStationId());
        getClient1.sendGetRequest();
        logger.info("GETClient1 sent GET request");

        // Start GETClient and send GET request for the second ContentServer
        GETClient getClient2 = new GETClient("localhost", SERVER_PORT, contentServer2.getStationId());
        getClient2.sendGetRequest();
        logger.info("GETClient2 sent GET request");

        // Wait for the server to process the requests
        Thread.sleep(2000);

        // Verify that the data was stored and retrieved correctly
        boolean containsKey1 = aggregationServer.getWeatherDataStore().containsKey(contentServer1.getStationId());
        boolean containsKey2 = aggregationServer.getWeatherDataStore().containsKey(contentServer2.getStationId());
        logger.info("WeatherDataStore contains key for ContentServer1: " + containsKey1);
        logger.info("WeatherDataStore contains key for ContentServer2: " + containsKey2);
        assertTrue(containsKey1, "WeatherData for ContentServer1 should be stored in the map");
        assertTrue(containsKey2, "WeatherData for ContentServer2 should be stored in the map");
    }
}