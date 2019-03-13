package com.williamgraver.applicationmeteo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentCondition {
    Date date;
    String hour, tmp, wnd_spd, wnd_gust, wnd_dir, pressure, humidity, condition, condition_key, icon, icon_big;

    public  CurrentCondition(JSONObject jsonObject) throws JSONException {
        try {
            date= new SimpleDateFormat("dd.MM.yyyy").parse(jsonObject.getString("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        hour = jsonObject.getString("hour");
        tmp = jsonObject.getString("tmp");
        wnd_spd = jsonObject.getString("wnd_spd");
        wnd_gust = jsonObject.getString("wnd_gust");
        wnd_dir = jsonObject.getString("wnd_dir");
        pressure = jsonObject.getString("pressure");
        humidity = jsonObject.getString("humidity");
        condition = jsonObject.getString("condition");
        condition_key = jsonObject.getString("condition_key");
        icon = jsonObject.getString("icon");
        icon_big = jsonObject.getString("icon_big");

    }
}
