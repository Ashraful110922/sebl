package service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.MyApplication;
import com.opl.one.oplsales.R;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import db.DBHandler;
import interfac.ApiService;
import model.MoveData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

public class MyWorker extends Worker {
    private static final String DEFAULT_START_TIME = "08:00";
    private static final String DEFAULT_END_TIME = "23:00";
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context mContext;
    private DBHandler db;
    private static final float LOCATION_REQUEST_DISPLACEMENT = 5.0f;
    private LocationRequest mLocationRequest;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    //private NotificationManager notificationManager;




    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        db = new DBHandler(mContext);
        pref = mContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            String CHANNEL_ID = "TRS API CALL";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "ONE TRS",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) Objects.requireNonNull(mContext.getSystemService(Context.NOTIFICATION_SERVICE))).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(MyApplication.getAppContext(), CHANNEL_ID).setContentTitle("ONE TRS").setContentText("ONE TRS").build();
           startForeground(555666, notification);
        } else {
            startForeground(555666, new Notification());
        }*/
    }


    @NonNull
    @Override
    public Result doWork() {
        createLocationRequest();
        return Result.success();
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
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        String formattedTime = timeFormat.format(date);
        String formattedDate = dateFormat.format(date);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            !=PackageManager.PERMISSION_DENIED) {
                return;
            }

        }else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        try {
            Date cdTime = timeFormat.parse(formattedTime);
            Date sDate = timeFormat.parse(DEFAULT_START_TIME);
            Date eDate = timeFormat.parse(DEFAULT_END_TIME);
            if (cdTime.after(sDate) && cdTime.before(eDate)){
                if (!isLocationEnabled()){
                    JsonObject object =new JsonObject();
                    object.addProperty("Date",formattedDate);
                    object.addProperty("Time",formattedTime);
                    object.addProperty("IsLocation",0);
                    object.addProperty("IsDataConnected",1);
                    connectionUser(pref.getString(AppConstant.TOKEN,""),object);
                }
            }
        }catch (ParseException ignored){

        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Log.e("MyService", "OnCompleteListener: "+task.isSuccessful());
                if (task.isSuccessful()) {
                    Log.e("MyService", "getResult: "+task.getResult());
                    if ( task.getResult() != null){
                        mLocation = task.getResult();
                    try {
                        Date currentDate = timeFormat.parse(formattedTime);
                        Date startDate = timeFormat.parse(DEFAULT_START_TIME);
                        Date endDate = timeFormat.parse(DEFAULT_END_TIME);
                        if (currentDate.after(startDate) && currentDate.before(endDate)){
                            Thread thread = new Thread(new MyThread(mLocation));
                            thread.start();
                        }else {
                            Thread thread = new Thread(new CheckOutThread(mLocation));
                            thread.start();
                            //Log.e("MyService", "Time up to get location. Your time is : " + DEFAULT_START_TIME + " to " + DEFAULT_END_TIME);
                        }
                    } catch (ParseException ignored) {

                    }
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
                super.onLocationResult(locationResult);
                //Log.e("MyService","requestLocationUpdates latitude: "+locationResult.getLocations().get(0).getLatitude());
                //Log.e("MyService","requestLocationUpdates longitude: "+locationResult.getLocations().get(0).getLongitude());
            }
        }, Looper.getMainLooper());
    }

    final class MyThread implements Runnable{
        Location mLocation;
        MyThread(Location mLocation){
            this.mLocation= mLocation;
        }
        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm ",Locale.getDefault());
            List<MoveData> list = new ArrayList<MoveData>();
            list.clear();
            MoveData user = new MoveData();
            String address= getCompleteAddressString(mLocation.getLatitude(),mLocation.getLongitude());
            user.setVisitDateTime(sdf.format(Calendar.getInstance().getTime()));
            user.setLatitude(String.valueOf(mLocation.getLatitude()));
            user.setLongitude(String.valueOf(mLocation.getLongitude()));
            if (!TextUtils.isEmpty(address)){
                user.setAddress(address);
            }else {
                user.setAddress("There is no found address using latitude:"+mLocation.getLatitude() +" Longitude: "+mLocation.getLongitude());
            }
            db.addAttendance(user);
            list.addAll(db.getAllAttendance());
            Log.e("MyService","List :  "+new Gson().toJson(list));
            setLocationData(pref.getString(AppConstant.TOKEN,""),list);
        }
    }

    private void setLocationData(String header, List<MoveData> list) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setLocationData(header, list);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                JsonObject serverResponse = response.body();
                Log.e("MyService", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (serverResponse != null && response.code() == 200) {
                    boolean status = serverResponse.get("status").getAsBoolean();
                    String message = serverResponse.get("message").getAsString().trim();
                    if (status){
                        db.deleteAll();
                    }else if(message.equals("Invalid Token.")){
                        AppConstant.logOut(MainActivity.getInstance());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
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

    private boolean isLocationEnabled() {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    private void connectionUser(String header,JsonObject object){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.connectionUser(header,object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                JsonObject serverResponse = response.body();
                Log.e("MyData",new Gson().toJson(serverResponse));
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
               Log.e("MyData",t.getMessage());
            }
        });
    }

    final class CheckOutThread implements Runnable{
        Location mLocation;
        CheckOutThread(Location mLocation){
            this.mLocation= mLocation;
        }
        @Override
        public void run() {
            Calendar current = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat tf = new SimpleDateFormat("hh:mm a",Locale.getDefault());
            String address= getCompleteAddressString(mLocation.getLatitude(),mLocation.getLongitude());
            if (Integer.parseInt(pref.getString(AppConstant.CHECK_IN_STATUS,"")) ==1){
                JsonObject object = new JsonObject();
                object.addProperty("flag",0);
                object.addProperty("address",address);
                object.addProperty("dateTime",df.format(current.getTime()));
                object.addProperty("time",tf.format(current.getTime()));
                object.addProperty("opinion","Default check out");
                object.addProperty("latitude",mLocation.getLatitude());
                object.addProperty("longitude",mLocation.getLongitude());
                setCheckInOut(pref.getString(AppConstant.TOKEN,""),object);
            }
        }
    }

    private void setCheckInOut(String header,JsonObject object){
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
                JsonObject serverResponse = response.body();
                Log.e("MyService","CheckOut: "+new Gson().toJson(serverResponse));
                if(response.code()== 200 && serverResponse!=null) {
                    boolean status = serverResponse.get("status").getAsBoolean();
                    if (status){
                        Log.e("MyService","CheckOut success:"+status);
                        editor.putString(AppConstant.CHECK_IN_STATUS,"2");
                        editor.apply();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("MyService",t.getMessage());
            }
        });
    }

}
