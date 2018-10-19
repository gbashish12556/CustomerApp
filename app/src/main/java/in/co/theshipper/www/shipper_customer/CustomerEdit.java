package in.co.theshipper.www.shipper_customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

/**
 * Created by Shubham on 24/06/2016.
 */
public class CustomerEdit extends Fragment{

    protected  String TAG = CustomerEdit.class.getName();
    protected RequestQueue requestQueue;
    private View view;
    private EditText name,email,address;
    Button updateButton;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    public CustomerEdit(){

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_customer_edit, container, false);
        name = (EditText) view.findViewById(R.id.name);
        email = (EditText) view.findViewById(R.id.email);
        address = (EditText) view.findViewById(R.id.address);
        updateButton = (Button)view.findViewById(R.id.frag_next_button);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() != null) {

            String mobile_no = "";
            String get_user_info_url = Constants.Config.ROOT_PATH + "get_customer_info";
            mobile_no = Fn.getPreference(getActivity(), "mobile_no");
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("mobile_no", mobile_no);
            sendVolleyRequest(get_user_info_url, Fn.checkParams(hashMap), "get_info");
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (checkValidation()) {

                        String username = name.getText().toString();
                        String useremail = email.getText().toString();
                        String useraddress = address.getText().toString();
                        String edit_customer_profile_url = Constants.Config.ROOT_PATH + "edit_customer_profile";
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("name", username);
                        hashMap.put("email", useremail);
                        hashMap.put("postal_address", useraddress);
                        hashMap.put("user_token", Fn.getPreference(getActivity(), "user_token"));
                        sendVolleyRequest(edit_customer_profile_url, Fn.checkParams(hashMap), "edit_info");

                    } else {

                        Fn.ToastShort(getActivity(), Constants.Message.FORM_ERROR);

                    }
                }
            });
        }
    }
    public void sendVolleyRequest(String URL, final HashMap<String,String> hMap,final String method){
        if(getActivity() != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    if (method.equals("get_info")) {

                        setValues(response);

                    } else if (method.equals("edit_info")) {

                        editProfileSuccess(response);

                    }

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    ErrorDialog(Constants.Title.NETWORK_ERROR, Constants.Message.NETWORK_ERROR);

                }

            }) {

                @Override
                protected HashMap<String, String> getParams() {
                    return hMap;
                }

            };

            stringRequest.setTag(TAG);
            Fn.addToRequestQue(requestQueue, stringRequest, getActivity());

        }

    }
    private void setValues(String response){

        try {

            if(!Fn.CheckJsonError(response)){

            JSONObject jsonObject = new JSONObject(response);
            String errFlag = jsonObject.getString("errFlag");
            String errMsg = jsonObject.getString("errMsg");

            if(errFlag.equals("0")) {

                if (jsonObject.has("likes")) {

                    jsonArray = jsonObject.getJSONArray("likes");
                    int count = 0;

                    while (count < jsonArray.length()) {

                        JSONObject JO = jsonArray.getJSONObject(count);
                        String received_username = JO.getString("name");
                        String received_useremail = JO.getString("email");
                        String received_useraddress = JO.getString("postal_address");
                        name.setText(received_username);
                        email.setText(received_useremail);
                        address.setText(received_useraddress);
                         count++;

                    }

                } else {


                }

            }else{

                ErrorDialog(Constants.Title.SERVER_ERROR,Constants.Message.SERVER_ERROR);

            }

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

    private boolean checkValidation() {

        boolean ret = true;
        if (!FormValidation.isEmailAddress(email, true)) ret = false;
        if (!FormValidation.isRequired(name,Constants.Config.NAME_FIELD_LENGTH)) ret = false;
        if(!FormValidation.isRequired(address, Constants.Config.ADDRESS_FIELD_LENGTH)) ret = false;
        return ret;

    }
    public void editProfileSuccess(String response) {

        if(getActivity() != null) {

            if (!Fn.CheckJsonError(response)) {

                 Intent intent = new Intent(getActivity(), FullActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } else {

                ErrorDialog(Constants.Title.SERVER_ERROR, Constants.Message.SERVER_ERROR);

            }

        }

    }

    private void ErrorDialog(String Title,String Message){

        if(getActivity() != null) {

            Fn.showDialog(getActivity(), Title, Message);

        }

    }

    @Override
    public void onPause() {
        super.onPause();

        Fn.stopAllVolley(requestQueue);

    }

    @Override
    public void onResume() {
        super.onResume();

        Fn.startAllVolley(requestQueue);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Fn.cancelAllRequest(requestQueue,TAG);

    }
}
