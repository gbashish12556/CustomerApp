package in.co.theshipper.www.shipper_customer.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import in.co.theshipper.www.shipper_customer.Activities.CompleteActivity;
import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.R;

/**
 * Create this Class from tutorial :
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial
 *
 * For Geocoder read this : http://stackoverflow.com/questions/472313/android-reverse-geocoding-getfromlocation
 *
 */

public class GpsTracker extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    // Get Class Name
    public RequestQueue requestQueue;
    public HashMap<String,String> hashMap;
    private String TAG = GpsTracker.class.getName();
    Location location;
    private String user_token ;
    private GoogleApiClient mGoogleApiClient;
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        user_token = Helper.getPreference(this,"user_token");

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mGoogleApiClient.connect();
        Intent i = new Intent(this, CompleteActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder.setContentIntent(pi)
                .setSmallIcon(R.drawable.vehicle_1).setTicker("SHIPPER").setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle("SHIIPER")
                .setContentText("Location Tracking in Progress").build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(1317, notification);
        TimerProgramm();
        return (START_NOT_STICKY);

    }

    @Override
    public void onDestroy() {

        if(mGoogleApiClient.isConnected()){

            mGoogleApiClient.disconnect();

        }

        stopForeground(true);

        if(timer != null) {

            timer.cancel();
            timer = null;

        }


    }
    public void TimerProgramm(){

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                getLocation();

            }

        }, Constants.Config.UPDATE_CUSTOMER_LOCATION_DELAY, Constants.Config.UPDATE_CUSTOMER_LOCATION_PERIOD);

    }
    /**
     * Try to get my current location by GPS or Network Provider
     */
    public void getLocation() {

        if(mGoogleApiClient.isConnected()){

            do{

                location = Helper.getAccurateCurrentlocationService(mGoogleApiClient,this);

            }while(location == null);

            updateGPSCoordinates();
        }
    }
    public void updateGPSCoordinates() {

        if (location != null) {

            String update_location_url = Constants.Config.ROOT_PATH+"update_customer_location";
            String lattitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("location_lat", lattitude);
            hashMap.put("location_lng", longitude);
            hashMap.put("user_token", user_token);
            Helper.logD("latitude", lattitude);
            Helper.logD("longitude",longitude);
            sendVolleyRequest(update_location_url,Helper.checkParams(hashMap));

        }

    }

    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Helper.ToastShort(getApplicationContext(), Constants.Message.TRACKING_ERROR);

            }

        }){

            @Override
            public HashMap<String,String> getParams(){
                return hMap;
            }

        };

        stringRequest.setTag(TAG);
        Helper.addToRequestQue(requestQueue, stringRequest, this);

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}