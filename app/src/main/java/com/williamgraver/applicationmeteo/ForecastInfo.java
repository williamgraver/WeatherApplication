package com.williamgraver.applicationmeteo;

import org.json.JSONException;
import org.json.JSONObject;

public class ForecastInfo {
    double latitude,longitude, elevation;

    public  ForecastInfo(JSONObject jsonObject)  {
            latitude =  jsonObject.optDouble("latitude",0.0);
            longitude = jsonObject.optDouble("longitude",0.0);
            elevation = jsonObject.optDouble("elevation",0.0);
    }
}
