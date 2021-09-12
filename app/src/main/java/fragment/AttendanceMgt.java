package fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import adapter.AttendViewAdapter;
import adapter.DoctorAdapter;
import helper.BaseFragment;
import helper.DividerItemDecoration;
import interfac.ApiService;
import model.LatLon;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;

public class AttendanceMgt extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RecyclerView mRecyclerView;
    private RelativeLayout notFound;
    private ArrayList<LatLon> list= new ArrayList<LatLon>();

    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final float LOCATION_REQUEST_DISPLACEMENT = 5.0f;
    private LocationRequest mLocationRequest;
    private String opinion="",address="",dateTime="",time="";
    private Calendar current;
    private SimpleDateFormat df,tf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.atten_mgt, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        bundle = this.getArguments();
        intUit();
    }

    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle(getResources().getString(R.string.in_out));
        notFound = (RelativeLayout) getView().findViewById(R.id.notFoundAttendance);
        pb = (ProgressBar) getView().findViewById(R.id.pbAttendMgt);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rcvAttend);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1,RecyclerView.VERTICAL,false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, ResourcesCompat.getDrawable(context.getResources(),R.drawable.divider,context.getTheme())));
        mRecyclerView.setLayoutManager(gridVertical);
        notFound.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        createLocationRequest();

        current=Calendar.getInstance();
        df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        tf = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        dateTime = df.format(current.getTime());
        time = tf.format(current.getTime());

        Log.e("MyData","Token: "+ pref.getString(AppConstant.TOKEN,""));
        Log.e("MyData","CheckIn status: "+ pref.getString(AppConstant.CHECK_IN_STATUS,""));

       if (AppConstant.isOnline(context)){
           getCheckInOut(pref.getString(AppConstant.TOKEN,""));
       }else {
           AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
       }
       /* list.clear();
        list.add(0,new LatLon(0,""));*/
  /*      list.add(1,new LatLon(1,""));
        list.add(2,new LatLon(2,""));
        list.add(3,new LatLon(3,""));
        list.add(4,new LatLon(4,""));
        list.add(5,new LatLon(5,""));
        list.add(6,new LatLon(6,""));
        list.add(7,new LatLon(7,""));
        list.add(8,new LatLon(8,""));*/
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
                        //Log.e("MyData","Latitude: "+mLocation.getLatitude());
                        //Log.e("MyData","Longitude: "+mLocation.getLongitude());
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

                    }
                },
                Looper.myLooper());
    }


    final class MyThread implements Runnable{
        Location mLocation;
        MyThread(Location mLocation){
            this.mLocation= mLocation;
        }
        @Override
        public void run() {
            if (mLocation!=null)
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

    private void getCheckInOut(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getCheckInOut(header);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() ==200 && serverResponse!=null){
                    if (serverResponse.get("Data")!=null &&serverResponse.get("Data").getAsJsonArray().size()>0) {
                        JsonObject object =  serverResponse.get("Data").getAsJsonArray().get(0).getAsJsonObject();
                        String flag = object.get("Flag").getAsString();
                        int WorkingDays=0;
                        int PresentDays = 0;
                        if (serverResponse.get("Summary").getAsJsonArray().get(0).getAsJsonObject()!=null){
                            WorkingDays = serverResponse.get("Summary").getAsJsonArray().get(0).getAsJsonObject().get("WorkingDays").getAsInt();
                            PresentDays =serverResponse.get("Summary").getAsJsonArray().get(0).getAsJsonObject().get("PresentDays").getAsInt();
                        }
                        JsonArray history = serverResponse.get("History").getAsJsonArray();
                        list.clear();
                        if (history.size()>0){
                            TypeToken<ArrayList<LatLon>> token = new TypeToken<ArrayList<LatLon>>() {};
                            list = new Gson().fromJson(history, token.getType());
                        }
                        editor.putString(AppConstant.CHECK_IN_STATUS,flag);
                        editor.apply();
                        AttendViewAdapter adapter = new AttendViewAdapter(context, list, WorkingDays, PresentDays, flag, new AttendViewAdapter.MyAdapterListener() {
                            @Override
                            public void checkBtn(View v, int position) {
                                JsonObject object = new JsonObject();
                                if (Integer.parseInt(flag) ==2){
                                    object.addProperty("flag",1);
                                } else if (Integer.parseInt(flag) ==1){
                                    object.addProperty("flag",0);
                                } else if(Integer.parseInt(flag) ==0){
                                    object.addProperty("flag",1);
                                }
                                object.addProperty("address",address);
                                object.addProperty("dateTime",dateTime);
                                object.addProperty("time",time);
                                object.addProperty("opinion",opinion);
                                if (mLocation!=null){
                                    object.addProperty("latitude",mLocation.getLatitude());
                                    object.addProperty("longitude",mLocation.getLongitude());
                                    if (AppConstant.isOnline(context)){
                                        //Log.e("MyData","Json: "+new Gson().toJson(object));
                                        setCheckInOut(pref.getString(AppConstant.TOKEN,""),object);
                                    }else {
                                        AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                                    }
                                }else {
                                    Toast.makeText(context,"Location not found,Please try again",Toast.LENGTH_SHORT).show();
                                }
                              /*  Log.e("MyData","address: "+address);
                                Log.e("MyData","dateTime: "+dateTime);
                                Log.e("MyData","time: "+time);
                                Log.e("MyData","opinion: "+opinion);
                                Log.e("MyData","latitude: "+mLocation.getLatitude());
                                Log.e("MyData","longitude: "+mLocation.getLongitude());
                                Log.e("MyData","flag: "+flag)*/;
                            }
                            @Override
                            public void rowClick(View v, int position) {
                                LatLon att = list.get(position - 1);
                                AttendReport  report= new AttendReport();
                                Bundle args = new Bundle();
                                args.putParcelable("REPORT_ATTENDANCE", att);
                                report.setArguments(args);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.setContentFragment(report, true);
                            }
                            @Override
                            public void onTextChanged(int position, String charSeq) {
                                opinion = charSeq;
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }
                }else {
                    notFound.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }
    private void setCheckInOut(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setCheckInOut(header,object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData",new Gson().toJson(serverResponse));
                if(response.code()== 200 && serverResponse!=null) {
                    boolean status = serverResponse.get("status").getAsBoolean();
                    String message = serverResponse.get("message").getAsString().trim();
                    Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    if (status){
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(AttendanceMgt.this).attach(AttendanceMgt.this).commit();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}



