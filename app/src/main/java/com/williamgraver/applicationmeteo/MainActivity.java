package com.williamgraver.applicationmeteo;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    String currentCity = "";
    DonneesMeteos donnees;
    final String CHANNEL_ID = "42";
    String[] cities = new String[]{};
    List<String> citiesSaved = new ArrayList<>();
    String cityNamePosition = "Grenoble";
    PopupMenu popup = null;
    boolean positionSetted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Démarrage des services notification et widget
        Intent serviceIntent = new Intent(this, NotificationService.class);
        Intent widget = new Intent(this, MeteoWidget.class);
        startService(widget);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Mise en place de l'action bar
        setUpActionBar(getSupportActionBar());
        View actionBar = (View) findViewById(R.id.action_bar_container);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        nv = (NavigationView) findViewById(R.id.navigation);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return manageNavigationViewItemClick(item);
            }
        });


        // Gestion de l'ajout des villes chargement des villes sauvegardées + villes de l'api

        getCities();


        // gestion de la liste view
        adapter = new MeteoAdapter(this, 0);
        listItem = (ListView) findViewById(R.id.itemlist);
        listItem.setAdapter(adapter);

        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FcstDay day = daysToShow.get(i);

                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("fcstDay", day);
                DateFormat dateFormater = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                intent.putExtra("DayName", dateFormater.format(day.getDate()));
                startActivity(intent);
            }
        });

        // récupération du compte
        Intent t = getIntent();
        account = t.getParcelableExtra("GoogleAccount");
        configureNavHeader(account, getIntent().getStringExtra("UserName"));

        // refresher
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cityNamePosition.contains(currentCity)){
                    populateListItem("");
                } else{
                    populateListItem(currentCity);
                }

            }
        });

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(cityNamePosition.contains(currentCity)){
                    populateListItem("");
                } else{
                    populateListItem(currentCity);
                }

            }
        }, 0);

        loadLastCities();

    }

    /**
     * Récupère l'ensemble des villes précédement sauvegardées
     */
    private void loadLastCities() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        Map<String, ?> cities = sharedPreferences.getAll();

        if (cities != null && cities.size() != 0) {
            for (Map.Entry<String, ?> city : cities.entrySet()) {
                String currentCity = city.getValue().toString();
                this.citiesSaved.add(currentCity);
                popup.getMenu().add(currentCity);
            }

        }


    }

    /**
     * Sauvegarde dans un SharedPreference l'ensemble des villes ajoutées
     */
    private void saveProperties() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < citiesSaved.size(); i++) {
            editor.putString("city" + i, citiesSaved.get(i));
        }
        editor.commit();
    }


    /**
     * Configuration du Header de la navigation avec les informations du compte google si il est non null
     * @param account le compte google de l'utilisateur connecté
     * @param userName le nom de l'utilisateur s'il n'a pas de compte google
     */
    public void configureNavHeader(GoogleSignInAccount account, String userName) {
        View viewheader = getLayoutInflater().inflate(R.layout.header_navigation, null);
        ImageView image = (ImageView) viewheader.findViewById(R.id.imageuser);
        TextView textView = (TextView) viewheader.findViewById(R.id.nomUser);
        if (account != null) {
            textView.setText(account.getDisplayName());
            Glide.with(this).load(account.getPhotoUrl()).into(image);
        } else {
            textView.setText(userName);
            image.setImageResource(R.drawable.default_profile);
        }


        nv.addHeaderView(viewheader);
    }

    /**
     * Permet de redigirer sur la bonne activité lors d'un click sur un item du Nav
     * @param item l'item clické
     * @return true dans tous les cas
     */
    private boolean manageNavigationViewItemClick(MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers();
        if (item.getItemId() == R.id.menu_forecast) {

        } else if (item.getItemId() == R.id.disconnect) {
            Intent t = new Intent(this, LoginActivity.class);
            t.putExtra("LogoutGoogle", account != null);
            startActivity(t);
        } else if (item.getItemId() == R.id.menu_home) {
            // TODO : rajouter un properties
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Charge dans la variables cities l'ensemble des villes fournies par l'api
     */
    private void getCities() {
        String url = "https://www.prevision-meteo.ch/services/json/list-cities";
        final RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("errors")) {
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                cities = new String[jsonObject.length()];
                                for (int i = 0; i < jsonObject.length(); i++) {
                                    cities[i] = StringUtils.capitalize(jsonObject.getJSONObject(String.valueOf(i)).getString("url"));

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

    /**
     * Fais appelle au webservice
     * @param cityName le nom de la ville
     * @param latitude la latitude dans le cas où on cherche par rapport a notre position
     * @param longitude la longitude dans le cas où on cherche par rapport a notre position
     */
    private void callWebService(String cityName, @Nullable final double latitude, @Nullable final double longitude) {
        String url = "";
        if (cityName != null && !cityName.isEmpty()) {
            url = "https://www.prevision-meteo.ch/services/json/" + cityName;

        } else {
            url = "https://www.prevision-meteo.ch/services/json/grenoble";
        }

        final RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //homeText.setText(response);
                        //response.contains()
                        if (response.contains("errors") && response.contains("11")) {
                            createNewRequest();
                        } else {
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
                    // Si la ville dans laquelle on se situe n'est pas trouvée par la requete on relance une requete avec les longitudes et latitude cette fois
                    public void createNewRequest() {
                        if (longitude != 0 && latitude != 0) {
                            String url = "https://www.prevision-meteo.ch/services/json/lat=" + latitude + "lng=" + longitude;
                            StringRequest request1 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response.contains("errors") && response.contains("11")) {
                                        return;
                                    }
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
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });
                            queue.add(request1);
                        }

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

    /**
     * Fais appelle au web service soit grace à la position du telephone soit avec le paramètre city
     * @param city dans le cas ou ce soit une ville sauvegardée, sinon @null
     */
    private void populateListItem(String city) {
        if (city == null || city.length() == 0) {
            refreshLayout.setRefreshing(true);
            // récupération de la position
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            // Si on a pas la permission de localisation
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                callWebService(null, 0.0, 0.0);
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
                                        cityNamePosition = currentCity + " (position)";


                                        activityTitleTV.setText(currentCity);
                                        callWebService(currentCity, location.getLatitude(), location.getLongitude());

                                        if (!positionSetted) {
                                            popup.getMenu().add(cityNamePosition);
                                            positionSetted = true;
                                        }

                                    } else {
                                        currentCity = "Grenoble";
                                        callWebService(null, 0.0, 0.0);
                                    }
                                } else {
                                    callWebService(null, 0.0, 0.0);
                                }
                            }
                        });
            }
            adapter.notifyDataSetChanged();

            refreshLayout.setRefreshing(false);


        } else {
            refreshLayout.setRefreshing(true);
            activityTitleTV.setText(currentCity);
            callWebService(currentCity, 0.0, 0.0);
            refreshLayout.setRefreshing(false);
        }

    }

    /**
     * Met en place l'action bar, recupere les differents elements, ajoute des listener sur le bouton ajouter et sur le popup
     * @param actionBar l'action bar a setup
     */
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
                        populateListItem(currentCity);
                        Toast.makeText(MainActivity.this, "Vous avez selectionné : " + item.getTitle(), Toast.LENGTH_SHORT).show();
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

                        openDialog();
                        break;
                }
            }
        });


        actionBar.setCustomView(customActionBarView);
        actionBar.setDisplayShowCustomEnabled(true);

    }

    /**
     * A l'ouverture du menu
     */
    public void openDialog() {
        final Dialog dialog = new Dialog(this); // Context, this, etc.


        dialog.setContentView(R.layout.dialog_demo);

        final AutoCompleteTextView editTextView = dialog.findViewById(R.id.autoComplete);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities);
        editTextView.setAdapter(adapterCity);

        dialog.setTitle(getString(R.string.addCityDial));
        dialog.show();

        Button bt_yes = (Button) dialog.findViewById(R.id.dialog_ok);
        Button bt_no = (Button) dialog.findViewById(R.id.dialog_cancel);

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCity = editTextView.getText().toString();
                if (citiesSaved.contains(currentCity)) {
                    return;
                }
                populateListItem(editTextView.getText().toString());
                Toast.makeText(MainActivity.this, currentCity + " a été ajouté", Toast.LENGTH_SHORT).show();
                if (!positionSetted) {
                    popup.getMenu().add(cityNamePosition);
                    positionSetted = true;
                }
                popup.getMenu().add(currentCity);

                citiesSaved.add(currentCity);
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
        saveProperties();
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        saveProperties();
        super.onPause();
    }
}
