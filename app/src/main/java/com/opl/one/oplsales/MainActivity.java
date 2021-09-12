package com.opl.one.oplsales;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import db.DBHandler;
import fragment.DoctorMgt;
import fragment.DoctorReport;
import fragment.Home;
import fragment.Launch;
import fragment.MarketMgt;
import fragment.PasswordChange;
import fragment.Profile;
import fragment.ReportDoctor;
import fragment.ReportEmp;
import fragment.ReportMio;
import helper.CustomTypefaceSpan;
import helper.ManagementAdapter;
import helper.RoundedTransformation;
import helper.UIUtils;
import interfac.ApiService;
import interfac.CommunicatorFragmentInterface;
import model.ItemType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

public class MainActivity extends AppCompatActivity implements CommunicatorFragmentInterface {
    private Context context;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private int height;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    static MainActivity mainActivity;
    private ProgressBar pb;

    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final float LOCATION_REQUEST_DISPLACEMENT = 5.0f;
    private LocationRequest mLocationRequest;
    private String address="";
    private List<ItemType> tempArrayReport;
    private List<ItemType> tempArrayList;
    private List<ItemType> tempArrayTracking;
    private ListView lvReport,lvList,lvTracking;
    private RelativeLayout profileView,reportView,listMenu,trackingView,vieChangePass;
    private TextView postingPlace,supporterInfo;
    private ImageView picUser,reportArrow,listArrow,trackingArrow;
    private boolean flagReport= true,flagList=true,flagTrack=true;
    private DBHandler db;
    public static MainActivity getInstance() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();
        mainActivity = this;
        pref = MainActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        db = new DBHandler(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels/4;
        pb = (ProgressBar) findViewById(R.id.pbMain);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        createLocationRequest();
        Home home= new Home();
        setContentFragment(home,false);
        initUi();

    }

