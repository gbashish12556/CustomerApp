package in.co.theshipper.www.shipper_customer;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinishedBooking extends Fragment {

    ListView list;
    ListAdapter listAdapter;
    private String TAG = FinishedBooking.class.getName();
    protected RequestQueue requestQueue;
    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String, String>>();

    public FinishedBooking() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {

            return null;

        } else {

            View v = inflater.inflate(R.layout.fragment_finished_booking, container, false);
            list = (ListView) v.findViewById(R.id.finished_booking_list);
            // return inflater.inflate(R.layout.fragment_finished_booking, container, false);
            listAdapter = new SimpleAdapter(getContext(),values,R.layout.booking_list_view,new String[] {"crn_no","vehicle_type","datetime1","pickup_point","dropoff_point","vehicle_image"},
                    new int[] {R.id.crn_no,R.id.vehicle_type, R.id.datetime1,R.id.pickup_point,R.id.dropoff_point,R.id.vehicle_image});
            list.setAdapter(listAdapter);
            return v;
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createRequest(0);
        list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page) {

                createRequest(page);
                //return true;
            }
        });

    }

    private void createRequest(final int page_no){

        if(getActivity() !=  null) {

            final String user_token = Fn.getPreference(getActivity(), "user_token");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Config.ROOT_PATH + "completed_booking_list",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            uiUpdate(response);
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Fn.ToastShort(getActivity(), Constants.Message.NETWORK_ERROR);

                        }

                    }) {

                @Override
                protected HashMap<String, String> getParams() {

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user_token", user_token);
                    params.put("page_no", String.valueOf(page_no));
                    return Fn.checkParams(params);

                }

            };

            requestQueue = Volley.newRequestQueue(getContext());
            stringRequest.setTag(TAG);
            requestQueue.add(stringRequest);

        }

    }

    private void uiUpdate(String response)
    {
        if(getActivity() !=  null) {

            try {

                if (!Fn.CheckJsonError(response)) {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject UpdationObject;
                    JSONArray jsonArray;

                    if (jsonObject.has("likes")) {

                        jsonArray = jsonObject.getJSONArray("likes");
                        int count = 0;

                        while (count < jsonArray.length()) {

                            UpdationObject = jsonArray.getJSONObject(count);
                            HashMap<String, String> qvalues = new HashMap<String, String>();
                            qvalues.put("crn_no", UpdationObject.get("crn_no").toString());
                            qvalues.put("vehicle_type", Fn.VehicleName(UpdationObject.get("vehicletype_id").toString(), getActivity()));
                            qvalues.put("datetime1", Fn.getDateName(UpdationObject.get("datetime1").toString()));
                            qvalues.put("pickup_point", UpdationObject.get("pickup_point").toString());
                            qvalues.put("dropoff_point", UpdationObject.get("dropoff_point").toString());
                            qvalues.put("vehicle_image", Integer.toString(Fn.getVehicleImage(Integer.parseInt(UpdationObject.get("vehicletype_id").toString()))));
                            values.add(qvalues);
                            count++;

                        }

                    }

                } else {

                    Fn.ToastShort(getActivity(), Constants.Message.SERVER_ERROR);

                }

            } catch (Exception e) {
            }

            ((BaseAdapter) listAdapter).notifyDataSetChanged();

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    View child_view = list.getChildAt(position - list.getFirstVisiblePosition());
                    TextView crn_no = (TextView) child_view.findViewById(R.id.crn_no);
                    Fragment fragment = new FinishedBookingDetail();
                    Bundle bundle = new Bundle();
                    bundle.putString("crn_no", crn_no.getText().toString());
                    fragment.setArguments(Fn.CheckBundle(bundle));
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
            });

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

        Fn.cancelAllRequest(requestQueue, TAG);

    }

}


