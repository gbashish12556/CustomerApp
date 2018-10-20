package in.co.theshipper.www.shipper_customer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.Utils.FormValidation;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.R;
import in.co.theshipper.www.shipper_customer.Service.RegistrationIntentService;


public class EditProfile extends AppCompatActivity {

    public  String TAG = EditProfile.class.getName();
    public RequestQueue requestQueue;
    public HashMap<String,String> hashMap;
    EditText name,email,address;
    String username,useremail,useraddress,received_username,received_useremail,received_useraddress,received_usertoken,json_string,errFlag,errMsg;
    JSONObject  jsonObject;
    JSONArray jsonArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.address);
        json_string = getIntent().getStringExtra("JSON_STRING");

        try {

            jsonObject = new JSONObject(json_string);
            errFlag = jsonObject.getString("errFlag");
            errMsg = jsonObject.getString("errMsg");

            if(errFlag.equals("0"))
            {

                if(jsonObject.has("likes")) {

                    jsonArray = jsonObject.getJSONArray("likes");
                    int count = 0;

                    while (count < jsonArray.length())
                    {

                        JSONObject JO = jsonArray.getJSONObject(count);
                        received_username = JO.getString("name");
                        received_usertoken = JO.getString("user_token");
                        received_useremail = JO.getString("email");
                        received_useraddress = JO.getString("postal_address");

                        if(received_username.length()>0) {
                            name.setText(received_username);
                        }

                        if(received_useremail.length()>0) {
                            email.setText(received_useremail);
                        }

                        if(received_useraddress.length()>0) {
                            address.setText(received_useraddress);
                        }

                        String stored_usertoken = Helper.getPreference(this,"user_token");
                        Helper.logD("stored_usertoken",stored_usertoken);
                        count++;

                    }

                }

                else
                {

                    Helper.Toast(this,Constants.Message.NEW_USER_ENTER_DETAILS);

                }

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    public void editProfile(View view)
    {

        if(checkValidation()) {

            username = name.getText().toString();
            useremail = email.getText().toString();
            useraddress = address.getText().toString();
            String edit_customer_profile_url = Constants.Config.ROOT_PATH + "edit_customer_profile";
            hashMap = new HashMap<String, String>();
            hashMap.put("name", username);
            hashMap.put("email", useremail);
            hashMap.put("postal_address", useraddress);
            hashMap.put("user_token", received_usertoken);
            sendVolleyRequest(edit_customer_profile_url,Helper.checkParams(hashMap));

        }
        else{

            Toast.makeText(EditProfile.this, "Form Contains Error", Toast.LENGTH_SHORT).show();

        }

    }

    private boolean checkValidation() {

        boolean ret = true;
        if (!FormValidation.isEmailAddress(email, true)) ret = false;
        if (!FormValidation.isRequired(name,Constants.Config.NAME_FIELD_LENGTH)) ret = false;
        if(!FormValidation.isRequired(address,Constants.Config.ADDRESS_FIELD_LENGTH)) ret = false;
        return ret;

    }

    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                editProfileSuccess(response);

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);

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

    public void editProfileSuccess(String response){

        if (!Helper.CheckJsonError(response)) {

            Intent i = new Intent(this, RegistrationIntentService.class);
            startService(i);
            Helper.putPreference(this,"user_token",received_usertoken);
            Intent intent = new Intent(this, FullActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else {

            ErrorDialog(Constants.Title.SERVER_ERROR,Constants.Message.SERVER_ERROR);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Helper.cancelAllRequest(requestQueue,TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        Helper.startAllVolley(requestQueue);
    }

    @Override
    public void onStop() {
        super.onStop();
        Helper.stopAllVolley(requestQueue);
    }
}
