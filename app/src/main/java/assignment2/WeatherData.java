package assignment2;

import com.google.gson.Gson;

public class WeatherData {
    private String id;
    private String name;
    private String state;
    private String time_zone;
    private double lat;
    private double lon;
    private String local_date_time;
    private String local_date_time_full;
    private double air_temp;
    private double apparent_t;
    private String cloud;
    private double dewpt;
    private double press;
    private double rel_hum;
    private String wind_dir;
    private int wind_speed_kmh;
    private int wind_spd_kt;
    
    public WeatherData(String id, String name, String state, String time_zone, double lat, double lon,
            String local_date_time, String local_date_time_full, double air_temp, double apparent_t, String cloud,
            double dewpt, double press, double rel_hum, String wind_dir, int wind_speed_kmh, int wind_spd_kt) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.time_zone = time_zone;
        this.lat = lat;
        this.lon = lon;
        this.local_date_time = local_date_time;
        this.local_date_time_full = local_date_time_full;
        this.air_temp = air_temp;
        this.apparent_t = apparent_t;
        this.cloud = cloud;
        this.dewpt = dewpt;
        this.press = press;
        this.rel_hum = rel_hum;
        this.wind_dir = wind_dir;
        this.wind_speed_kmh = wind_speed_kmh;
        this.wind_spd_kt = wind_spd_kt;
    }

    // Convert this object to a JSON string
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Create a WeatherData object from a JSON string
    public static WeatherData fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, WeatherData.class);
    }

    public String getId() {
        return id;
    }
}