package com.williamgraver.applicationmeteo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DonneesMeteos implements Parcelable {

    CityInfo cityInfo;
    CurrentCondition currentCondition;
    FcstDay fcstDay_0, fcstDay_1, fcstDay_2, fcstDay_3, fcstDay_4;
    ForecastInfo forecastInfo;

    public DonneesMeteos(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

            cityInfo = new CityInfo(new JSONObject(jsonObject.getString("city_info")));
            currentCondition = new CurrentCondition(new JSONObject(jsonObject.getString("current_condition")));
            forecastInfo = new ForecastInfo(new JSONObject(jsonObject.getString("forecast_info")));

            fcstDay_0 = new FcstDay(new JSONObject(jsonObject.getString("fcst_day_0")));
            fcstDay_1 = new FcstDay(new JSONObject(jsonObject.getString("fcst_day_1")));
            fcstDay_2 = new FcstDay(new JSONObject(jsonObject.getString("fcst_day_2")));
            fcstDay_3 = new FcstDay(new JSONObject(jsonObject.getString("fcst_day_3")));
            fcstDay_4 = new FcstDay(new JSONObject(jsonObject.getString("fcst_day_4")));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected DonneesMeteos(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DonneesMeteos> CREATOR = new Creator<DonneesMeteos>() {
        @Override
        public DonneesMeteos createFromParcel(Parcel in) {
            return new DonneesMeteos(in);
        }

        @Override
        public DonneesMeteos[] newArray(int size) {
            return new DonneesMeteos[size];
        }
    };
}
