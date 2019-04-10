package com.williamgraver.applicationmeteo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FcstDay implements Parcelable {
    Date date;
    String day_short, day_long, tmin, tmax, condition, condition_key, icon, icon_big;
    List<HourData> hours;

    public List<HourData> getHours() {
        return hours;
    }

    public List<HourData> getHoursFrom(int hour) {
        List<HourData> hours = new ArrayList<>();
        if (hour >= 24) return hours;

        for (int i = hour; i < 24; i++) {
            hours.add(this.hours.get(i));
        }
        return hours;
    }

    public void setHours(List<HourData> hours) {
        this.hours = hours;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDay_short() {
        return day_short;
    }

    public void setDay_short(String day_short) {
        this.day_short = day_short;
    }

    public String getDay_long() {
        return day_long;
    }

    public void setDay_long(String day_long) {
        this.day_long = day_long;
    }

    public String getTmin() {
        return tmin;
    }

    public void setTmin(String tmin) {
        this.tmin = tmin;
    }

    public String getTmax() {
        return tmax;
    }

    public void setTmax(String tmax) {
        this.tmax = tmax;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition_key() {
        return condition_key;
    }

    public void setCondition_key(String condition_key) {
        this.condition_key = condition_key;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon_big() {
        return icon_big;
    }

    public void setIcon_big(String icon_big) {
        this.icon_big = icon_big;
    }

    public FcstDay(JSONObject jsonObject) {
        try {
            date = new SimpleDateFormat("dd.MM.yyyy").parse(jsonObject.optString("date"));
            day_short = jsonObject.optString("day_short");
            day_long = jsonObject.optString("day_long");
            tmin = jsonObject.optString("tmin");
            tmax = jsonObject.optString("tmax");
            condition = jsonObject.optString("condition");
            condition_key = jsonObject.optString("condition_key");
            icon = jsonObject.optString("icon");
            icon_big = jsonObject.optString("icon_big");


            if (!(this instanceof CurrentCondition)) {
                hours = new ArrayList<>();
                getHours(new JSONObject(jsonObject.getString("hourly_data")));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(day_short);
        parcel.writeString(day_long);
        parcel.writeString(tmin);
        parcel.writeString(tmax);
        parcel.writeString(condition);
        parcel.writeString(condition_key);
        parcel.writeString(icon);
        parcel.writeString(icon_big);
        parcel.writeList(hours);

    }

    public static final Creator<FcstDay> CREATOR = new Creator<FcstDay>() {
        @Override
        public FcstDay createFromParcel(Parcel in) {
            return new FcstDay(in);
        }

        @Override
        public FcstDay[] newArray(int size) {
            return new FcstDay[size];
        }


    };

    protected FcstDay(Parcel in) {
        //date= in.readDate();
        day_short = in.readString();
        day_long = in.readString();
        tmin = in.readString();
        tmax = in.readString();
        condition = in.readString();
        condition_key = in.readString();
        icon = in.readString();
        icon_big = in.readString();
        hours = new ArrayList<>();
        in.readList(hours, HourData.class.getClassLoader());
    }

    protected void getHours(JSONObject hourdata) {
        for (int i = 0; i <= 23; i++) {
            try {
                HourData hourData = new HourData(i + "H00", new JSONObject(hourdata.getString(i + "H00")));
                hours.add(hourData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
