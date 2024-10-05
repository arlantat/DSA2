package assignment2;
import com.google.gson.Gson;

public class PutRequest {
    private WeatherData weatherData;
    private int lamportTime;

    public PutRequest(WeatherData weatherData, int lamportTime) {
        this.weatherData = weatherData;
        this.lamportTime = lamportTime;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public int getLamportTime() {
        return lamportTime;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PutRequest fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, PutRequest.class);
    }
}
