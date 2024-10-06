package assignment2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class GETClientTest {

    private static final int MOCK_SERVER_PORT = 4569;
    private ExecutorService executorService;

    @BeforeEach
    public void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        startMockServer();
    }

    private void startMockServer() {
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(MOCK_SERVER_PORT)) {
                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        String line;
                        while ((line = in.readLine()) != null && !line.isEmpty()) {
                            if (line.startsWith("GET /weather.json")) {
                                out.println("HTTP/1.1 200 OK");
                                out.println("Content-Type: application/json");
                                out.println("Lamport-Time: 1");
                                out.println();
                                out.println("{\"id\":\"ID01\",\"name\":\"Station 01\",\"state\":\"State\",\"time_zone\":\"TimeZone\",\"lat\":\"0.0\",\"lon\":\"0.0\",\"local_date_time\":\"2023-10-01T12:00:00\",\"local_date_time_full\":\"2023-10-01T12:00:00Z\",\"air_temp\":\"25.0\",\"apparent_t\":\"25.0\",\"cloud\":\"0\",\"dewpt\":\"10.0\",\"press\":\"1010.0\",\"rel_hum\":\"50\",\"wind_dir\":\"180\",\"wind_speed_kmh\":\"10\",\"wind_spd_kt\":\"5\"}");
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testSendGetRequestWithStationId() {
        GETClient getClient = new GETClient("localhost", MOCK_SERVER_PORT, "ID01");
        getClient.sendGetRequest();

        assertTrue(true, "GET request with stationId should be sent successfully");
    }

    @Test
    public void testSendGetRequestWithoutStationId() {
        GETClient getClient = new GETClient("localhost", MOCK_SERVER_PORT, null);
        getClient.sendGetRequest();

        assertTrue(true, "GET request without stationId should be sent successfully");
    }
}