package service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

public class EndlessService extends Service {
    private Context mContext;
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private DBHandler db;
    private static final float LOCATION_REQUEST_DISPLACEMENT = 5.0f;
    private LocationRequest mLocationRequest;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreate() {
        mContext= this.getApplicationContext();
        db = new DBHandler(mContext);
        pref = mContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = pref.edit();
        Log.e("MyService","onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MyService","onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("EndlessServiceTrs",
                    "OneTrs",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE))).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(mContext, "EndlessServiceTrs").setContentTitle("OneTrs").setContentText("OneTrs").build();
            startForeground(2525, notification);
        } else {
            startForeground(2525, new Notification());
        }
        createLocationRequest();
        return START_STICKY;
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
        Date targetTime = get2DayMidNight();
        // Log.e("MyService","time target:"+targetTime);
        if (new GregorianCalendar().getTime().before(targetTime) ){
            if (!isLocationEnabled()){
                JsonObject object =new JsonObject();
                object.addProperty("Date",formattedDate);
                object.addProperty("Time",formattedTime);
                object.addProperty("IsLocation",0);
                object.addProperty("IsDataConnected",1);
                connectionUser(pref.getString(AppConstant.TOKEN,""),object);
            }
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
                        Date midNight = get2DayMidNight();
                        if (new GregorianCalendar().getTime().before(midNight)){
                            Thread thread = new Thread(new MyThread(mLocation));
                            thread.start();
                        }else {
                            Thread thread = new Thread(new CheckOutThread(mLocation));
                            thread.start();
                        }
                    }else {
                        Log.e("MyService", "Location not found.");
                        //stopSelf();
                    }
                } else {
                    Log.e("MyService", "Failed to get location.");
                    //stopSelf();
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
            list.clear();
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
                Log.e("MyService", "Set location<><>"+new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (serverResponse != null && response.code() == 200) {
                    boolean status = serverResponse.get("status").getAsBoolean();
                    String message = serverResponse.get("message").getAsString().trim();
                    if (status){
                        db.deleteAll();
                        //stopForeground(true);
                        //stopSelf();
                    }else if(message.equals("Invalid Token.")){
                        AppConstant.logOut(MainActivity.getInstance());
                        //stopSelf();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                //stopSelf();
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
                //stopSelf();
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                //stopSelf();
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
            if (Integer.parseInt(Objects.requireNonNull(pref.getString(AppConstant.CHECK_IN_STATUS, ""))) ==1){
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
                        //stopSelf();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("MyService",t.getMessage());
                //stopSelf();
            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 9696, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(SystemClock.elapsedRealtime() + 300000, restartServicePendingIntent);
            try {
                alarmService.setAlarmClock(alarmClockInfo, restartServicePendingIntent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                alarmService.setExact(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 300000, restartServicePendingIntent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            try {
                alarmService.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 300000, restartServicePendingIntent);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }*/
        super.onTaskRemoved(rootIntent);
    }

    private  Date get2DayMidNight(){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return   c.getTime();
    }

    @Override
    public void onDestroy() {
        Log.e("MyService","onDestroy");
        //stopSelf();
        super.onDestroy();
    }
}
