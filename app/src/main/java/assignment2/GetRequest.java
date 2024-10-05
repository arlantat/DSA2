package assignment2;
import com.google.gson.Gson;

public class GetRequest {
    private int lamportTime;

    public GetRequest(int lamportTime) {
        this.lamportTime = lamportTime;
    }

    public int getLamportTime() {
        return lamportTime;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this); // Convert GetRequest object to JSON format
    }

    public static GetRequest fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, GetRequest.class); // Convert JSON string to GetRequest object
    }
}
