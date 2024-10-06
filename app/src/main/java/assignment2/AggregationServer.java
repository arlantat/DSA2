package assignment2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonSyntaxException;

public class AggregationServer {
    private int port;
    private Map<String, WeatherData> weatherDataStore; // Key: Weather station ID, Value: WeatherData object
    private Map<String, Long> lastCommunicationTime; // Key: Weather station ID, Value: Last communication time in milliseconds
    private LamportClock clock;
    private static final long EXPIRATION_TIME = 30000; // 30 seconds
    private static final int CACHE_SIZE = 20; // LRU Cache size
    
    public AggregationServer(int port) {
        this.port = port;
        this.weatherDataStore = new LinkedHashMap<String, WeatherData>(CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, WeatherData> eldest) {
                return size() > CACHE_SIZE;
            }
        };
        this.lastCommunicationTime = new ConcurrentHashMap<>();
        this.clock = new LamportClock();
    }

    public void start() {
        // Starts the server on the given port and listens for incoming requests (PUT/GET).
        // Initialize necessary handlers and threads for client communication.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("AggregationServer started on port: " + port);

            // Schedule a task to remove expired data every 5 seconds
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(this::removeExpiredData, 5, 5, TimeUnit.SECONDS);

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
                if (requestLine == null || requestLine.isEmpty()) {
                    out.println("HTTP/1.1 204");
                    out.println();
                    return;
                }

                if (requestLine.startsWith("PUT")) {
                    // Parse headers
                    String headerLine;
                    int contentLength = 0;
                    int receivedTime = 0;
                    while (!(headerLine = in.readLine()).isEmpty()) {
                        if (headerLine.startsWith("Content-Length:")) {
                            contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                        } else if (headerLine.startsWith("Lamport-Time:")) {
                            receivedTime = Integer.parseInt(headerLine.split(":")[1].trim());
                        }
                    }

                    // Update Lamport clock based on received time
                    clock.update(receivedTime);

                    // Read the body (weather data in JSON format)
                    char[] bodyChars = new char[contentLength];
                    in.read(bodyChars, 0, contentLength);
                    String body = new String(bodyChars);

                    // Parse the weather data and handle the PUT request
                    handlePutRequest(body, out);

                } else if (requestLine.startsWith("GET")) {
                    // Parse headers
                    String headerLine;
                    int receivedTime = 0;
                    while (!(headerLine = in.readLine()).isEmpty()) {
                        if (headerLine.startsWith("Lamport-Time:")) {
                            receivedTime = Integer.parseInt(headerLine.split(":")[1].trim());
                        }
                    }

                    // Update Lamport clock based on received time
                    clock.update(receivedTime);
                    
                    // Handle GET request
                    handleGetRequest(out, requestLine);
                } else {
                    out.println("HTTP/1.1 400");
                    out.println();
                }

            } catch (IOException e) {
                System.err.println("Error: Unable to communicate with client.");
                e.printStackTrace();
            }
        }
    }

    synchronized void handlePutRequest(String jsonBody, PrintWriter out) {
        if (jsonBody == null || jsonBody.isEmpty()) {
            out.println("HTTP/1.1 204");
            out.println();
            return;
        }

        WeatherData weatherData;
        try {
            weatherData = WeatherJSONTransformer.fromJson(jsonBody);
        } catch (JsonSyntaxException e) {
            out.println("HTTP/1.1 500");
            out.println();
            return;
        }

        boolean isNewEntry = !weatherDataStore.containsKey(weatherData.getId());

        // Store the weather data in the map using station ID as key
        weatherDataStore.put(weatherData.getId(), weatherData);

        // Update the last communication time for the station ID
        lastCommunicationTime.put(weatherData.getId(), System.currentTimeMillis());

        // Increment Lamport clock before sending the response
        clock.increment();
        int currentTime = clock.getTime();

        // Respond with success message
        if (isNewEntry) {
            out.println("HTTP/1.1 201 Created");
        } else {
            out.println("HTTP/1.1 200 OK");
        }
        out.println("Content-Type: text/plain");
        out.println("Lamport-Time: " + currentTime); // Send Lamport time
        out.println();
    }

    synchronized void handleGetRequest(PrintWriter out, String requestLine) throws IOException {
        String queriedStationId = null;
    
        // Parse the request line to extract the stationId if present
        if (requestLine.contains("?stationId=")) {
            String[] parts = requestLine.split("\\?");
            if (parts.length > 1) {
                String[] queryParams = parts[1].split("=");
                if (queryParams.length > 1 && "stationId".equals(queryParams[0])) {
                    queriedStationId = queryParams[1];
                }
            }
        }
    
        // Fetch the weather data based on the stationId
        String jsonResponse;
        if (queriedStationId != null && weatherDataStore.containsKey(queriedStationId)) {
            jsonResponse = WeatherJSONTransformer.toJson(weatherDataStore.get(queriedStationId));
            // Update the last communication time for the station ID
            lastCommunicationTime.put(queriedStationId, System.currentTimeMillis());
        } else {
            WeatherData mostRecentData = null;
            for (WeatherData data : weatherDataStore.values()) {
                if (mostRecentData == null || lastCommunicationTime.get(data.getId()) > lastCommunicationTime.get(mostRecentData.getId())) {
                    mostRecentData = data;
                }
            }
            if (mostRecentData == null) {
                jsonResponse = "{}"; // No data available
            } else {
                jsonResponse = WeatherJSONTransformer.toJson(mostRecentData);
                // Update the last communication time for the station ID
                lastCommunicationTime.put(mostRecentData.getId(), System.currentTimeMillis());
            }
        }

        // Increment Lamport clock before sending the response
        clock.increment();
        int currentTime = clock.getTime();
    
        // Respond with the JSON data
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Lamport-Time: " + currentTime); // Send Lamport time
        out.println();
        out.println(jsonResponse);
    }

    private void removeExpiredData() {
        // Removes weather data from content servers that haven't communicated for over 30 seconds.
        System.out.println("Checking for expired data...");
        long currentTime = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : lastCommunicationTime.entrySet()) {
            if (currentTime - entry.getValue() > EXPIRATION_TIME) {
                String stationId = entry.getKey();
                weatherDataStore.remove(stationId);
                lastCommunicationTime.remove(stationId);
                System.out.println("Removed expired data for station ID: " + stationId);
            }
        }
    }

    private void recoverFromCrash() {
        // Recovers from server crashes by restoring the last known good state from the file.
    }
    
    public static void main(String[] args) {
        // Entry point to start the server with a default or provided port.
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4567;
        new AggregationServer(port).start();
    }

    public Map<String, WeatherData> getWeatherDataStore() {
        return weatherDataStore;
    }
}
