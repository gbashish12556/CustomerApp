package in.co.theshipper.www.shipper_customer;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzir.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookingDetails extends Fragment implements View.OnClickListener{

    protected RequestQueue requestQueue;
    private String TAG = BookingDetails.class.getName();
    protected View view;
    private LinearLayout map_view,map;
    private Button callButton;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap=null;
    private boolean stopTimer = false;
    private Timer timer;
    private Location location;
    private String received_driver_current_lat,received_driver_current_lng, received_driver_token;
    private String crn_no="";
    private TextView location_datetime;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private ImageView driver_image,popup;
    private Dialog dialog;


    public BookingDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_booking_details, container, false);

        if (getActivity() != null) {

            // Inflate the layout for this fragment

            if ((getActivity().getIntent() != null) && (getActivity().getIntent().getExtras() != null)) {

                Bundle bundle = getActivity().getIntent().getExtras();
                crn_no = Fn.getValueFromBundle(bundle, "crn_no");
                getActivity().getIntent().setData(null);
                getActivity().setIntent(null);

            } else if (this.getArguments() != null) {

                Bundle bundle = this.getArguments();
                crn_no = Fn.getValueFromBundle(bundle, "crn_no");

            }

            map_view = (LinearLayout) view.findViewById(R.id.map_view);
            map = (LinearLayout) view.findViewById(R.id.map);
            location_datetime = (TextView) view.findViewById(R.id.location_datetime);
            driver_image = (ImageView) view.findViewById(R.id.driver_image);
            callButton = (Button) view.findViewById(R.id.driver_mobile_no);
            callButton.setOnClickListener(this);
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);
            dialog.setCancelable(true);
            popup = (ImageView) dialog.findViewById(R.id.image_popup);

        }

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity() != null) {

            mMapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map, mMapFragment, "MAP_FRAGMENT").commit();
            String booking_status_url = Constants.Config.ROOT_PATH + "get_booking_status";
            Fn.logD("booking_status_url", booking_status_url);
            HashMap<String, String> hashMap = new HashMap<String, String>();
            String user_token = Fn.getPreference(getActivity(), "user_token");
            hashMap.put("crn_no", crn_no);
            hashMap.put("user_token", user_token);
            sendVolleyRequest(booking_status_url, Fn.checkParams(hashMap), "booking_status");

        }

    }

    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap,final String method){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(method.equals("booking_status")) {

                    bookingStatusSuccess(response);

                }else if (method.equals("vehicle_location")) {

                    vehicleLocationSuccess(response);

                }else if(method.equals("draw_path")){

                    drawPath(response);

                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if(getActivity()!=null) {

                    Fn.ToastShort(getActivity(), Constants.Message.NETWORK_ERROR);
                }
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                return hMap;
            }
        };
        stringRequest.setTag(TAG);
        Fn.addToRequestQue(requestQueue, stringRequest, getActivity());
    }
    protected void bookingStatusSuccess(String response) {

        if (getActivity() != null)
        {
        if (!Fn.CheckJsonError(response)) {

            JSONObject jsonObject;
            JSONArray jsonArray;

            try {

                jsonObject = new JSONObject(response);
                String errFlag = jsonObject.getString("errFlag");

                if (errFlag.equals("0")) {

                    TextView textView = new TextView(getActivity());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(layoutParams);
                    textView.setId(R.id.large_text);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimensionPixelSize(R.dimen.large_text_size));
                    textView.setGravity(Gravity.CENTER);

                    if (jsonObject.has("likes")) {

                        jsonArray = jsonObject.getJSONArray("likes");
                        int count = 0;

                        while (count < jsonArray.length()) {

                            JSONObject JO = jsonArray.getJSONObject(count);
                            String received_is_cancelled = JO.getString("is_cancelled");
                            String received_is_active = JO.getString("is_active");
                            String booked_vehicle_id = JO.getString("booked_vehicle_id");

                            if (received_is_cancelled.equals("0") && received_is_active.equals("1") && (!booked_vehicle_id.equals("0"))) {

                                LinearLayout location_found_view = (LinearLayout) view.findViewById(R.id.location_found);
                                LinearLayout driver_found_view = (LinearLayout) view.findViewById(R.id.driver_found);
                                TextView driver_name_view = (TextView) view.findViewById(R.id.driver_name);
                                received_driver_token = JO.getString("driver_token");

                                Fn.putPreference(getActivity(), "driver_token", received_driver_token);

                                String received_driver_name = JO.getString("driver_name");
                                String received_driver_mobile_no = JO.getString("driver_mobile_no");
                                String received_driver_location_datetime = JO.getString("driver_location_datetime");
                                received_driver_current_lat = JO.getString("driver_location_lat");
                                received_driver_current_lng = JO.getString("driver_location_lng");
                                String driver_profile_pic = JO.getString("driver_profile_pic_url");
                                String profile_pic_url = Constants.Config.ROOT_PATH + driver_profile_pic;

                                if (driver_profile_pic.length() > 0) {

                                    downloadBitmapFromURL(profile_pic_url);

                                } else {

                                    driver_image.setImageResource(R.drawable.addcontact);
                                    driver_image.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            popup.setImageResource(R.drawable.addcontact);
                                            dialog.show();

                                        }
                                    });

                                }

                                location_datetime.setText("Last Seen: " + Fn.getDateName(received_driver_location_datetime));
                                location_found_view.setVisibility(View.VISIBLE);
                                driver_name_view.setText("Driver: " + received_driver_name);
                                callButton.setText(received_driver_mobile_no);
                                driver_found_view.setVisibility(View.VISIBLE);
                                location_found_view.setVisibility(View.VISIBLE);
                                map.setVisibility(View.VISIBLE);
                                map_view.setVisibility(View.GONE);

                                setUpMapIfNeeded();
                                TimerProgramm();

                            } else {

                                textView.setText(Constants.Message.VEHICLE_ALLOCATION_PENDING);
                                map_view.addView(textView);

                            }

                            count++;

                        }

                    } else {

                        textView.setText(Constants.Message.NO_CURRENT_BOOKING);
                        map_view.addView(textView);

                    }

                }

            } catch (JSONException e) {

                e.printStackTrace();

            }

        } else {

            ErrorDialog(Constants.Title.SERVER_ERROR, Constants.Message.SERVER_ERROR);

        }

    }

    }

    protected void vehicleLocationSuccess(String response){

        if(!Fn.CheckJsonError(response)){

            JSONObject jsonObject;
            JSONArray jsonArray;

            try {

                jsonObject = new JSONObject(response);
                String errFlag = jsonObject.getString("errFlag");
                if(errFlag.equals("0"))
                {

                    if(jsonObject.has("likes"))
                    {

                        jsonArray = jsonObject.getJSONArray("likes");
                        int count = 0;

                        while (count < jsonArray.length())
                        {

                            JSONObject JO = jsonArray.getJSONObject(count);
                            received_driver_current_lat = JO.getString("driver_location_lat");
                            received_driver_current_lng = JO.getString("driver_location_lng");
                            String received_driver_location_datetime = JO.getString("driver_location_datetime");
                            location_datetime.setText("Last Seen: "+Fn.getDateName(received_driver_location_datetime));
                            map.setVisibility(View.VISIBLE);
                            map_view.setVisibility(View.GONE);
                            Fn.logD("LocationSuccessCallingMap","LocationSuccessCallingMap");
                            setUpMapIfNeeded();
                            count++;

                        }

                    }

                }

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }else{

            if(getActivity()!=null) {

                Fn.ToastShort(getActivity(), Constants.Message.SERVER_ERROR);

            }

        }

    }
    @Override
    public void onClick(View v) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + callButton.getText().toString()));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);

    }

    public void TimerProgramm() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {

                        if (!stopTimer) {

                            String driver_location_url = Constants.Config.ROOT_PATH + "get_driver_location";
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("driver_token", received_driver_token);
                            sendVolleyRequest(driver_location_url, hashMap, "vehicle_location");

                        }

                    }
                });

            }
        }, Constants.Config.GET_DRIVER_LOCATION_DELAY, Constants.Config.GET_DRIVER_LOCATION_PERIOD);

    }

    private void setUpMapIfNeeded() {

        if(getActivity() != null) {

            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap != null) {
                mMap.clear();
                mMap = null;
            }

            if (mMap == null) {

                //            Try to obtain the map from the SupportMapFragment.

                mMap = mMapFragment.getMap();
                Fn.logD("map_not_null", String.valueOf(mMap));

                // Check if we were successful in obtaining the map.

                if (mMap != null) {

                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    mMap.setMyLocationEnabled(true);

                    if (FullActivity.mGoogleApiClient.isConnected()) {

                        do {

                            location = Fn.getAccurateCurrentlocation(FullActivity.mGoogleApiClient, getActivity());

                        } while (location == null);

                        if (location != null) {

                            double current_lat = location.getLatitude();
                            double current_lng = location.getLongitude();
                            LatLng latlng = new LatLng(Double.parseDouble(received_driver_current_lat), Double.parseDouble(received_driver_current_lng));// This methods gets the users current longitude and latitude.
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));//Moves the camera to users current longitude and latitude
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, Constants.Config.MAP_HIGH_ZOOM_LEVEL));//Animates camera and zooms to preferred state on the user's current location.

                            try {

                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(received_driver_current_lat), Double.parseDouble(received_driver_current_lng))).title("Driver"));

                                //                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble("22.6256"), Double.parseDouble("88.3576"))).title("Driver"));
                            } catch (NumberFormatException e) {

                                e.printStackTrace();

                            }

                            String url = makeURL(received_driver_current_lat, received_driver_current_lng, String.valueOf(current_lat), String.valueOf(current_lng));
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            sendVolleyRequest(url, Fn.checkParams(hashMap), "draw_path");

                        }

                    }

                }

            }

        }

    }

    public String makeURL(String sourceLat, String sourceLng, String destLat,String destLng){

        StringBuilder urlString = new StringBuilder();

        try {

            urlString.append("https://maps.googleapis.com/maps/api/directions/json");
            urlString.append("?origin=");// from
            urlString.append(URLEncoder.encode(sourceLat,"UTF-8"));
            urlString.append(",");
            urlString.append(URLEncoder.encode(sourceLng,"UTF-8"));
            urlString.append("&destination=");// to
            urlString.append(URLEncoder.encode(destLat,"UTF-8"));
            urlString.append(",");
            urlString.append(URLEncoder.encode(destLng,"UTF-8"));
            urlString.append("&sensor=false&mode=driving&alternatives=true");
            urlString.append("&key=" + URLEncoder.encode(getResources().getString(R.string.server_APIkey1), "UTF-8"));

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return urlString.toString();

    }

    public void drawPath(String  result) {

        try {

            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );
            JSONArray legsArray = routes.getJSONArray("legs");
            JSONObject legs = legsArray.getJSONObject(0);
            JSONObject distance = legs.getJSONObject("distance");
            String distance_km  = distance.getString("text");
            JSONObject duration = legs.getJSONObject("duration");
            String duration_min  = duration.getString("text");

            if(getActivity() != null) {

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(duration_min + " ( " + distance_km + " ) ");

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {

            int b, shift = 0, result = 0;

            do {

                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;

            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {

                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;

            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);

        }

        return poly;

    }

    private void ErrorDialog(String Title,String Message){

        if(getActivity() != null) {

            Fn.showDialog(getActivity(), Title, Message);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(getActivity() != null) {

            switch (requestCode) {

                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:

                    switch (resultCode) {

                        case Activity.RESULT_OK:
                            break;
                        case Activity.RESULT_CANCELED:
                            Fn.showGpsAutoEnableRequest(FullActivity.mGoogleApiClient, getActivity());//keep asking if imp or do whatever
                            break;

                    }

                    break;

            }

        }

    }
    protected void downloadBitmapFromURL(String profile_pic_url){

        if(getActivity() != null) {

            //        RequestQueue requestQueue;
            final Bitmap[] return_param = new Bitmap[1];
            ImageRequest imageRequest = new ImageRequest(profile_pic_url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(final Bitmap response) {
                    driver_image.setImageBitmap(response);
                    driver_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popup.setImageBitmap(response);
                            dialog.show();
                        }
                    });
//                driverimage = response;
                }
            }, 0, 0, null, null);

            imageRequest.setTag(TAG);
            Fn.addToRequestQue(requestQueue, imageRequest, getActivity()

            );

        }

    }

    //start of extra method
    @Override
    public void onResume() {
        super.onResume();

        Fn.startAllVolley(requestQueue);
        stopTimer = false;

    }

    @Override
    public void onPause() {
        super.onPause();

        Fn.stopAllVolley(requestQueue);
        stopTimer = true;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(timer != null){
            timer.cancel();
            timer = null;
        }

        if(mMap != null){
            mMap = null;
        }

        Fn.cancelAllRequest(requestQueue, TAG);

    }

}
