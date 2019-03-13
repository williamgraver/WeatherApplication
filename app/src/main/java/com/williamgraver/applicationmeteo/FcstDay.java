package com.williamgraver.applicationmeteo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FcstDay {
    Date date;
    String day_short, day_long, tmin, tmax, condition, condition_key, icon, icon_big;

    public FcstDay(JSONObject jsonObject){
        try {
            date= new SimpleDateFormat("dd.MM.yyyy").parse(jsonObject.getString("date"));
            day_short = jsonObject.getString("day_short");
            day_long = jsonObject.getString("day_long");
            tmin = jsonObject.getString("tmin");
            tmax = jsonObject.getString("tmax");
            condition = jsonObject.getString("condition");
            condition_key = jsonObject.getString("condition_key");
            icon = jsonObject.getString("icon");
            icon_big = jsonObject.getString("icon_big");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
