package assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GETClient {
    private String serverAddress;
    private int serverPort;
    private String stationId;  // implemented so one GETClient = one request = one stationId
    private LamportClock clock;

    public GETClient(String serverAddress, int serverPort, String stationId) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.stationId = stationId;
        this.clock = new LamportClock();
    }

    public void sendGetRequest() {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            // Create output stream to send request
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Increment Lamport clock before sending the request
            clock.increment();
            int currentTime = clock.getTime();
            
            // Construct GET request headers
            String requestPath = "/weather.json";
            if (stationId != null && !stationId.isEmpty()) {
                requestPath += "?stationId=" + stationId;
            }
            out.println("GET " + requestPath + " HTTP/1.1");
            out.println("User-Agent: GETClient/1.0");
            out.println("Lamport-Time: " + currentTime); // Send Lamport time
            out.println();  // Empty line to separate headers and body (no body for GET)
            
            // Receive and display the server's response
            String serverResponse;
            StringBuilder jsonResponse = new StringBuilder();
            while ((serverResponse = in.readLine()) != null) {
                jsonResponse.append(serverResponse).append("\n");
                if (serverResponse.startsWith("Lamport-Time:")) {
                    int receivedTime = Integer.parseInt(serverResponse.split(":")[1].trim());
                    clock.update(receivedTime); // Update Lamport clock based on server response
                }
            }
            
            // Display the response from the server (JSON formatted weather data)
            displayWeatherData(jsonResponse.toString());
        } catch (IOException e) {
            System.err.println("Error: Unable to connect to server or retrieve data.");
            e.printStackTrace();
        }
    }

    private void displayWeatherData(String jsonData) {
        System.out.println(jsonData);
    }

    public static void main(String[] args) {
        String serverAddress = args[0]; // e.g., "localhost"
        int serverPort = Integer.parseInt(args[1]); // e.g., 4567
        String stationId = args.length > 2 ? args[2] : null; // Optional stationId
        
        new GETClient(serverAddress, serverPort, stationId).sendGetRequest();
    }
}
