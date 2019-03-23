package com.williamgraver.applicationmeteo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailItemHolder extends RecyclerView.ViewHolder {
    View rowView;

    public DetailItemHolder(@NonNull View itemView) {
        super(itemView);
        rowView =itemView;
    }

    public void bind(HourData hour){

        TextView tvHour = (TextView)rowView.findViewById(R.id.hourTv);
        TextView tvTemperature = (TextView)rowView.findViewById(R.id.temperatureDetail);
        TextView tvWind = (TextView)rowView.findViewById(R.id.windspeed);

        ImageView imgCondition = (ImageView)rowView.findViewById(R.id.conditionImg);
        ImageView imgWind = (ImageView)rowView.findViewById(R.id.windImage);


        tvHour.setText(hour.getHour());
        tvTemperature.setText(hour.getTemperature() +"°C");
        tvWind.setText(hour.getWindspeed()+ "km/h");

        imgWind.setRotation(Float.parseFloat(hour.getWinddegre()));
        Glide.with(imgCondition.getContext()).load(hour.getIcon()).into(imgCondition);
    }
}
