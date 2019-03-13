package com.williamgraver.applicationmeteo;

import org.json.JSONException;
import org.json.JSONObject;

public class DonneesMeteos {
    CityInfo cityInfo;
    CurrentCondition currentCondition;
    FcstDay fcstDay_0, fcstDay_1, fcstDay_2, fcstDay_3, fcstDay_4;
    ForecastInfo forecastInfo;

    public DonneesMeteos(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
