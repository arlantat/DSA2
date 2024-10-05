package assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ContentServer {
    private String serverAddress;
    private int serverPort;
    private String filePath;
    private LamportClock clock;

    public ContentServer(String serverAddress, int serverPort, String filePath) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.filePath = filePath;
        this.clock = new LamportClock();
    }

    public void sendPutRequest() {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read weather data from the file and create WeatherData object
            WeatherData weatherData = readWeatherDataFromFile();

            // Create a PutRequest with the current Lamport time
            PutRequest putRequest = new PutRequest(weatherData, clock.getTime());

            // Send PUT request as JSON
            out.println(putRequest.toJson());

            // Receive and display server response
            String response = in.readLine();
            System.out.println("Server response: " + response);

        } catch (IOException e) {
            System.err.println("Error: Unable to send PUT request.");
            e.printStackTrace();
        }
    }

    private WeatherData readWeatherDataFromFile() {
        // Dummy implementation for file reading
        // Replace this with actual file reading logic
        return new WeatherData("ID12345", "Sample Location", "State", "PST", -34.9, 138.6, 
                               "15/04:00pm", "20230715160000", 13.3, 9.5, "Cloudy", 
                               5.7, 1023.9, 60, "S", 15, 8);
    }

    public static void main(String[] args) {
        String serverAddress = args[0]; // e.g., "localhost"
        int serverPort = Integer.parseInt(args[1]); // e.g., 4567
        String filePath = args[2]; // e.g., "weather.txt"

        ContentServer contentServer = new ContentServer(serverAddress, serverPort, filePath);
        contentServer.sendPutRequest();
    }
}
