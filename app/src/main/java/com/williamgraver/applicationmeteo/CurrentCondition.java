package com.williamgraver.applicationmeteo;

import org.json.JSONObject;

import java.util.Date;

public class CurrentCondition {
    Date date;
    String hour, tmp, wnd_spd, wnd_gust, wnd_dir, pressure, humidity, condition, condition_key, icon, icon_big;

    public  CurrentCondition(JSONObject jsonObject){

    }
}
