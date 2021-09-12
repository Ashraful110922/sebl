package fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import helper.BaseFragment;
import interfac.ApiService;
import model.MoveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import service.MyWorker;
import utils.AppConstant;
import static android.content.Context.MODE_PRIVATE;

public class Launch extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private TextView tvOplSales,forgotPass,btnLogin,tvSingCont,tvBBar;
    private TextInputEditText ctUserName,ctUserPass;

    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final float LOCATION_REQUEST_DISPLACEMENT = 5.0f;
    private LocationRequest mLocationRequest;
    private String address="";
    //private double latitude=23.8103,longitude=90.4125;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        return inflater.inflate(R.layout.launch_, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        intUit();
    }

    private void intUit() {
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Log.e("width","<><><><>"+displayMetrics.heightPixels);

        pb = (ProgressBar) getView().findViewById(R.id.pbLogin);
        tvOplSales = (TextView) getView().findViewById(R.id.tvOplSales);
        forgotPass = (TextView) getView().findViewById(R.id.forgotPass);
        btnLogin = (TextView) getView().findViewById(R.id.btnLogin);
        tvSingCont = (TextView) getView().findViewById(R.id.tvSingCont);
        tvBBar = (TextView) getView().findViewById(R.id.tvBBar);
        ctUserName = (TextInputEditText) getView().findViewById(R.id.ctUserName);
        ctUserPass = (TextInputEditText) getView().findViewById(R.id.ctUserPass);
        Typeface ceraProBold = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProBold.otf");
        Typeface ceraProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProRegular.otf");
        tvOplSales.setTypeface(ceraProBold);
        tvSingCont.setTypeface(ceraProRegular);
        btnLogin.setTypeface(ceraProRegular);
        forgotPass.setTypeface(ceraProRegular);
        tvBBar.setTypeface(ceraProRegular);
        ctUserName.setTypeface(ceraProRegular);
        ctUserPass.setTypeface(ceraProRegular);

        createLocationRequest();

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(R.anim.enter_animation,R.anim.exit_animation);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isLocationEnabled(context))
                    dataVerify();
                else
                    openDialog("GPS ENABLE!", "GPS in not Enabled.Please select location mode device only.Do you want to go to GPS settings menu?");
              /*  editor.putString("IS_LOGIN","success");
                editor.apply();
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();*/
            }
        });
        //JsonParser parser = new JsonParser();
        //JsonObject original = (JsonObject) parser.parse(pref.getString("TARGET_MGT_RESPONSE",""));
    }

    private void dataVerify(){
        String name,pass;
        ctUserName.setError(null);
        ctUserPass.setError(null);
        name = ctUserName.getText().toString().trim();
        pass = ctUserPass.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            ctUserName.setError("Required Employee ID");
            ctUserName.requestFocus();
        }else if(TextUtils.isEmpty(pass)){
            ctUserPass.setError("Required password");
            ctUserPass.requestFocus();
        }else {
            JsonObject object = new JsonObject();
            object.addProperty("Name",name);
            object.addProperty("Password",pass);
            if (mLocation!=null){
                object.addProperty("Latitude",mLocation.getLatitude());
                object.addProperty("Longitude",mLocation.getLongitude());
            }
            object.addProperty("Address",address);
            Log.e("MyData","g:  "+new Gson().toJson(object));
            if (AppConstant.isOnline(context)){
                loginApp(object);
            }else {
                AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
            }
        }
    }


    private void loginApp(JsonObject jsonObject){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.loginApp(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if(response.code()== 200 && serverResponse!=null) {
                    boolean status = serverResponse.get("status").getAsBoolean();
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if(status){
                        editor.putString(AppConstant.IS_LOGIN,"success");
                        editor.apply();

                        if (serverResponse.has("auth_token")){
                            editor.putString(AppConstant.TOKEN,serverResponse.get("auth_token").getAsString());
                            editor.apply();
                        }

                        if (serverResponse.get("profile")!=null && !serverResponse.get("profile").isJsonNull()){
                            editor.putString(AppConstant.LOGIN_RESPONSE,new Gson().toJson(serverResponse.get("profile").getAsJsonObject()));
                        }else {
                            editor.putString(AppConstant.LOGIN_RESPONSE,"");
                        }
                        editor.apply();

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }else {
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
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


    private void openDialog(String title, String mgs) {
        final Dialog openDialog = new Dialog(context,R.style.Theme_AppCompat_Dialog_Alert);
        openDialog.setContentView(R.layout.alert);
        TextView alertTitle = (TextView) openDialog.findViewById(R.id.alertTitle);
        TextView alertDesc = (TextView) openDialog.findViewById(R.id.alertDesc);
        TextView btnOk = (TextView) openDialog.findViewById(R.id.btnOk);
        alertTitle.setText(title);
        alertDesc.setText(mgs);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                openDialog.dismiss();
            }
        });
        openDialog.show();
    }



}



