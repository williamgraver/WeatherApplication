package com.williamgraver.applicationmeteo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent t= getIntent();
        FcstDay day = (FcstDay)t.getParcelableExtra("fcstDay");
        System.out.print("FCST DAY " + day.condition);
    }
}
