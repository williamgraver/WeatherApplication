package com.williamgraver.applicationmeteo;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnSuccessListener;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    NavigationView nv;
    GoogleSignInAccount account = null;
    private FusedLocationProviderClient fusedLocationClient;
    ListView listItem;
    List<FcstDay> daysToShow = new ArrayList<>();
    ArrayAdapter<FcstDay> adapter;
    SwipeRefreshLayout refreshLayout;
    TextView activityTitleTV;
    String currentCity ="Grenoble";
    DonneesMeteos donnees;
    final String CHANNEL_ID = "42";
    NotificationService service;
    String[] cities = new String[]{};
    Button button;
    PopupMenu popup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
//        createNotificationChannel();

        Intent serviceIntent = new Intent(this, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(serviceIntent);
//            ComponentName thisWidget=new ComponentName(this, MeteoWidget.class);
//            int []allWidgetIds=AppWidgetManager.getInstance(this).getAppWidgetIds(thisWidget);
//
//            //built intent to call service
//            Intent intent=new Intent(this.getApplicationContext(), MeteoWidget.WidgetService.class);
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,allWidgetIds);
//            startForegroundService(intent);
        } else{
            startService(serviceIntent);
        }
        setUpActionBar(getSupportActionBar());
        View actionBar = (View)findViewById(R.id.action_bar_container);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        nv = (NavigationView) findViewById(R.id.navigation);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return manageNavigationViewItemClick(item);
            }
        });


        getCITYES();


        // gestion de la liste view
        adapter = new MeteoAdapter(this, 0);
        listItem = (ListView)findViewById(R.id.itemlist);
        listItem.setAdapter(adapter);

        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FcstDay day = daysToShow.get(i);

                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("fcstDay", day);
                DateFormat dateFormater = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                intent.putExtra("DayName",dateFormater.format(day.getDate()));
                startActivity(intent);
            }
        });

        // récupération du compte
        Intent t = getIntent();
        account = t.getParcelableExtra("GoogleAccount");
        configureNavHeader(account, getIntent().getStringExtra("UserName"));

        // refresher
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PopulateListItem(currentCity);
                if (donnees != null){
//                    manageNotifications();
                }
            }
        });

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                PopulateListItem(currentCity);
                if (donnees != null){
//                    manageNotifications();
                }

            }
        }, 0);

        // Notification management

    }

