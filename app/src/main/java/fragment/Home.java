package fragment;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.MyApplication;
import com.opl.one.oplsales.R;
import java.util.Calendar;
import java.util.List;
import helper.BaseFragment;
import helper.MovableFloatingActionButton;
import interfac.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import service.EndlessService;
import utils.AppConstant;
import static android.content.Context.MODE_PRIVATE;

public class Home extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private LinearLayout btnDoctorVisit,btnCompletedVisit,btnReportMgt,btnTarget,btnMarket,btnAttendance;
    //private RelativeLayout addDCView,dashSummary;
    private MovableFloatingActionButton addDoctor;
    private TextView addDr,addChemist;
    private TextView levelCollect,levelInvoice;
    private ImageButton btnCollect,btnInvoice;
    private Float translateY =100f;
    private boolean isMenuOpen=false;
    private OvershootInterpolator interpolator =new OvershootInterpolator();
    private Calendar rightNow;
    public static OwnReceiver myBroadCastReceiver;
    private ComponentName componentName;
    private PackageManager packageManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        intUit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!AppConstant.isLocationEnabled(context))
            openDialog("GPS ENABLE!", "GPS in not Enabled.Please select location mode device only.Do you want to go to GPS settings menu?");
        //context.unregisterReceiver(myBroadCastReceiver);
    }

    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        //Typeface ceRaProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProRegular.otf");
        //Typeface awesomeFont = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
        pb = (ProgressBar) getView().findViewById(R.id.pbHome);

        btnDoctorVisit = (LinearLayout) getView().findViewById(R.id.btnDoctorVisit);
        btnCompletedVisit = (LinearLayout) getView().findViewById(R.id.btnCompletedVisit);
        btnReportMgt = (LinearLayout) getView().findViewById(R.id.btnChemistVisit);
        btnMarket = (LinearLayout) getView().findViewById(R.id.btnMarket);
        btnTarget = (LinearLayout) getView().findViewById(R.id.btnTarget);
        btnAttendance = (LinearLayout) getView().findViewById(R.id.btnAttendance);

      /*  addDCView = (RelativeLayout) getView().findViewById(R.id.addDCView);
        dashSummary = (RelativeLayout) getView().findViewById(R.id.dashSummary);*/

        addDoctor = (MovableFloatingActionButton) getView().findViewById(R.id.addDoctor);
        addDr = (TextView) getView().findViewById(R.id.addDr);
        addChemist = (TextView) getView().findViewById(R.id.addChemist);
        btnCollect = (ImageButton) getView().findViewById(R.id.btnCollect);
        btnInvoice = (ImageButton) getView().findViewById(R.id.btnInvoice);
        levelCollect = (TextView) getView().findViewById(R.id.levelCollect);
        levelInvoice = (TextView) getView().findViewById(R.id.levelInvoice);
        addDr.setAlpha(0f);
        addChemist.setAlpha(0f);
        addDr.setTranslationY(translateY);
        addChemist.setTranslationY(translateY);

        btnDoctorVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(new DoctorMgt(), true);
            }
        });

        btnReportMgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(new DoctorReport(), true);
            }
        });

        addDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMenuOpen){
                    closeMenu();
                }else {
                    openMenu();
                }
            }
        });

        addDr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDoctor doctor = new NewDoctor();
                Bundle ars = new Bundle();
                ars.putString("EDIT_TYPE","drAdd");
                doctor.setArguments(ars);
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(doctor, true);
                if (isMenuOpen){
                    closeMenu();
                }else {
                    openMenu();
                }
            }
        });

        addChemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDoctor doctor = new NewDoctor();
                Bundle ars = new Bundle();
                ars.putString("EDIT_TYPE","chemistAdd");
                doctor.setArguments(ars);
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(doctor, true);
                if (isMenuOpen){
                    closeMenu();
                }else {
                    openMenu();
                }
            }
        });

        btnMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(new MarketMgt(), true);
            }
        });

        btnTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(new TargetMain(), true);
            }
        });

        btnCompletedVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(new CompletedMain(), true);
            }
        });

        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(new AttendanceMgt(), true);
            }
        });

        // final String TASK_ID = "data_set_every_15_mini";
       /* final String TASK_ID = "data_set_every_15_mini"; // some unique string id for the task
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(TASK_ID, ExistingPeriodicWorkPolicy.KEEP, work);*/

       /* Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES)
                        .addTag(TASK_ID)
                        .setConstraints(constraints);
        PeriodicWorkRequest workRequest = builder.setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS).build();
        //WorkManager.getInstance().enqueue(workRequest);
        WorkManager.getInstance().enqueueUniquePeriodicWork(TASK_ID,ExistingPeriodicWorkPolicy.REPLACE, workRequest);*/


        /*Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .build();

        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES);
        builder.setConstraints(constraints);
        builder.setInitialDelay(30, TimeUnit.SECONDS);
        builder.addTag("checkLocation");
        PeriodicWorkRequest workRequest = builder.build();
        WorkManager.getInstance(MainActivity.getInstance()).enqueue(workRequest);*/
     /*   WorkManager.getInstance(MainActivity.getInstance()).getWorkInfosByTagLiveData("checkLocation").observe(MainActivity.getInstance(), new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                for (WorkInfo w:workInfos )
                    if (w!=null && w.getState() == WorkInfo.State.SUCCEEDED)
                    Log.e("MyService","onChanged in home fragment : "+w.getState());
            }
        });*/

         componentName = new ComponentName(context, OwnReceiver.class);
         packageManager = context.getPackageManager();
        //Log.e("Token",pref.getString(AppConstant.TOKEN,""));
        rightNow = Calendar.getInstance();
        long sinceNow = rightNow.getTimeInMillis();
        //Log.e("MyService","<>>Time<><>:"+ SystemClock.elapsedRealtime());
        //getVisitSummary(pref.getString(AppConstant.TOKEN,""));
        if (sinceNow>pref.getLong(AppConstant.SET_DASH_BOARD_API_CALL,0)){
            getVisitSummary(pref.getString(AppConstant.TOKEN,""));
        } else {
            btnCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    levelCollect.setText(String.format("%s৳", pref.getFloat(AppConstant.TOTAL_COLLECTION,0)));
                    slideFromRightToLeft(levelCollect);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            levelCollect.setText(getResources().getString(R.string.show_collect));
                        }
                    },1000);

                }
            });
            btnInvoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    levelInvoice.setText(String.format("%s৳", pref.getFloat(AppConstant.TOTAL_INVOICE,0)));
                    slideFromRightToLeft(levelInvoice);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            levelInvoice.setText(getResources().getString(R.string.show_invoice));
                        }
                    },1000);
                }
            });
        }
    }


    private void openMenu(){
        isMenuOpen = !isMenuOpen;
        addDoctor.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
        addDr.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        addChemist.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu(){
        isMenuOpen = !isMenuOpen;
        addDoctor.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
        addDr.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        addChemist.animate().translationY(translateY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
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

    private void getVisitSummary(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getVisitSummary(header);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse!=null){
                    String message = serverResponse.get("message").getAsString().trim();
                    if ( message.equals("Invalid Token.")){
                        AppConstant.logOut(MainActivity.getInstance());
                    }else {
                        float collection = Math.max(serverResponse.get("TotalCollection").getAsFloat(), 0.0f);
                        float invoice =  Math.max(serverResponse.get("TotalInvoice").getAsFloat(), 0.0f);
                        editor.putLong(AppConstant.SET_DASH_BOARD_API_CALL,Calendar.getInstance().getTimeInMillis()+3600000);
                        editor.apply();
                        editor.putFloat(AppConstant.TOTAL_COLLECTION,collection);
                        editor.apply();
                        editor.putFloat(AppConstant.TOTAL_INVOICE,invoice);
                        editor.apply();
                        //Enable BroadCastReceiver
                        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                        Intent myIntent = new Intent(context, OwnReceiver.class);
                        myIntent.setAction("TRS API CALL");
                        context.sendBroadcast(myIntent);
                        //BroadCastReceiver
                        btnCollect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                levelCollect.setText(String.format("%s৳", collection));
                                slideFromRightToLeft(levelCollect);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        levelCollect.setText(getResources().getString(R.string.show_collect));
                                    }
                                },1000);

                            }
                        });
                        btnInvoice.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                levelInvoice.setText(String.format("%s৳", invoice));
                                slideFromRightToLeft(levelInvoice);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        levelInvoice.setText(getResources().getString(R.string.show_invoice));
                                    }
                                },1000);
                            }
                        });
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    public void slideFromRightToLeft(View view) {
        TranslateAnimation animate;
        if (view.getHeight() == 0) {
            view.getWidth();
        }
        animate = new TranslateAnimation(view.getWidth(), 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        myBroadCastReceiver = new OwnReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("TRS API CALL");
       // intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        //intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(myBroadCastReceiver, intentFilter);
    }

    public static class OwnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getAlarmDate(context,intent);
            String action =intent.getAction();
            Log.e("MyService","inside OwnReceiver: "+action);
            Log.e("MyService","service status: "+isMyServiceRunning(context,EndlessService.class));
            if ("TRS API CALL".equals(action) ){
                Intent ii = new Intent(context, EndlessService.class);
                ii.setAction("TRS API CALL");
                if (isMyServiceRunning(context,EndlessService.class)){
                    MyApplication.getAppContext().stopService(ii);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(MyApplication.getAppContext(), ii);
                } else {
                    MyApplication.getAppContext().startService(ii);
                }
            }
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services != null) {
            for (int i = 0; i < services.size(); i++) {
                if ((serviceClass.getName()).equals(services.get(i).service.getClassName()) && services.get(i).pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Calendar getAlarmDate(Context mContext,Intent intent) {
        Calendar calendar = Calendar.getInstance();
        int hour = 0;
        int minute = 0;
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        int cMinute = calendar.get(Calendar.MINUTE);
        hour = cHour;
        minute = cMinute + 15;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 9, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                if (alarm != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
                        try {
                            alarm.setAlarmClock(alarmClockInfo, pendingIntent);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        try {
                            alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //ComponentName receiver = new ComponentName(mContext, OwnReceiver.class);
                // PackageManager pm = mContext.getPackageManager();
                //pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
        });
        return calendar;
    }


}



