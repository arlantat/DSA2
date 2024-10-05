package assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

public class AggregationServer {
    private int port;
    private Map<String, WeatherData> weatherDataStore; // Key: Weather station ID, Value: WeatherData object
    private LamportClock clock;
    
    public AggregationServer(int port) {
        this.port = port;
        this.weatherDataStore = new ConcurrentHashMap<>();
        this.clock = new LamportClock();
    }

    public void start() {
        // Starts the server on the given port and listens for incoming requests (PUT/GET).
        // Initialize necessary handlers and threads for client communication.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("AggregationServer started on port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start(); // Handle each client in a new thread
            }
        } catch (IOException e) {
            System.err.println("Error: Unable to start server.");
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                // Read the request line
                String requestLine = in.readLine();
                if (requestLine.startsWith("PUT")) {
                    // Parse headers
                    String headerLine;
                    int contentLength = 0;
                    while (!(headerLine = in.readLine()).isEmpty()) {
                        if (headerLine.startsWith("Content-Length:")) {
                            contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                        }
                    }

                    // Read the body (weather data in JSON format)
                    char[] bodyChars = new char[contentLength];
                    in.read(bodyChars, 0, contentLength);
                    String body = new String(bodyChars);

                    // Parse the weather data and handle the PUT request
                    handlePutRequest(body, out);

                } else if (requestLine.startsWith("GET")) {
                    // Handle GET request
                    handleGetRequest(out);
                }

            } catch (IOException e) {
                System.err.println("Error: Unable to communicate with client.");
                e.printStackTrace();
            }
        }
    }

    private String fetchWeatherData() {
        Gson gson = new Gson();
        return gson.toJson(weatherDataStore); // Convert weather data map to JSON string
    }

    private synchronized void handlePutRequest(String jsonBody, PrintWriter out) {
        Gson gson = new Gson();
        WeatherData weatherData = gson.fromJson(jsonBody, WeatherData.class);
        System.out.println(weatherData.toString());

        // Store the weather data in the map using station ID as key
        weatherDataStore.put(weatherData.getId(), weatherData);

        // Respond with success message
        out.println("HTTP/1.1 201 Created");
        out.println("Content-Type: text/plain");
        out.println();
        out.println("PUT request successful. Data stored for station: " + weatherData.getId());
    }

    private synchronized void handleGetRequest(PrintWriter out) {
        // Convert stored weather data to JSON and send as response
        String jsonResponse = fetchWeatherData();

        // Respond with the JSON data
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println(jsonResponse);
    }

    private void removeExpiredData() {
        // Removes weather data from content servers that haven't communicated for over 30 seconds.
    }

    private void recoverFromCrash() {
        // Recovers from server crashes by restoring the last known good state from the file.
    }
    
    public static void main(String[] args) {
        // Entry point to start the server with a default or provided port.
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4567;
        new AggregationServer(port).start();
    }
}
