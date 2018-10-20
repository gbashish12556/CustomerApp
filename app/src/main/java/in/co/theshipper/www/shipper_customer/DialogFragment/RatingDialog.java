package in.co.theshipper.www.shipper_customer.DialogFragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import in.co.theshipper.www.shipper_customer.Activities.FullActivity;
import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.Fragments.FinishedBookingDetail;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.R;

/**
 * Created by Shubham on 14/07/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RatingDialog extends DialogFragment implements View.OnClickListener {

    public RequestQueue requestQueue;
    private String TAG = RatingDialog.class.getName();
    private EditText feedbackText;
    private RatingBar ratingBar;
    private Button submitButton;
    private float rating;
    private String booking_id, feedback,crn_no;
    private AlertDialog.Builder b;
    private Dialog rd;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        b = new AlertDialog.Builder(getActivity());
        b.setTitle("How Was Your Experience?");
        View v = getActivity().getLayoutInflater().inflate(R.layout.rating_dialog, null);
        b.setView(v);
        b.setCancelable(false);

        if((getArguments() != null)) {

            Bundle extras = getArguments();
            crn_no = extras.getString("crn_no");
            rating = Float.parseFloat(extras.getString("rating"));
            booking_id = extras.getString("booking_id");

        }

        feedbackText = (EditText) v.findViewById(R.id.feedbackText);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        ratingBar.setRating(rating);
        submitButton = (Button) v.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        rd = b.create();
        return rd;
    }

    @Override
    public void onClick(View v) {

        rating = ratingBar.getRating();
        feedback = feedbackText.getText().toString();

        if (rating == 0.0f) {

            if(getActivity() !=  null) {

                Toast.makeText(getActivity(), "Give a rating!!", Toast.LENGTH_SHORT).show();

            }

        }
        else {

            HashMap<String, String> hashMap = new HashMap<String, String>();
            String rating_url = Constants.Config.ROOT_PATH + "rate_driver";
            // String CrnNo = Helper.getPreference(getActivity(),"current_crn_no");

            if(getActivity() !=  null) {

                String user_token = Helper.getPreference(getActivity(), "user_token");

            }

            hashMap.put("booking_id", booking_id);
            hashMap.put("driver_rating", String.valueOf(rating));
            hashMap.put("customer_feedback", feedback);
            sendVolleyRequest(rating_url, Helper.checkParams(hashMap));

        }

    }

    public void sendVolleyRequest(String URL, final HashMap<String, String> hMap) {

        if(getActivity() !=  null) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    handleResponse(response);

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {


                }
            }) {

                @Override
                public HashMap<String, String> getParams() {
                    return hMap;
                }
            };

            stringRequest.setTag(TAG);
            Helper.addToRequestQue(requestQueue, stringRequest, getActivity());

        }

    }

    public void handleResponse(String response) {

        if(getActivity() !=  null) {

            if (!Helper.CheckJsonError(response)) {

                JSONObject jsonObject;

                try {

                    jsonObject = new JSONObject(response);
                    String errFlag = jsonObject.getString("errFlag");
                    String errMsg = jsonObject.getString("errMsg");

                    if (errFlag.equals("1")) {

                        Helper.ToastShort(getActivity(), "Error in submission");

                    } else {

                        Fragment fragment = new FinishedBookingDetail();
                        Bundle bundle = new Bundle();
                        bundle.putString("crn_no", crn_no);
                        fragment.setArguments(Helper.CheckBundle(bundle));
                        FragmentManager fragmentManager = FullActivity.fragmentManager;
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.main_content, fragment, Constants.Config.CURRENT_FRAG_TAG);

                        if ((FullActivity.homeFragmentIndentifier == -5)) {

                            transaction.addToBackStack(null);
                            FullActivity.homeFragmentIndentifier = transaction.commit();

                        } else {

                            transaction.commit();

                        }

                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_finished_booking_detail_fragment);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }

        }

    }

    public void show(FragmentManager childFragmentManager, String abc) {
        rd.show();
    }

    @Override
    public void dismiss() {

        super.dismiss();
        Helper.cancelAllRequest(requestQueue, TAG);

    }

}