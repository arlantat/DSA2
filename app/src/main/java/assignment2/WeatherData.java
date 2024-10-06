package assignment2;

import com.google.gson.Gson;

public class WeatherData {
    private String id;
    private String name;
    private String state;
    private String time_zone;
    private String lat;
    private String lon;
    private String local_date_time;
    private String local_date_time_full;
    private String air_temp;
    private String apparent_t;
    private String cloud;
    private String dewpt;
    private String press;
    private String rel_hum;
    private String wind_dir;
    private String wind_speed_kmh;
    private String wind_spd_kt;

    public WeatherData(String id, String name, String state, String time_zone, String lat, String lon,
            String local_date_time, String local_date_time_full, String air_temp, String apparent_t, String cloud,
            String dewpt, String press, String rel_hum, String wind_dir, String wind_speed_kmh, String wind_spd_kt) {
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

    @Override
    public String toString() {
        return "WeatherData [id=" + id + ", name=" + name + ", state=" + state + ", time_zone=" + time_zone + ", lat="
                + lat + ", lon=" + lon + ", local_date_time=" + local_date_time + ", local_date_time_full="
                + local_date_time_full + ", air_temp=" + air_temp + ", apparent_t=" + apparent_t + ", cloud=" + cloud
                + ", dewpt=" + dewpt + ", press=" + press + ", rel_hum=" + rel_hum + ", wind_dir=" + wind_dir
                + ", wind_speed_kmh=" + wind_speed_kmh + ", wind_spd_kt=" + wind_spd_kt + "]";
    }
}