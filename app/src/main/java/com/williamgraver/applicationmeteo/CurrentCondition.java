package com.williamgraver.applicationmeteo;

import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentCondition extends FcstDay implements Parcelable {
    Date date;
    String hour, tmp, wnd_spd, wnd_gust, wnd_dir, pressure, humidity;

    public CurrentCondition(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(jsonObject.getString("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        hour = jsonObject.optString("hour");
        tmp = jsonObject.optString("tmp");
        wnd_spd = jsonObject.optString("wnd_spd");
        wnd_gust = jsonObject.optString("wnd_gust");
        wnd_dir = jsonObject.optString("wnd_dir");
        pressure = jsonObject.optString("pressure");
        humidity = jsonObject.optString("humidity");


    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getWnd_spd() {
        return wnd_spd;
    }

    public void setWnd_spd(String wnd_spd) {
        this.wnd_spd = wnd_spd;
    }

    public String getWnd_gust() {
        return wnd_gust;
    }

    public void setWnd_gust(String wnd_gust) {
        this.wnd_gust = wnd_gust;
    }

    public String getWnd_dir() {
        return wnd_dir;
    }

    public void setWnd_dir(String wnd_dir) {
        this.wnd_dir = wnd_dir;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
