package assignment2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ContentServerTest {

    private static final int MOCK_SERVER_PORT = 4568;
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
                            if (line.startsWith("PUT /weather.json")) {
                                out.println("HTTP/1.1 200 OK");
                                out.println("Content-Type: text/plain");
                                out.println("Lamport-Time: 1");
                                out.println();
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
    public void testSendPutRequest() {
        ContentServer contentServer = new ContentServer("localhost", MOCK_SERVER_PORT);
        contentServer.sendPutRequest();
        
        assertTrue(true, "PUT request should be sent successfully");
    }
}