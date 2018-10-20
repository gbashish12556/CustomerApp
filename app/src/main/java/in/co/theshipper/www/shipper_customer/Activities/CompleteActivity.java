package in.co.theshipper.www.shipper_customer.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import in.co.theshipper.www.shipper_customer.Constants;
import in.co.theshipper.www.shipper_customer.Fragments.About;
import in.co.theshipper.www.shipper_customer.Fragments.Book;
import in.co.theshipper.www.shipper_customer.Fragments.BookingDetails;
import in.co.theshipper.www.shipper_customer.Fragments.BookingStatus;
import in.co.theshipper.www.shipper_customer.Fragments.CustomerEdit;
import in.co.theshipper.www.shipper_customer.Fragments.EmergencyContact;
import in.co.theshipper.www.shipper_customer.Fragments.FinishedBookingDetail;
import in.co.theshipper.www.shipper_customer.Service.GpsTracker;
import in.co.theshipper.www.shipper_customer.Helper;
import in.co.theshipper.www.shipper_customer.Utils.NavMenu;
import in.co.theshipper.www.shipper_customer.Adapter.NavMenuAdapter;
import in.co.theshipper.www.shipper_customer.R;
import in.co.theshipper.www.shipper_customer.Fragments.RateCard;

public class CompleteActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,AdapterView.OnItemClickListener{

    public  String TAG = CompleteActivity.class.getName();
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] NavList,TitleList;
    private ActionBarDrawerToggle drawerListener;
    public static FragmentManager fragmentManager;
    private Fragment fragment;
    private int vehicle_type;
    public static int homeFragmentIndentifier = -5;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static GoogleApiClient mGoogleApiClient;
    private String method = "",fragment_title = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        }

        if((getIntent() != null)&&(getIntent().getExtras() != null)) {

            Bundle bundle = getIntent().getExtras();
            fragment_title = Helper.getValueFromBundle(bundle,"menuFragment");
            method = Helper.getValueFromBundle(bundle, "method");

        }

        int item = 0;

        if (fragment_title.equals("BookingDetails")) {

            item = 7;

        }else if (fragment_title.equals("FinishedBookingDetails")) {

            item = 8;
        }

        fragmentManager = getSupportFragmentManager();
        if (null == savedInstanceState) {

            selectItem(item);

        }

        getSupportActionBar().setLogo(R.drawable.vehicle_1);
        getSupportActionBar().setHomeButtonEnabled(true);

        try {

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } catch (Exception e) {

            e.printStackTrace();

        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listView = (ListView) findViewById(R.id.nav_menu);
        NavList = getResources().getStringArray(R.array.nav_menu);
        TitleList = getResources().getStringArray(R.array.title_menu);

        NavMenu weather_data[] = new NavMenu[]
                {
                        new NavMenu(R.drawable.ic_book_truck, NavList[0]),
                        new NavMenu(R.drawable.ic_booking_status, NavList[1]),
                        new NavMenu(R.drawable.ic_rate_card, NavList[2]),
                        new NavMenu(R.drawable.ic_edit_profile, NavList[3]),
                        new NavMenu(R.drawable.ic_emergency_contact, NavList[4]),
                        new NavMenu(R.drawable.ic_support, NavList[5]),
                        new NavMenu(R.drawable.ic_about, NavList[6]),
                        new NavMenu(R.drawable.ic_logout, NavList[7])
                };


        NavMenuAdapter adapter = new NavMenuAdapter(this, R.layout.activity_list_item, weather_data);
        listView.setAdapter(adapter);

        drawerListener = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();

            }

        };

        drawerLayout.setDrawerListener(drawerListener);
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_full, menu);
        MenuItem item = menu.findItem(R.id.location_switch);
        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);
        SwitchCompat mySwitch = (SwitchCompat)relativeLayout.findViewById(R.id.switchForActionBar);
        if(Helper.isMyServiceRunning(GpsTracker.class,this))

            mySwitch.setChecked(true);

        else

            mySwitch.setChecked(false);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                trackerStatus(isChecked);
            }
        });

        return true;

    }

    private void trackerStatus(boolean v){

        Intent i = new Intent(this,GpsTracker.class);

        if(v){

            this.startService(i);
            Toast.makeText(this, "GPS Tracking ON", Toast.LENGTH_SHORT).show();

        }
        else{

            this.stopService(i);
            Toast.makeText(this,"GPS Tracking OFF",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerListener.onOptionsItemSelected(item)){

            return true;

        }else{

            return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        drawerLayout.closeDrawers();

        if(position == 5) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + Constants.Config.SUPPORT_CONTACT));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            startActivity(callIntent);

        }
        else if(position==7){

            showLogoutDialog();
        }
        else {

            selectItem(position);
            setTitle(position);

        }

    }

    public  void showLogoutDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.LogoutAlertDialogTitle);
        alertDialog.setMessage(R.string.LogoutAlertDialogMessage);
        alertDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                logout();

            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        alertDialog.show();

    }

    public void logout(){

        Helper.putPreference(this, "user_token", "defaultStringIfNothingFound");
        Helper.putPreference(this, "mobile_no", null);
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
    public void selectItem(int position){

        FragmentTransaction transaction = fragmentManager.beginTransaction();// For AppCompat use getSupportFragmentManager
        switch(position) {

            case 0:
                fragment = new Book();
                break;

            case 1:
                fragment = new BookingStatus();
                break;

            case 2:
                fragment = new RateCard();
                break;

            case 3:
                fragment = new CustomerEdit();
                break;

            case 4:
                fragment = new EmergencyContact();
                break;

            case 6:
                fragment = new About();
                break;

            case 7:
                fragment = new BookingDetails();
                break;

            case 8:
                fragment = new FinishedBookingDetail();
                break;

            default:
                Toast.makeText(this,"haha",Toast.LENGTH_SHORT).show();
                //fragment = new Book();
                break;

        }

        transaction.replace(R.id.main_content, fragment, Constants.Config.CURRENT_FRAG_TAG);
        if((homeFragmentIndentifier == -5)&&(!(fragment instanceof  Book))){

            if(method.equals("push")) {

                transaction.commit();
                method = "";

            }else{

                transaction.addToBackStack(null);
                homeFragmentIndentifier =  transaction.commit();

            }

        }else{

            transaction.commit();

        }

    }

    @Override
    public void onBackPressed() {

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentByTag(Constants.Config.CURRENT_FRAG_TAG)).commit();
        super.onBackPressed();

        if(homeFragmentIndentifier != -5) {

            getSupportActionBar().setTitle(R.string.title_book_fragment);
            fragmentManager.popBackStack(homeFragmentIndentifier, 0);

        }

        homeFragmentIndentifier = -5;

    }

    public void setTitle(int position){

        getSupportActionBar().setTitle(TitleList[position]);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {

            try {

                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();

            }

        } else {

        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

}
