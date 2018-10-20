package in.co.theshipper.www.shipper_customer.Fragments;

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

import in.co.theshipper.www.shipper_customer.Activities.CompleteActivity;
import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.R;
import in.co.theshipper.www.shipper_customer.Utils.EndlessScrollListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FutureBooking extends Fragment  {

    ListView list;
    ListAdapter listAdapter;
    private String TAG = FutureBooking.class.getName();
    public RequestQueue requestQueue;
    ArrayList<HashMap<String,String>> values = new ArrayList<HashMap<String, String>>();
    public FutureBooking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {

            return null;

        } else {

            View v = inflater.inflate(R.layout.fragment_future_booking, container, false);
            list = (ListView) v.findViewById(R.id.future_booking_list);
            return v;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        listAdapter = new SimpleAdapter(getContext(),values,R.layout.booking_list_view,new String[] {"crn_no","vehicle_type","datetime1","pickup_point","dropoff_point","vehicle_image"},
                new int[] {R.id.crn_no,R.id.vehicle_type, R.id.datetime1,R.id.pickup_point,R.id.dropoff_point,R.id.vehicle_image});
        list.setAdapter(listAdapter);
        createRequest(0);

        list.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page) {

                createRequest(page);
            }
        });

    }

    private void createRequest(final int page_no){

        if(getActivity() !=  null) {

            final String user_token = Helper.getPreference(getActivity(), "user_token");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.Config.ROOT_PATH + "future_booking_list",

                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            uiUpdate(response);

                        }

                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Helper.ToastShort(getActivity(), Constants.Message.NETWORK_ERROR);

                        }

                    }) {

                @Override
                public HashMap<String, String> getParams() {

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user_token", user_token);
                    params.put("page_no", String.valueOf(page_no));
                    return Helper.checkParams(params);

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

                if (!Helper.CheckJsonError(response)) {

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
                            qvalues.put("vehicle_type", Helper.VehicleName(UpdationObject.get("vehicletype_id").toString(), getActivity()));
                            qvalues.put("datetime1", Helper.getDateName(UpdationObject.get("datetime1").toString()));
                            qvalues.put("pickup_point", UpdationObject.get("pickup_point").toString());
                            qvalues.put("dropoff_point", UpdationObject.get("dropoff_point").toString());
                            qvalues.put("vehicle_image", Integer.toString(Helper.getVehicleImage(Integer.parseInt(UpdationObject.get("vehicletype_id").toString()))));
                            values.add(qvalues);
                            count++;

                        }

                    }

                } else {

                    Helper.ToastShort(getActivity(), Constants.Message.SERVER_ERROR);

                }

            } catch (Exception e) {

                // handle exception
            }

            ((BaseAdapter) listAdapter).notifyDataSetChanged();

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    View child_view = list.getChildAt(position - list.getFirstVisiblePosition());
                    TextView crn_no = (TextView) child_view.findViewById(R.id.crn_no);
                    Fragment fragment = new Fragment();
                    fragment = new BookingDetails();
                    Bundle bundle = new Bundle();
                    bundle.putString("crn_no", crn_no.getText().toString());
                    fragment.setArguments(Helper.CheckBundle(bundle));
                    FragmentManager fragmentManager = CompleteActivity.fragmentManager;
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.main_content, fragment, Constants.Config.CURRENT_FRAG_TAG);

                    if ((CompleteActivity.homeFragmentIndentifier == -5)) {

                        transaction.addToBackStack(null);
                        CompleteActivity.homeFragmentIndentifier = transaction.commit();

                    } else {

                        transaction.commit();

                    }

                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_future_booking_detail_fragment);

                }
            });

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Helper.startAllVolley(requestQueue);

    }

    @Override
    public void onPause() {
        super.onPause();

        Helper.startAllVolley(requestQueue);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Helper.cancelAllRequest(requestQueue, TAG);

    }

}
