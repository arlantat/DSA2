package assignment2;

public class WeatherJSONTransformer {

    public static String toJson(WeatherData weatherData) {
        if (weatherData == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(weatherData.getId()).append("\",");
        json.append("\"name\":\"").append(weatherData.getName()).append("\",");
        json.append("\"state\":\"").append(weatherData.getState()).append("\",");
        json.append("\"time_zone\":\"").append(weatherData.getTime_zone()).append("\",");
        json.append("\"lat\":\"").append(weatherData.getLat()).append("\",");
        json.append("\"lon\":\"").append(weatherData.getLon()).append("\",");
        json.append("\"local_date_time\":\"").append(weatherData.getLocal_date_time()).append("\",");
        json.append("\"local_date_time_full\":\"").append(weatherData.getLocal_date_time_full()).append("\",");
        json.append("\"air_temp\":\"").append(weatherData.getAir_temp()).append("\",");
        json.append("\"apparent_t\":\"").append(weatherData.getApparent_t()).append("\",");
        json.append("\"cloud\":\"").append(weatherData.getCloud()).append("\",");
        json.append("\"dewpt\":\"").append(weatherData.getDewpt()).append("\",");
        json.append("\"press\":\"").append(weatherData.getPress()).append("\",");
        json.append("\"rel_hum\":\"").append(weatherData.getRel_hum()).append("\",");
        json.append("\"wind_dir\":\"").append(weatherData.getWind_dir()).append("\",");
        json.append("\"wind_speed_kmh\":\"").append(weatherData.getWind_speed_kmh()).append("\",");
        json.append("\"wind_spd_kt\":\"").append(weatherData.getWind_spd_kt()).append("\"");
        json.append("}");
        return json.toString();
    }

    public static WeatherData fromJson(String jsonString) {
        if (jsonString == null || jsonString.equals("null")) {
            return null;
        }

        jsonString = jsonString.trim();
        if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
            jsonString = jsonString.substring(1, jsonString.length() - 1);
            String[] keyValuePairs = jsonString.split(",");
            String id = null, name = null, state = null, time_zone = null, lat = null, lon = null, local_date_time = null, local_date_time_full = null, air_temp = null, apparent_t = null, cloud = null, dewpt = null, press = null, rel_hum = null, wind_dir = null, wind_speed_kmh = null, wind_spd_kt = null;

            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split(":");
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");

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
        }

        return null;
    }
}