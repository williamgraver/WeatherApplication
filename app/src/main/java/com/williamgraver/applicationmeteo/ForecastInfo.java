package com.williamgraver.applicationmeteo;

import org.json.JSONException;
import org.json.JSONObject;

public class ForecastInfo {
    double latitude,longitude, elevation;

    public  ForecastInfo(JSONObject jsonObject) throws JSONException {
        latitude = jsonObject.getDouble("latitude");
        longitude = jsonObject.getDouble("longitude");
        elevation = jsonObject.getDouble("elevation");
    }
}
