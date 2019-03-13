package com.williamgraver.applicationmeteo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawer;
    NavigationView nv;
    GoogleSignInAccount account = null;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_reorder_white_24dp);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        nv = (NavigationView) findViewById(R.id.navigation);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return manageNavigationViewItemClick(item);
            }
        });

        Intent t = getIntent();
        account = t.getParcelableExtra("GoogleAccount");
        callWebService(null, null);
        configureNavHeader(account, getIntent().getStringExtra("UserName"));

        // Geolocalisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            callWebService((Double.toString(location.getLatitude())), Double.toString(location.getLongitude()));
                        }
                    }
                });
    }


    public void configureNavHeader(GoogleSignInAccount account, String userName){
        View viewheader = getLayoutInflater().inflate(R.layout.header_navigation,null);
        ImageView image = (ImageView)viewheader.findViewById(R.id.imageuser);
        TextView textView =(TextView)viewheader.findViewById(R.id.nomUser);
        if (account != null) {
            textView.setText(account.getDisplayName());
//            System.out.print("REGARDER MOI : " + account.getPhotoUrl());
            Glide.with(this).load(account.getPhotoUrl()).into(image);
        } else {
            textView.setText(userName);
            image.setImageResource(R.drawable.default_profile);
        }


        nv.addHeaderView(viewheader);
    }

    private boolean manageNavigationViewItemClick(MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers();
        if(item.getItemId() == R.id.menu_forecast){
            System.out.print("TU VAS ME VOIR SINON JE VAIS MENENERVER");
        } else if (item.getItemId() == R.id.disconnect){
            Intent t = new Intent(this, LoginActivity.class);
            t.putExtra("LogoutGoogle", account!=null);
            startActivity(t);
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


    private void callWebService(String longitude, String latitude){
        String url = "";
        if ((longitude != null && longitude != null ) && (!longitude.isEmpty() && !longitude.isEmpty())){
            url = "https://www.prevision-meteo.ch/services/json/lat="+latitude + "lng=" + longitude ;
            System.out.print("URL DE LA REQUETE : " + url);

        } else {
            url= "https://www.prevision-meteo.ch/services/json/grenoble";
        }
        final TextView homeText = (TextView)findViewById(R.id.textHomePage);
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
