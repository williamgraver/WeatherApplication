package com.williamgraver.applicationmeteo;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        Intent t= getIntent();
        FcstDay day = (FcstDay)t.getParcelableExtra("fcstDay");
        String currentDay = t.getStringExtra("DayName");

        // JOURS ET DATE
        TextView tvDay = (TextView)findViewById(R.id.weatherday);
        TextView tvDate = (TextView)findViewById(R.id.weatherdate);
        //
        TextView tvUpTemp = (TextView)findViewById(R.id.uppertemperature);
        TextView tvDownTemp = (TextView)findViewById(R.id.lowertemperature);

        ImageView weatherImage = (ImageView)findViewById(R.id.weatherimage);
        ImageView windImage = (ImageView)findViewById(R.id.windimage);


        tvDay.setText(day.getDay_long());

        //
        DateFormat dateFormater = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        tvDate.setText(currentDay);
        tvUpTemp.setText("Max : " + day.getTmax() + "°C");
        tvDownTemp.setText("Min : " + day.getTmin() + "°C ");


        Glide.with(this).load(day.icon_big).into(weatherImage);




        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.detailItem);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new HourAdapater(day.getHours()));
    }

    @Override
    public void onBackPressed(){
        finishActivity(0);
    }
//    public boolean onOptionsItemSelected(boolean test) {
//        finish();
//       return false;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }
}
