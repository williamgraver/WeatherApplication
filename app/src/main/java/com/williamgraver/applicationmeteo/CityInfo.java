package com.williamgraver.applicationmeteo;

import org.json.JSONException;
import org.json.JSONObject;

public class CityInfo {
    String name, country, sunrise, sunset;
    double latitude, longitude,elevation;


    public CityInfo(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        country = jsonObject.getString("country");
        latitude = jsonObject.getDouble("latitude");
        longitude = jsonObject.getDouble("longitude");
        elevation = jsonObject.getDouble("elevation");
        sunrise = jsonObject.getString("sunrise");
        sunset = jsonObject.getString("sunset");
    }
}
