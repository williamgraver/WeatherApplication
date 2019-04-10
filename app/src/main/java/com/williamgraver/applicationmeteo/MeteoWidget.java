package com.williamgraver.applicationmeteo;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class MeteoWidget extends AppWidgetProvider {

    DonneesMeteos donneesMeteos = null;
    FusedLocationProviderClient fusedLocationClient;
    String currentCity = "";
    Context c;
    static Geocoder gcd;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, DonneesMeteos donneesMeteos) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.meteo_widget);
        views.setTextViewText(R.id.text_widget_city_name, "Le widget est en train de charger ");

        ComponentName thisWidget=new ComponentName(context, MeteoWidget.class);
        int []allWidgetIds=appWidgetManager.getAppWidgetIds(thisWidget);

        //built intent to call service
        Intent intent=new Intent(context.getApplicationContext(),WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,allWidgetIds);
        Log.w("LOG","before service");
        //update widget via service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startService(intent);
        } else{
            context.startService(intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        c = context;
        //getLocation();
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(c, appWidgetManager, appWidgetId, donneesMeteos);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }


    @Override
    public void onEnabled(Context context) {
        ComponentName thisWidget=new ComponentName(context, MeteoWidget.class);
        onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(thisWidget));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    public static class WidgetService extends JobIntentService{
        public LocationManager locationManager;
        public Location previousBestLocation = null;
        String currentCity = "";
        DonneesMeteos donnees;
        private static final int ONE_MINUTES = 1000 * 30 * 1;
        private static final int THIRTY_MINUTES = 1000 * 60 * 30;
        private static final int ONE_KILOMETER = 1000;
        private FusedLocationProviderClient fusedLocationClient;
        // Unique Identification Number for the Notification.
        // We use it on Notification start, and to cancel it.
        AppWidgetManager appWidgetManager;


        int []allWidgetIds;
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        /**
         * Class for clients to access.  Because we know this service always
         * runs in the same process as its clients, we don't need to deal with
         * IPC.
         */
        public class LocalBinder extends Binder {
            WidgetService getService() {
                return WidgetService.this;
            }
        }

        @Override
        public void onCreate() {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        /**
         * Au
         * @param intent
         * @param flags
         * @param startId
         * @return
         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            getLocation();
            ComponentName thisWidget=new ComponentName(this, MeteoWidget.class);
            appWidgetManager=AppWidgetManager.getInstance(this.getApplicationContext());
            allWidgetIds=intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (allWidgetIds == null){
                allWidgetIds=AppWidgetManager.getInstance(this.getApplicationContext()).getAppWidgetIds(thisWidget);
            }

            Integer widgetID = intent.getIntExtra("WidgetId",0);

            super.onStart(intent, startId);

            return START_REDELIVER_INTENT;
        }

        @Override
        public void onDestroy() {
            // Cancel the persistent notification.

        }

        @Override
        protected void onHandleWork(@NonNull Intent intent) {
            getLocation();
            appWidgetManager=AppWidgetManager.getInstance(this.getApplicationContext());
            allWidgetIds=intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            Log.w("WARN", "Dans le onHandleWork");

        }

        /**
         * Met a jour la vue du widget avec les donnees actuelles
         */
        public void updateInterface(){
            ComponentName thisWidget=new ComponentName(getApplicationContext(),MeteoWidget.class);

            Log.w("Widget","Trying to update Widget");


            if (allWidgetIds !=null){
                Log.w("LOG","From Intent" + String.valueOf(allWidgetIds.length));
                for(int widgetId:allWidgetIds){
                    RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.meteo_widget);
                    if (donnees != null) {
                        AppWidgetTarget awt = new AppWidgetTarget(getApplicationContext(),  views, R.id.imageView2, widgetId);
                        Glide.with(this).load(donnees.currentCondition.icon).asBitmap().into(awt);
                        views.setTextViewText(R.id.text_widget_city_name, currentCity);
                        views.setTextViewText(R.id.conditions, donnees.currentCondition.condition);
                        views.setTextViewText(R.id.temperature_widget, donnees.currentCondition.tmp + "°C ");
                        views.setTextViewText(R.id.updateTime, String.valueOf(new Date().getHours()) +"h "+  String.valueOf(new Date().getMinutes()));


                        Log.w("LOG","We have the city info " + currentCity);
                    }
                    else{
                        views.setTextViewText(R.id.text_widget_city_name,"wait till data load");
                    }

                    pushWidgetUpdate(getApplicationContext(), views);
                    //}
                }
            }

        }

        public static void pushWidgetUpdate(Context context, RemoteViews rv) {
            ComponentName myWidget = new ComponentName(context, MeteoWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(myWidget, rv);
        }

        private void getLocation(){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                callWebService(null);
            } else { // Sinon
                LocationListener listener = new WidgetService.MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, ONE_MINUTES, 0, listener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_MINUTES, 0, listener);
            }
        }
        private void callWebService(String cityName){
            String url = "";
            if (cityName != null && !cityName.isEmpty()){
                url = "https://www.prevision-meteo.ch/services/json/" + cityName ;

            } else {
                url= "https://www.prevision-meteo.ch/services/json/grenoble";
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            donnees = new DonneesMeteos(response);
                            updateInterface();
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

        public class MyLocationListener implements LocationListener
        {

            public void onLocationChanged(final Location loc) {

                loc.getLatitude();
                loc.getLongitude();
                final Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    currentCity=addresses.get(0).getLocality();
                    currentCity = currentCity.replace(" ", "-");
                    currentCity = currentCity.replace("'", "-");
                    callWebService(currentCity);
                    previousBestLocation = loc;

                } catch (Exception e) {
                    e.printStackTrace();
                    callWebService(null);
                }


            }
            public void onProviderDisabled(String provider)
            {
                Log.d("Provider", " No provider");
            }


            public void onProviderEnabled(String provider)
            {
                Log.d("Provider", "provider enabled");
            }


            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

        }
    }
}


