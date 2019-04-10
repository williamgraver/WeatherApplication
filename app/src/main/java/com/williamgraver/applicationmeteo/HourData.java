package com.williamgraver.applicationmeteo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class HourData implements Parcelable {
    String hour, icon, condition, temperature, winddirection, windspeed, winddegre;

    public HourData(String hour, JSONObject json) {
        this.hour = hour;
        icon = json.optString("ICON");
        condition = json.optString("CONDITION");
        temperature = json.optString("TMP2m");
        winddirection = json.optString("WNDDIRCARD10");
        windspeed = json.optString("WNDSPD10m");
        winddegre = json.optString("WNDDIR10m");
    }

    protected HourData(Parcel in) {
        hour = in.readString();
        icon = in.readString();
        condition = in.readString();
        temperature = in.readString();
        winddirection = in.readString();
        windspeed = in.readString();
        winddegre = in.readString();

    }

    public static final Creator<HourData> CREATOR = new Creator<HourData>() {
        @Override
        public HourData createFromParcel(Parcel in) {
            return new HourData(in);
        }

        @Override
        public HourData[] newArray(int size) {
            return new HourData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(hour);
        parcel.writeString(icon);
        parcel.writeString(condition);
        parcel.writeString(temperature);
        parcel.writeString(winddirection);
        parcel.writeString(windspeed);
        parcel.writeString(winddegre);
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWinddirection() {
        return winddirection;
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }

    public String getWinddegre() {
        return winddegre;
    }

    public void setWinddegre(String winddegre) {
        this.winddegre = winddegre;
    }
}
