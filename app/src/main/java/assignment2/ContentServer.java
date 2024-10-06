package assignment2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ContentServer {
    private String serverAddress;
    private int serverPort;
    private String stationId;
    private LamportClock clock;

    public ContentServer(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.stationId = "ID" + String.format("%02d", (int) (Math.random() * 21));
        this.clock = new LamportClock();
    }

    public void sendPutRequest() {
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Increment Lamport clock before sending the request
            clock.increment();
            int currentTime = clock.getTime();
            
            // Read weather data from the file and create WeatherData object
            WeatherData weatherData = readWeatherDataFromFile();

            // Serialize the weather data to JSON
            String weatherDataJson = weatherData.toJson();
            int contentLength = weatherDataJson.length();

            // Construct PUT request headers
            out.println("PUT /weather.json HTTP/1.1");
            out.println("User-Agent: ATOMClient/1.0");
            out.println("Content-Type: application/json");
            out.println("Content-Length: " + contentLength);
            out.println("Lamport-Time: " + currentTime); // Send Lamport time
            out.println();  // Empty line to separate headers and body
            out.println(weatherDataJson);  // Body: JSON data

            // Receive and display server response
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
                if (response.startsWith("Lamport-Time:")) {
                    int receivedTime = Integer.parseInt(response.split(":")[1].trim());
                    clock.update(receivedTime); // Update Lamport clock based on server response
                }
            }

        } catch (IOException e) {
            System.err.println("Error: Unable to send PUT request.");
            e.printStackTrace();
        }
    }
    
    private WeatherData readWeatherDataFromFile() throws IOException {
        String directoryPath = "src/main/resources/" + stationId;
        String filePath = directoryPath + "/weather.txt";

        // Create directory if it doesn't exist
        java.nio.file.Path path = java.nio.file.Paths.get(directoryPath);
        if (!java.nio.file.Files.exists(path)) {
            java.nio.file.Files.createDirectories(path);

            // Populate weather.txt with random data
            try (PrintWriter writer = new PrintWriter(filePath)) {
            writer.println("id: " + stationId);
            writer.println("name: Station " + stationId);
            writer.println("state: RandomState");
            writer.println("time_zone: RandomTimeZone");
            writer.println("lat: " + (Math.random() * 180 - 90));
            writer.println("lon: " + (Math.random() * 360 - 180));
            writer.println("local_date_time: 2023-10-01T12:00:00");
            writer.println("local_date_time_full: 2023-10-01T12:00:00Z");
            writer.println("air_temp: " + (Math.random() * 40 - 10));
            writer.println("apparent_t: " + (Math.random() * 40 - 10));
            writer.println("cloud: " + (Math.random() * 100));
            writer.println("dewpt: " + (Math.random() * 30));
            writer.println("press: " + (Math.random() * 50 + 950));
            writer.println("rel_hum: " + (Math.random() * 100));
            writer.println("wind_dir: " + (Math.random() * 360));
            writer.println("wind_speed_kmh: " + (Math.random() * 100));
            writer.println("wind_spd_kt: " + (Math.random() * 54));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String id = null, name = null, state = null, time_zone = null, lat = null, lon = null;
            String local_date_time = null, local_date_time_full = null, air_temp = null, apparent_t = null;
            String cloud = null, dewpt = null, press = null, rel_hum = null, wind_dir = null;
            String wind_speed_kmh = null, wind_spd_kt = null;
    
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length < 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();
    
                switch (key) {
                    case "id":
                        id = value;
                        break;
                    case "name":
                        name = value;
                        break;
                    case "state":
                        state = value;
                        break;
                    case "time_zone":
                        time_zone = value;
                        break;
                    case "lat":
                        lat = value;
                        break;
                    case "lon":
                        lon = value;
                        break;
                    case "local_date_time":
                        local_date_time = value;
                        break;
                    case "local_date_time_full":
                        local_date_time_full = value;
                        break;
                    case "air_temp":
                        air_temp = value;
                        break;
                    case "apparent_t":
                        apparent_t = value;
                        break;
                    case "cloud":
                        cloud = value;
                        break;
                    case "dewpt":
                        dewpt = value;
                        break;
                    case "press":
                        press = value;
                        break;
                    case "rel_hum":
                        rel_hum = value;
                        break;
                    case "wind_dir":
                        wind_dir = value;
                        break;
                    case "wind_speed_kmh":
                        wind_speed_kmh = value;
                        break;
                    case "wind_spd_kt":
                        wind_spd_kt = value;
                        break;
                }
            }
    
            return new WeatherData(id, name, state, time_zone, lat, lon, local_date_time, local_date_time_full, air_temp, apparent_t, cloud, dewpt, press, rel_hum, wind_dir, wind_speed_kmh, wind_spd_kt);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String serverAddress = args[0]; // e.g., "localhost"
        int serverPort = Integer.parseInt(args[1]); // e.g., 4567

        ContentServer contentServer = new ContentServer(serverAddress, serverPort);
        contentServer.sendPutRequest();
    }
}
