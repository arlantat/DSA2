package assignment2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class AggregationServerTest {

    private AggregationServer server;

    @BeforeEach
    public void setUp() {
        server = new AggregationServer(4567);
    }

    @Test
    public void testServerInitialization() {
        assertNotNull(server, "Server should be initialized");
    }

    @Test
    public void testHandlePutRequest() {
        StringWriter responseWriter = new StringWriter();
        PrintWriter out = new PrintWriter(responseWriter);

        String jsonBody = "{\"id\":\"ID01\",\"name\":\"Station 01\",\"state\":\"State\",\"time_zone\":\"TimeZone\",\"lat\":\"0.0\",\"lon\":\"0.0\",\"local_date_time\":\"2023-10-01T12:00:00\",\"local_date_time_full\":\"2023-10-01T12:00:00Z\",\"air_temp\":\"25.0\",\"apparent_t\":\"25.0\",\"cloud\":\"0\",\"dewpt\":\"10.0\",\"press\":\"1010.0\",\"rel_hum\":\"50\",\"wind_dir\":\"180\",\"wind_speed_kmh\":\"10\",\"wind_spd_kt\":\"5\"}";

        server.handlePutRequest(jsonBody, out);

        Map<String, WeatherData> weatherDataStore = server.getWeatherDataStore();
        assertTrue(weatherDataStore.containsKey("ID01"), "WeatherData should be stored in the map");
    }

    @Test
    public void testHandleGetRequestWithStationId() throws Exception {
        StringWriter responseWriter = new StringWriter();
        PrintWriter out = new PrintWriter(responseWriter);

        // First, add some data to the server
        String jsonBody = "{\"id\":\"ID01\",\"name\":\"Station 01\",\"state\":\"State\",\"time_zone\":\"TimeZone\",\"lat\":\"0.0\",\"lon\":\"0.0\",\"local_date_time\":\"2023-10-01T12:00:00\",\"local_date_time_full\":\"2023-10-01T12:00:00Z\",\"air_temp\":\"25.0\",\"apparent_t\":\"25.0\",\"cloud\":\"0\",\"dewpt\":\"10.0\",\"press\":\"1010.0\",\"rel_hum\":\"50\",\"wind_dir\":\"180\",\"wind_speed_kmh\":\"10\",\"wind_spd_kt\":\"5\"}";
        server.handlePutRequest(jsonBody, out);

        // Now, test the GET request
        String requestLine = "GET /weather.json?stationId=ID01 HTTP/1.1";
        server.handleGetRequest(out, requestLine);

        String response = responseWriter.toString();
        assertTrue(response.contains("\"id\":\"ID01\""), "Response should contain the weather data for station ID01");
    }

    @Test
    public void testHandleGetRequestWithoutStationId() throws Exception {
        StringWriter responseWriter = new StringWriter();
        PrintWriter out = new PrintWriter(responseWriter);

        // First, add some data to the server
        String jsonBody1 = "{\"id\":\"ID01\",\"name\":\"Station 01\",\"state\":\"State\",\"time_zone\":\"TimeZone\",\"lat\":\"0.0\",\"lon\":\"0.0\",\"local_date_time\":\"2023-10-01T12:00:00\",\"local_date_time_full\":\"2023-10-01T12:00:00Z\",\"air_temp\":\"25.0\",\"apparent_t\":\"25.0\",\"cloud\":\"0\",\"dewpt\":\"10.0\",\"press\":\"1010.0\",\"rel_hum\":\"50\",\"wind_dir\":\"180\",\"wind_speed_kmh\":\"10\",\"wind_spd_kt\":\"5\"}";
        String jsonBody2 = "{\"id\":\"ID02\",\"name\":\"Station 02\",\"state\":\"State\",\"time_zone\":\"TimeZone\",\"lat\":\"0.0\",\"lon\":\"0.0\",\"local_date_time\":\"2023-10-01T12:00:00\",\"local_date_time_full\":\"2023-10-01T12:00:00Z\",\"air_temp\":\"25.0\",\"apparent_t\":\"25.0\",\"cloud\":\"0\",\"dewpt\":\"10.0\",\"press\":\"1010.0\",\"rel_hum\":\"50\",\"wind_dir\":\"180\",\"wind_speed_kmh\":\"10\",\"wind_spd_kt\":\"5\"}";
        server.handlePutRequest(jsonBody1, out);
        server.handlePutRequest(jsonBody2, out);

        // Now, test the GET request without stationId
        String requestLine = "GET /weather.json HTTP/1.1";
        server.handleGetRequest(out, requestLine);

        String response = responseWriter.toString();
        assertTrue(response.contains("\"id\":\"ID02\""), "Response should contain the most recently updated weather data");
    }

    @Test
    public void testLRUCacheEviction() {
        StringWriter responseWriter = new StringWriter();
        PrintWriter out = new PrintWriter(responseWriter);

        // Add 21 entries to the server to trigger LRU eviction
        for (int i = 1; i <= 21; i++) {
            String jsonBody = String.format("{\"id\":\"ID%02d\",\"name\":\"Station %02d\",\"state\":\"State\",\"time_zone\":\"TimeZone\",\"lat\":\"0.0\",\"lon\":\"0.0\",\"local_date_time\":\"2023-10-01T12:00:00\",\"local_date_time_full\":\"2023-10-01T12:00:00Z\",\"air_temp\":\"25.0\",\"apparent_t\":\"25.0\",\"cloud\":\"0\",\"dewpt\":\"10.0\",\"press\":\"1010.0\",\"rel_hum\":\"50\",\"wind_dir\":\"180\",\"wind_speed_kmh\":\"10\",\"wind_spd_kt\":\"5\"}", i, i);
            server.handlePutRequest(jsonBody, out);
        }

        Map<String, WeatherData> weatherDataStore = server.getWeatherDataStore();
        assertFalse(weatherDataStore.containsKey("ID01"), "Oldest entry should be evicted from the cache");
        assertTrue(weatherDataStore.containsKey("ID21"), "Newest entry should be present in the cache");
    }
}