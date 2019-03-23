package com.williamgraver.applicationmeteo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeteoAdapter extends ArrayAdapter<FcstDay> {
    private final Context context;
    private final ArrayList<FcstDay> fcstDays;
    private ArrayAdapter<HourData> adapter;


    public MeteoAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context= context;
        fcstDays = new ArrayList<>();
    }

    @Override
    public int getPosition(FcstDay item) {
        return fcstDays.indexOf(item);
    }

    @Override
    public int getCount(){
        return fcstDays.size();
    }


    @Override
    public FcstDay getItem(int position) {
        return fcstDays.get(position);
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // retourne null dans le cas du currentCondition
        if(position == 0) return new View(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FcstDay dayToShow = getItem(position);
        View rowView = null;

        if(position == 1){
            rowView = inflater.inflate(R.layout.detailweather_item, parent, false);
            TextView tvConditions = (TextView)rowView.findViewById(R.id.conditions);
            TextView tvTemperature = (TextView)rowView.findViewById(R.id.temperature);
            TextView tvWind = (TextView)rowView.findViewById(R.id.wind);
            ImageView windImage = (ImageView)rowView.findViewById(R.id.winddirection);
            ImageView weatherImage = (ImageView)rowView.findViewById(R.id.actualDayImg);
            ListView listDetail = (ListView)rowView.findViewById(R.id.detailList);
            CurrentCondition currentCondition =(CurrentCondition) getItem(0);

            tvConditions.setText(currentCondition.condition);
            tvTemperature.setText("Temperature : " + currentCondition.tmp + "°C");
            Glide.with(context).load(currentCondition.icon_big).into(weatherImage);
            Integer currentHour = new Date().getHours();
            if((currentCondition.wnd_spd != null) && currentCondition.wnd_spd != "0") {
                windImage.setVisibility(View.VISIBLE);
                windImage.setRotation( Float.parseFloat(dayToShow.getHours().get(currentHour).getWinddegre()));
                TextView windText = (TextView)rowView.findViewById(R.id.wind);

                windText.setText(currentCondition.wnd_spd +" km/h " + currentCondition.wnd_dir);
            }


            RecyclerView recyclerView = (RecyclerView) rowView.findViewById(R.id.detailList);
            LinearLayoutManager layoutManager= new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new HourAdapater(dayToShow.getHoursFrom(currentHour + 1)));
        } else{


            rowView = inflater.inflate(R.layout.listview_item, parent, false);
            // JOURS ET DATE
            TextView tvDay = (TextView)rowView.findViewById(R.id.weatherday);
            TextView tvDate = (TextView)rowView.findViewById(R.id.weatherdate);
            //
            TextView tvUpTemp = (TextView)rowView.findViewById(R.id.uppertemperature);
            TextView tvDownTemp = (TextView)rowView.findViewById(R.id.lowertemperature);

            ImageView weatherImage = (ImageView)rowView.findViewById(R.id.weatherimage);
            ImageView windImage = (ImageView)rowView.findViewById(R.id.windimage);


            tvDay.setText(dayToShow.getDay_long());

            //
            DateFormat dateFormater = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            tvDate.setText(dateFormater.format(dayToShow.getDate()));
            tvUpTemp.setText("Max : " + dayToShow.getTmax() + "°C");
            tvDownTemp.setText("Min : " + dayToShow.getTmin() + "°C ");


            Glide.with(context).load(dayToShow.icon).into(weatherImage);
        }

        return  rowView;
    }

    @Override
    public void addAll(@NonNull Collection<? extends FcstDay> collection) {
        fcstDays.clear();
        fcstDays.addAll(collection);
        notifyDataSetChanged();
    }
}
