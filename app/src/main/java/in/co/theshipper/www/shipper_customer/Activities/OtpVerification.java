package in.co.theshipper.www.shipper_customer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;

import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.R;
import in.co.theshipper.www.shipper_customer.Utils.FormValidation;


public class OtpVerification extends AppCompatActivity {

    private  String TAG = OtpVerification.class.getName();
    public RequestQueue requestQueue;
    private  EditText otp_value;
    private String entered_otp;
    private String OTP;
    private Bundle b;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otp_value = (EditText) findViewById(R.id.editText2);
        if(getIntent().getExtras() != null) {

            b = getIntent().getExtras();
            OTP = String.valueOf(b.getInt("OTP"));

        }

    }

    public void verifyOtp(View view){

        if(checkValidation()) {

            entered_otp = otp_value.getText().toString();

            if (OTP.equals(entered_otp)) {

                String mobile_no = "";
                String get_user_info_url = Constants.Config.ROOT_PATH + "get_customer_info";
                mobile_no = Helper.getPreference(this, "mobile_no");
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("mobile_no", mobile_no);
                sendVolleyRequest(get_user_info_url,Helper.checkParams(hashMap));

            } else {

                Helper.showDialog(this,Constants.Title.OTP_VERIFICATION_ERROR,Constants.Message.OTP_VERIFICATION_ERROR);

            }

        }
        else {

           Helper.Toast(this, Constants.Message.FORM_ERROR);

        }

    }

    private boolean checkValidation() {

        boolean ret = true;

        if (!FormValidation.isValidOTP(otp_value, true)) ret = false;
        return ret;

    }

    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                OtpVerificationSuccess(response);

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorDialog(Constants.Title.NETWORK_ERROR,Constants.Message.NETWORK_ERROR);

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

    private void ErrorDialog(String Title,String Message){
        Helper.showDialog(this, Title, Message);
    }

    public void OtpVerificationSuccess(String response){

        if(!Helper.CheckJsonError(response)){

            Intent intent = new Intent(this, EditProfile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("JSON_STRING", response);
            startActivity(intent);

        }else{

            ErrorDialog(Constants.Title.SERVER_ERROR,Constants.Message.SERVER_ERROR);

        }

    }

}
