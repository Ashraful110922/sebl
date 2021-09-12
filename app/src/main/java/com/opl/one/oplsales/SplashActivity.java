package com.opl.one.oplsales;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import utils.AppConstant;

public class SplashActivity extends AppCompatActivity {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String[] PERMISSIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        context = this;

        PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA
        };
    }

    @Override
    protected void onStart() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            openPermission();
            //Log.e("openPermission","openPermission"+ Build.VERSION_CODES.Q+"<>"+Build.VERSION.SDK_INT);
        } else {
            if (!AppConstant.isLocationEnabled(context)) {
                openDialog("GPS ENABLE!", "GPS in not Enabled.Please select location mode device only.Do you want to go to GPS settings menu?");
            } else {
                startAnim();
            }
        }
        super.onStart();
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS) {
        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void startAnim() {
        Typeface ceraProRegular = Typeface.createFromAsset(getAssets(), "fonts/CeraProRegular.otf");
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout mainLinearLayout = (RelativeLayout) findViewById(R.id.splash);
        mainLinearLayout.clearAnimation();
        mainLinearLayout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.image);
        TextView tvSplash = findViewById(R.id.tvSplash);
        tvSplash.setTypeface(ceraProRegular);
        iv.clearAnimation();
        tvSplash.clearAnimation();
        iv.startAnimation(anim);
        tvSplash.startAnimation(anim);
        iv.setVisibility(View.VISIBLE);
        tvSplash.setVisibility(View.VISIBLE);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (pref.getString(AppConstant.IS_LOGIN, "").equalsIgnoreCase("success")) {
                        // Log.e("expired","<>expired<>"+pref.getLong("EXPIRES_DATE",0));
                        Date dt = new Date();
                        Calendar c = Calendar.getInstance();
                        c.setTime(dt);
                        c.add(Calendar.DATE, 1);
                        dt = c.getTime();
                        //Log.e("expired","<>current<>"+dt.getTime());
                       /* if(dt.getTime() >= pref.getLong("EXPIRES_DATE",0)){
                            editor.putString("IS_LOGIN","");
                            editor.apply();
                            Intent launchIntent = new Intent(SplashActivity.this,LaunchActivity.class);
                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(launchIntent);
                            finish();
                        }else {*/
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                        startActivity(mainIntent);
                        finish();
                        // }
                    } else {
                        Intent launchIntent = new Intent(SplashActivity.this, LaunchActivity.class);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                        startActivity(launchIntent);
                        finish();
                    }

                }
            }
        };
        timer.start();
    }

    private void openPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withContext(this).withPermissions(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CAMERA
            ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        if (!AppConstant.isLocationEnabled(context)) {
                            openDialog("GPS ENABLE!", "GPS in not Enabled.Please select location mode device only.Do you want to go to GPS settings menu?");
                        } else {
                            startAnim();
                        }
                    }
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        Toast.makeText(context, "Please cash clean or application uninstall then again install this application", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).
                    withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {
                            Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSameThread()
                    .check();
        } else {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CAMERA
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        startAnim();
                    }
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        Toast.makeText(context, "Please cash clean or application uninstall then again install this application", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).
                    withErrorListener(new PermissionRequestErrorListener() {
                        @Override
                        public void onError(DexterError error) {
                            Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onSameThread()
                    .check();

        }

    }

    private void openDialog(String title, String mgs) {
        final Dialog openDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog_Alert);
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