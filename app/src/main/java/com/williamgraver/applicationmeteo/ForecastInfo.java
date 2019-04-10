package com.williamgraver.applicationmeteo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class ForecastInfo implements Parcelable {
    double latitude, longitude, elevation;

    public ForecastInfo(JSONObject jsonObject) {
        latitude = jsonObject.optDouble("latitude", 0.0);
        longitude = jsonObject.optDouble("longitude", 0.0);
        elevation = jsonObject.optDouble("elevation", 0.0);
    }

    protected ForecastInfo(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        elevation = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(elevation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ForecastInfo> CREATOR = new Creator<ForecastInfo>() {
        @Override
        public ForecastInfo createFromParcel(Parcel in) {
            return new ForecastInfo(in);
        }

        @Override
        public ForecastInfo[] newArray(int size) {
            return new ForecastInfo[size];
        }
    };
}