//    private void manageNotifications() {
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_cloud_blue_24dp)
//                .setContentTitle(currentCity + " : " + donnees.currentCondition.getTmp() +"°C")
//                .setContentText(donnees.currentCondition.condition)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setOngoing(true);
//        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//        notificationManager.notify(512, builder.build());
//    }


    public void configureNavHeader(GoogleSignInAccount account, String userName){
        View viewheader = getLayoutInflater().inflate(R.layout.header_navigation,null);
        ImageView image = (ImageView)viewheader.findViewById(R.id.imageuser);
        TextView textView =(TextView)viewheader.findViewById(R.id.nomUser);
        if (account != null) {
            textView.setText(account.getDisplayName());
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

    private void getCITYES() {
        String url = "https://www.prevision-meteo.ch/services/json/list-cities";
        final RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.contains("errors")){
                        } else{
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                cities = new String[jsonObject.length()];
                                for(int i = 0; i<jsonObject.length(); i++){
                                    cities[i] = StringUtils.capitalize(jsonObject.getJSONObject(String.valueOf(i)).getString("url"));
                                    System.out.println(jsonObject.getJSONObject(String.valueOf(i)).getString("url"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(request);
    }

    private void callWebService(String cityName){
        String url = "";
        if (cityName != null && !cityName.isEmpty()){
            url = "https://www.prevision-meteo.ch/services/json/" + cityName ;

        } else {
            url= "https://www.prevision-meteo.ch/services/json/grenoble";
        }
        //final TextView homeText = (TextView)findViewById(R.id.textHomePage);
        final RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //homeText.setText(response);
                        //response.contains()
                        if(response.contains("errors") && response.contains("11")){
                            createNewRequest();
                        } else{
                            donnees = new DonneesMeteos(response);
                            daysToShow.clear();
                            daysToShow.add(donnees.currentCondition);
                            daysToShow.add(donnees.fcstDay_0);
                            daysToShow.add(donnees.fcstDay_1);
                            daysToShow.add(donnees.fcstDay_2);
                            daysToShow.add(donnees.fcstDay_3);
                            daysToShow.add(donnees.fcstDay_4);

                            adapter.addAll(daysToShow);
                        }
                    }

                    public void createNewRequest(){
//                        StringRequest
//                        queue.add(new StringRequest())
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


    private void PopulateListItem(String City){
        if(City.length() == 0) {
            refreshLayout.setRefreshing(true);
            // récupération de la position
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            // Si on a pas la permission de localisation
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                callWebService(null);
            } else { // Sinon
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    } catch (IOException e) {

                                    }
                                    if (addresses != null && addresses.size() > 0) {
                                        currentCity = addresses.get(0).getLocality();
                                        currentCity = currentCity.replace(" ", "-");
                                        currentCity = currentCity.replace("'", "-");

                                    System.out.println("City name : " + currentCity);

                                        activityTitleTV.setText(currentCity);
                                        callWebService(currentCity);
                                        popup.getMenu().add(currentCity);


                                    } else {
                                        currentCity = "Grenoble";
                                        callWebService(null);
                                    }
                                } else {
                                    callWebService(null);
                                }
                            }
                        });
            }
            adapter.notifyDataSetChanged();

            refreshLayout.setRefreshing(false);


        } else {
            refreshLayout.setRefreshing(true);
            activityTitleTV.setText(currentCity);
            callWebService(currentCity);
            refreshLayout.setRefreshing(false);
        }

    }

    private void setUpActionBar(ActionBar actionBar) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View customActionBarView = layoutInflater.inflate(R.layout.actionbar, null);


        // this view is used to show tile
        activityTitleTV = (TextView) customActionBarView.findViewById(R.id.custom_actionbar_titleText_tv);
        activityTitleTV.setText("Grenoble");
        activityTitleTV.setGravity(Gravity.CENTER);


        final ImageView button = (ImageView) customActionBarView.findViewById(R.id.switch_city);
        button.setVisibility(View.VISIBLE);

        //Creating the instance of PopupMenu
        popup = new PopupMenu(MainActivity.this, button);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.city_menu, popup.getMenu());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup.show();

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        currentCity = item.getTitle().toString();
                        PopulateListItem(currentCity);
                        Toast.makeText(MainActivity.this,"Vous avez selectionné : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        popup.dismiss();
                        return true;
                    }
                });

            }
        });//closing the setOnClickListener method


        //this view can be used for back navigation
        ImageView backToRecorderIV = (ImageView) customActionBarView.findViewById(R.id.custom_actionbar_back_iv);
        backToRecorderIV.setVisibility(View.VISIBLE);
        backToRecorderIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.custom_actionbar_back_iv:
                        drawer.openDrawer(GravityCompat.START);
                        break;
                }
            }
        });
        ImageView addCity = (ImageView) customActionBarView.findViewById(R.id.add_city);
        addCity.setVisibility(View.VISIBLE);
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.add_city:
                        //drawer.openDrawer(GravityCompat.START);
                        System.out.println("$$$$$$$$**************** Pressed");
                        openDialog();
                        break;
                }
            }
        });


        actionBar.setCustomView(customActionBarView);
        actionBar.setDisplayShowCustomEnabled(true);

    }
    public void openDialog() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.


        dialog.setContentView(R.layout.dialog_demo);

        final AutoCompleteTextView editTextView = dialog.findViewById(R.id.autoComplete);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities);
        editTextView.setAdapter(adapterCity);

        dialog.setTitle("Ajouter une ville à votre application");
        dialog.show();

        Button bt_yes = (Button)dialog.findViewById(R.id.dialog_ok);
        Button bt_no = (Button)dialog.findViewById(R.id.dialog_cancel);

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
                System.out.println(editTextView.getText());
                currentCity = editTextView.getText().toString();
                PopulateListItem(editTextView.getText().toString());
                Toast.makeText(MainActivity.this, currentCity + " a été ajouté", Toast.LENGTH_SHORT).show();
                popup.getMenu().add(currentCity);
                dialog.dismiss();

            }
        });
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }



}