    private void initUi() {
        tempArrayReport = new ArrayList<ItemType>();
        tempArrayReport.clear();
        tempArrayList = new ArrayList<ItemType>();
        tempArrayList.clear();
        tempArrayTracking = new ArrayList<ItemType>();

        lvReport = (ListView) findViewById(R.id.lvReport);
        lvList = (ListView) findViewById(R.id.lvList);
        lvTracking = (ListView) findViewById(R.id.lvTracking);

        profileView = (RelativeLayout) findViewById(R.id.profileView);
        reportView = (RelativeLayout) findViewById(R.id.reportView);
        listMenu = (RelativeLayout) findViewById(R.id.listMenu);
        trackingView = (RelativeLayout) findViewById(R.id.trackingView);
        vieChangePass = (RelativeLayout) findViewById(R.id.vieChangePass);

        postingPlace = (TextView) findViewById(R.id.postingPlace);
        picUser = (ImageView) findViewById(R.id.picUser);
        reportArrow = (ImageView) findViewById(R.id.reportArrow);
        listArrow = (ImageView) findViewById(R.id.listArrow);
        trackingArrow = (ImageView) findViewById(R.id.trackingArrow);
        supporterInfo = (TextView) findViewById(R.id.supporterInfo);

        dummyDataReport();
        dummyDataList();
        dummyDataTracking();

        int statusBar = getStatusBarHeight();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, statusBar, 0, 0);
        profileView.setLayoutParams(params);

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllFragment();
                mDrawer.closeDrawer(GravityCompat.START);
                setContentFragment(new Profile(),true);
            }
        });

        reportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listMenu.setBackgroundColor(Color.WHITE);
                vieChangePass.setBackgroundColor(Color.WHITE);
                trackingView.setBackgroundColor(Color.WHITE);
                reportView.setBackgroundColor(ContextCompat.getColor(context,R.color.et_bg));

                if (flagReport){
                    flagReport = false;
                    lvReport.setVisibility(View.VISIBLE);
                    reportArrow.setRotation(90);
                    lvReport.invalidateViews();

                    flagList = true;
                    lvList.setVisibility(View.GONE);
                    listArrow.setRotation(0);

                    flagTrack = true;
                    lvTracking.setVisibility(View.GONE);
                    trackingArrow.setRotation(0);

                }else{
                    flagReport = true;
                    lvReport.setVisibility(View.GONE);
                    reportArrow.setRotation(0);
                }
            }
        });

        listMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportView.setBackgroundColor(Color.WHITE);
                vieChangePass.setBackgroundColor(Color.WHITE);
                trackingView.setBackgroundColor(Color.WHITE);
                listMenu.setBackgroundColor(ContextCompat.getColor(context,R.color.et_bg));

                if (flagList){
                    flagList = false;
                    lvList.setVisibility(View.VISIBLE);
                    listArrow.setRotation(90);
                    lvList.invalidateViews();
                    lvList.setFocusable(true);

                    flagReport = true;
                    lvReport.setVisibility(View.GONE);
                    reportArrow.setRotation(0);

                    flagTrack = true;
                    lvTracking.setVisibility(View.GONE);
                    trackingArrow.setRotation(0);

                }else{
                    flagList = true;
                    lvList.setVisibility(View.GONE);
                    listArrow.setRotation(0);
                }
            }
        });


        trackingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportView.setBackgroundColor(Color.WHITE);
                vieChangePass.setBackgroundColor(Color.WHITE);
                listMenu.setBackgroundColor(Color.WHITE);
                trackingView.setBackgroundColor(ContextCompat.getColor(context,R.color.et_bg));

                if (flagTrack){
                    flagTrack = false;
                    lvTracking.setVisibility(View.VISIBLE);
                    trackingArrow.setRotation(90);
                    lvTracking.invalidateViews();
                    lvTracking.setFocusable(true);

                    flagReport = true;
                    lvReport.setVisibility(View.GONE);
                    reportArrow.setRotation(0);

                    flagList= true;
                    lvList.setVisibility(View.GONE);
                    listArrow.setRotation(0);

                }else{
                    flagTrack = true;
                    lvTracking.setVisibility(View.GONE);
                    trackingArrow.setRotation(0);
                }
            }
        });

        vieChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportView.setBackgroundColor(Color.WHITE);
                listMenu.setBackgroundColor(Color.WHITE);
                trackingView.setBackgroundColor(Color.WHITE);
                vieChangePass.setBackgroundColor(ContextCompat.getColor(context,R.color.et_bg));

                flagReport = true;
                lvReport.setVisibility(View.GONE);
                reportArrow.setRotation(0);

                flagList = true;
                lvList.setVisibility(View.GONE);
                listArrow.setRotation(0);

                flagTrack = true;
                lvTracking.setVisibility(View.GONE);
                trackingArrow.setRotation(0);

                removeAllFragment();
                mDrawer.closeDrawer(GravityCompat.START);
                overridePendingTransition(R.anim.enter_animation,R.anim.exit_animation);
                setContentFragment(new PasswordChange(),true);

            }
        });


        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (!TextUtils.isEmpty(pref.getString(AppConstant.LOGIN_RESPONSE, ""))) {
                    JsonParser parser = new JsonParser();
                    JsonObject response = (JsonObject) parser.parse(pref.getString(AppConstant.LOGIN_RESPONSE, ""));
                    if (response.has("empImage") && !TextUtils.isEmpty(response.get("empImage").getAsString()))
                        Picasso.get()
                                .load(AppConstant.BASE_URL+"/"+response.get("empImage").getAsString())
                                .placeholder(R.drawable.camera_icon)
                                .error(R.drawable.camera_icon)
                                .transform(new RoundedTransformation(10, 0))
                                .into(picUser);
                    String posting="";
                    if(response.get("POSTING_LOCATION")!=null && !TextUtils.isEmpty(response.get("POSTING_LOCATION").getAsString())){
                        String type = response.get("POSTING_LOCATION").getAsString();
                        switch (type) {
                            case "Z":
                                posting = response.get("ZoneName")!=null ?response.get("ZoneName").getAsString():"";
                                break;
                            case "D":
                                posting= response.get("DepotName")!=null ?response.get("DepotName").getAsString():"";
                                break;
                            case "R":
                                posting=  response.get("RegionName")!=null?response.get("RegionName").getAsString():"";
                                break;
                            case "A":
                                posting= response.get("AreaName")!=null ? response.get("AreaName").getAsString():"";
                                break;
                            case "T":
                                posting=  response.get("TerritoryName")!=null?response.get("TerritoryName").getAsString():"";
                                break;
                        }
                    }
                    postingPlace.setText(Html.fromHtml("<big><b>"+response.get("MIOName").getAsString() +"</b></big><br><small><b>Posting: </b>"+posting+"<br><b>Designation: </b>"+response.get("Designation").getAsString()+"</small>"));

                    String supporterName="",supporterMobile="";
                    supporterName = response.get("SupportName")!=null ?response.get("SupportName").getAsString():supporterName;
                    supporterMobile = response.get("SupportMobile")!=null ?response.get("SupportMobile").getAsString():supporterMobile;

                if (!TextUtils.isEmpty(supporterName) && !TextUtils.isEmpty(supporterMobile))
                    supporterInfo.setText(Html.fromHtml("<big><b>Supporter Information<br>Name: "+supporterName +"</b></big><br><small><b>Mobile:<font color='#0000FF'>"+supporterMobile+"</font></b></small>"));
                else if(!TextUtils.isEmpty(supporterName) && TextUtils.isEmpty(supporterMobile))
                    supporterInfo.setText(Html.fromHtml("<big><b>Supporter Information<br>Name: "+"Md. Alam" +"</b></big><br><small><b>Mobile:<font color='#0000FF'>Not found</font></b></small>"));
                else
                    supporterInfo.setText(Html.fromHtml("<big><b>Supporter Information<br>Name: Not found</b></big><br><small><b>Mobile:<font color='#0000FF'>Not found</font></b></small>"));
                    supporterInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeAllFragment();
                            mDrawer.closeDrawer(GravityCompat.START);
                            try {
                                String mobile = response.get("SupportMobile")!=null ?response.get("SupportMobile").getAsString():"Mobile no not found";
                                String uri = "tel:" + mobile;
                                if (!TextUtils.isEmpty(uri)) {
                                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
                                    startActivity(dialIntent);
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }



                if (!AppConstant.isLocationEnabled(context)) {
                    openDialog("GPS ENABLE!", "GPS in not Enabled.Please select location mode device only.Do you want to go to GPS settings menu?");
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // do something when drawer closed
            }


            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });




    }

    private int getStatusBarHeight() {
        int height;
        Resources myResources = getResources();
        int idStatusBarHeight = myResources.getIdentifier( "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = getResources().getDimensionPixelSize(idStatusBarHeight);
        } else {
            height = 0;
        }
        return height;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                finish();
                startActivity(getIntent());
                return true;
            case R.id.menu_logout:
                JsonObject object=new JsonObject();
                object.addProperty("Latitude",mLocation.getLatitude());
                object.addProperty("Longitude",mLocation.getLongitude());
                object.addProperty("Address",address);
                logoutUser(pref.getString(AppConstant.TOKEN,""),object);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setContentFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment cf = fm.findFragmentById(R.id.flContent);

        if (cf != null && fragment.getClass().isAssignableFrom(cf.getClass())) {
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flContent, fragment, fragment.getClass().getName());

        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    @Override
    public void addContentFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null) {
            return;
        }
        final FragmentManager fm = getSupportFragmentManager();
        Fragment cf = fm.findFragmentById(R.id.flContent);

        if (cf != null && fragment.getClass().isAssignableFrom(cf.getClass())) {
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flContent, fragment, fragment.getClass().getName());
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getName());
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    @Override
    public void removeAllFragment() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fm.popBackStackImmediate();
        }
    }
    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (count>0) {
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }

    private void openDialog(String title, String mgs) {
        final Dialog dialog = new Dialog(MainActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
        dialog.setContentView(R.layout.alert);
        TextView alertTitle = (TextView) dialog.findViewById(R.id.alertTitle);
        TextView alertDesc = (TextView) dialog.findViewById(R.id.alertDesc);
        TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);
        alertTitle.setText(title);
        alertDesc.setText(mgs);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                dialog.dismiss();
                mDrawer.closeDrawers();

            }
        });
        dialog.show();
    }


    private void logoutUser(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.logoutUser(header,object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData",new Gson().toJson(serverResponse));
                if(response.code()== 200 && serverResponse!=null) {
                    boolean status = serverResponse.get("status").getAsBoolean();
                    String message= serverResponse.get("message").getAsString();
                    //Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if (status || message.equals("Invalid Token.")){
                        AppConstant.logOut(MainActivity.getInstance());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(AppConstant.LOCATION_INTERVAL);
            mLocationRequest.setSmallestDisplacement(LOCATION_REQUEST_DISPLACEMENT);
            requestLocationUpdate();
        }
    }


    private void requestLocationUpdate() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            !=PackageManager.PERMISSION_DENIED) {
                return;
            }

        }else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Log.e("MyService", "OnCompleteListener: "+task.isSuccessful());
                if (task.isSuccessful()) {
                    if ( task.getResult() != null){
                        mLocation = task.getResult();
                        Thread thread = new Thread(new MyThread(mLocation));
                        thread.start();
                        //Log.e("MyData","Latitude: Main..."+mLocation.getLatitude());
                        //Log.e("MyData","Longitude: Main..."+mLocation.getLongitude());
                    }else {
                        Log.e("MyService", "Location not found.");
                    }
                } else {
                    Log.e("MyService", "Failed to get location.");
                }
            }
        });
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here

                    }
                },
                Looper.myLooper());
       // mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }


    final class MyThread implements Runnable{
        Location mLocation;
        MyThread(Location mLocation){
            this.mLocation= mLocation;
        }
        @Override
        public void run() {
            address = getCompleteAddressString(mLocation.getLatitude(), mLocation.getLongitude());

        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private void dummyDataReport(){
        tempArrayReport.add(new ItemType("EMPLOYEE REPORT",R.drawable.right_arrow));
        tempArrayReport.add(new ItemType("MIO REPORT",R.drawable.right_arrow));
        tempArrayReport.add(new ItemType("DOCTOR REPORT",R.drawable.right_arrow));
        tempArrayReport.add(new ItemType("CHEMIST REPORT",R.drawable.right_arrow));
        ManagementAdapter manageMentAdapter  = new ManagementAdapter(mainActivity,tempArrayReport,mDrawer);
        lvReport.setAdapter(manageMentAdapter);
        UIUtils.setListViewHeightBasedOnItems(lvReport);
    }

    private void dummyDataList(){
        tempArrayList.add(new ItemType("DOCTOR LIST",R.drawable.right_arrow));
        tempArrayList.add(new ItemType("CHEMIST LIST",R.drawable.right_arrow));
        tempArrayList.add(new ItemType("MARKET LIST",R.drawable.right_arrow));
        ManagementAdapter listAdapter  = new ManagementAdapter(mainActivity,tempArrayList,mDrawer);
        lvList.setAdapter(listAdapter);
        UIUtils.setListViewHeightBasedOnItems(lvList);
    }

    private void dummyDataTracking(){
        tempArrayTracking.add(new ItemType("CURRENT LOCATION",R.drawable.right_arrow));
        tempArrayTracking.add(new ItemType("ROAD MAP",R.drawable.right_arrow));
        ManagementAdapter adapter  = new ManagementAdapter(mainActivity,tempArrayTracking,mDrawer);
        lvTracking.setAdapter(adapter);
        UIUtils.setListViewHeightBasedOnItems(lvTracking);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
