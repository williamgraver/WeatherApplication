package com.williamgraver.applicationmeteo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    NavigationView nv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_reorder_white_24dp);
        drawer = (DrawerLayout)findViewById(R.id.drawer);
        nv = (NavigationView)findViewById(R.id.navigation);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return manageNavigationViewItemClick(item);
            }
        });

        callWebService();


        configureNavHeader(getIntent().getStringExtra("UserName"));
    }


    public void configureNavHeader(String userName){
        View viewheader = getLayoutInflater().inflate(R.layout.header_navigation,null);
        TextView textView =(TextView)viewheader.findViewById(R.id.nomUser);
        textView.setText(userName);
        nv.addHeaderView(viewheader);
    }

    private boolean manageNavigationViewItemClick(MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers();
        if(item.getItemId() == R.id.menu_forecast){
            System.out.print("TU VAS ME VOIR SINON JE VAIS MENENERVER");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    private void callWebService(){

        final TextView homeText = (TextView)findViewById(R.id.textHomePage);
        String url= "https://www.prevision-meteo.ch/services/json/grenoble";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        homeText.setText(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }


        );

        queue.add(request);
    }
}
