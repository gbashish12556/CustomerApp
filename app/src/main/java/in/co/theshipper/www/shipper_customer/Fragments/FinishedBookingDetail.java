package in.co.theshipper.www.shipper_customer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.DialogFragment.RatingDialog;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishedBookingDetail extends Fragment{

    public RequestQueue requestQueue;
    private String TAG = FinishedBookingDetail.class.getName();
    public View view;
    public Context context;
    private TextView received_pickup_point_view,received_dropoff_point_view, received_crn_no_view, received_booking_datetime_view,received_driver_name_view, received_driver_mobile_no_view, received_truck_name_view,received_total_fare_view;
    private ImageView drivericon,vehicleicon,popup;
    private Dialog dialog;
    private String booking_id,received_crn_no;
    private TextView ratingText;
    private RatingBar ratingBar;
    private String crn_no="";
    private float driver_rating;

    public FinishedBookingDetail() {
        // Required empty public constructor
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {

            return null;

        } else {

            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_finished_booking_detail, container, false);
            received_pickup_point_view=(TextView)view.findViewById(R.id.pickup_point);
            received_dropoff_point_view=(TextView)view.findViewById(R.id.dropoff_point);
            received_booking_datetime_view=(TextView)view.findViewById(R.id.booking_datetime);
            received_truck_name_view=(TextView)view.findViewById(R.id.vehicle_type);
            received_driver_name_view=(TextView)view.findViewById(R.id.driver_name);
            received_driver_mobile_no_view=(TextView)view.findViewById(R.id.driver_mobile_no);
            received_crn_no_view=(TextView)view.findViewById(R.id.crn_no);
            received_total_fare_view=(TextView)view.findViewById(R.id.total_fare);
            ratingText = (TextView)view.findViewById(R.id.ratingText);
            ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);
            dialog.setCancelable(true);
            drivericon=(ImageView)view.findViewById(R.id.driver_imageview);
            vehicleicon=(ImageView)view.findViewById(R.id.vehicleimage);
            popup=(ImageView)dialog.findViewById(R.id.image_popup);

            if((getActivity().getIntent()!=null)&&(getActivity().getIntent().getExtras()!=null)) {

                Bundle bundle = getActivity().getIntent().getExtras();
                crn_no = Helper.getValueFromBundle(bundle,"crn_no");
                getActivity().getIntent().setData(null);
                getActivity().setIntent(null);

            }else if(this.getArguments()!=null) {

                Bundle bundle = this.getArguments();
                crn_no = Helper.getValueFromBundle(bundle, "crn_no");

            }
            return view;

        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HashMap<String,String>  hashMap= new HashMap<String,String>();
        String booking_status_url = Constants.Config.ROOT_PATH+"get_completed_booking_status";
        String user_token = Helper.getPreference(getActivity(),"user_token");
        hashMap.put("crn_no", crn_no);
        hashMap.put("user_token", user_token);
        sendVolleyRequest(booking_status_url, Helper.checkParams(hashMap));

    }

    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Helper.logD("onResponse_booking_status", String.valueOf(response));
                bookingStatusSuccess(response);

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }){

            @Override
            public HashMap<String,String> getParams(){
                return hMap;
            }
        };

        stringRequest.setTag(TAG);
        Helper.addToRequestQue(requestQueue, stringRequest, getActivity());

    }

    public void bookingStatusSuccess(String response){

        if(!Helper.CheckJsonError(response)){

            JSONObject jsonObject;
            JSONArray jsonArray;

            try {

                jsonObject = new JSONObject(response);
                String errFlag = jsonObject.getString("errFlag");
                String errMsg = jsonObject.getString("errMsg");

                if(errFlag.equals("0"))
                {
                    if(jsonObject.has("likes"))
                    {
                        jsonArray = jsonObject.getJSONArray("likes");
                        int count = 0;

                        while (count < jsonArray.length())
                        {

                            JSONObject JO = jsonArray.getJSONObject(count);
                            String received_exact_pickup_point = JO.getString("exact_pickup_point");
                            String received_exact_dropoff_point = JO.getString("exact_dropoff_point");
                            final String received_vehicletype_id = JO.getString("vehicletype_id");
                            String received_booking_datetime = Helper.getDateName(JO.getString("booking_datetime"));
                            String received_total_fare = JO.getString("total_fare");
                            received_crn_no = JO.getString("crn_no");
                            String received_truck_name = Helper.VehicleName(received_vehicletype_id, getActivity());
                            String received_driver_name = JO.getString("driver_name");
                            String received_driver_mobile_no = JO.getString("driver_mobile_no");
                            booking_id = JO.getString("booking_id");
                            driver_rating = Float.parseFloat(JO.getString("driver_rating"));

                            if(driver_rating == 0.0f){

                                handleRating();

                            }
                            else{

                                ratingText.setText("You Rated");
                                ratingBar.setRating(driver_rating);
                                ratingBar.setIsIndicator(true);

                            }

                            received_pickup_point_view.setText(received_exact_pickup_point);
                            received_driver_mobile_no_view.setText(received_driver_mobile_no);
                            received_dropoff_point_view.setText(received_exact_dropoff_point);
                            received_booking_datetime_view.setText(received_booking_datetime);
                            received_driver_name_view.setText(received_driver_name);
                            received_crn_no_view.setText(received_crn_no);
                            received_truck_name_view.setText(received_truck_name);
                            received_total_fare_view.setText(received_total_fare + " Rs");
                            String driver_profile_pic_url=JO.getString("driver_profile_pic_url");
                            String profile_pic_url = Constants.Config.ROOT_PATH+driver_profile_pic_url;

                            if(driver_profile_pic_url.length()>0){

                                downloadBitmapFromURL(profile_pic_url);

                            }else{

                                drivericon.setImageResource(R.drawable.addcontact);
                                drivericon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popup.setImageResource(R.drawable.addcontact);
                                        dialog.show();
                                    }
                                });

                            }

                            vehicleicon.setImageResource(Helper.getVehicleImage(Integer.parseInt(received_vehicletype_id)));
                            vehicleicon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    popup.setImageResource(Helper.getVehicleImage(Integer.parseInt(received_vehicletype_id)));
                                    dialog.show();

                                }
                            });

                            count++;

                        }

                    }

                }

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

    }

    public void handleRating(){

        ratingText.setText("Rate your driver");
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                RatingDialog rd = new RatingDialog();
                Bundle bundle = new Bundle();
                bundle.putString("rating", String.valueOf(rating));
                bundle.putString("booking_id", booking_id);
                bundle.putString("crn_no", received_crn_no);
                Helper.SystemPrintLn("booking_id" + booking_id + "rating" + rating + "crn_no" + crn_no);
                rd.setArguments(Helper.CheckBundle(bundle));
                rd.show(getActivity().getFragmentManager(), "ABC");

            }
        });

    }
    public void downloadBitmapFromURL(String profile_pic_url){

        //        RequestQueue requestQueue;
        final Bitmap[] return_param = new Bitmap[1];
        ImageRequest imageRequest = new ImageRequest(profile_pic_url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(final Bitmap response) {
                drivericon.setImageBitmap(response);
                drivericon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.setImageBitmap(response);
                        dialog.show();
                    }
                });
            }
        }, 0, 0, null, null);
        imageRequest.setTag(TAG);
        Helper.addToRequestQue(requestQueue, imageRequest, getActivity()
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        Helper.stopAllVolley(requestQueue);
    }

    @Override
    public void onResume() {
        super.onResume();
        Helper.startAllVolley(requestQueue);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Helper.cancelAllRequest(requestQueue,TAG);
    }
}
