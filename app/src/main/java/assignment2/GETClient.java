package assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GETClient {
    private String serverAddress;
    private int serverPort;
    private LamportClock clock;

    public GETClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.clock = new LamportClock();
    }

    public void sendGetRequest() {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            // Create output stream to send request
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Construct GET request headers
            out.println("GET /weather.json HTTP/1.1");
            out.println("User-Agent: GETClient/1.0");
            out.println();  // Empty line to separate headers and body (no body for GET)
            
            // Receive and display the server's response
            String serverResponse;
            StringBuilder jsonResponse = new StringBuilder();
            while ((serverResponse = in.readLine()) != null) {
                jsonResponse.append(serverResponse).append("\n");
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
        new GETClient(serverAddress, serverPort).sendGetRequest();
    }
}
