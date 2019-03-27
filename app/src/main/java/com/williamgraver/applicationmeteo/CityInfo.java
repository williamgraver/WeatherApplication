package com.williamgraver.applicationmeteo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class CityInfo implements Parcelable {
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

    protected CityInfo(Parcel in) {
        name = in.readString();
        country = in.readString();
        sunrise = in.readString();
        sunset = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        elevation = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(sunrise);
        dest.writeString(sunset);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(elevation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CityInfo> CREATOR = new Creator<CityInfo>() {
        @Override
        public CityInfo createFromParcel(Parcel in) {
            return new CityInfo(in);
        }

        @Override
        public CityInfo[] newArray(int size) {
            return new CityInfo[size];
        }
    };
}
