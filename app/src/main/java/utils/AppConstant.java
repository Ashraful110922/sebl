package utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.gson.JsonElement;
import com.opl.one.oplsales.LaunchActivity;
import com.opl.one.oplsales.R;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import db.DBHandler;
import fragment.Home;
import service.EndlessService;

public class AppConstant {
    //public static String BASE_URL = "http://10.11.4.155:8097/";
    public static String BASE_URL = "http://103.106.236.90:9105/";

    public static final int LOCATION_INTERVAL = 300000;
    public static final int FASTEST_LOCATION_INTERVAL = 10000;
    public static String IS_LOGIN="isLogin";
    public static String TOKEN= "token";
    public static String LOGIN_RESPONSE="loginResponse";
    public static String TYPE_DR_CHEMIST= "typeDrChemist";
    public static String TYPE_REPORT= "typeReport";
    public static String SHOW_VIEW ="showView";
    public static String CHECK_IN_STATUS= "check_in_status";
    public static String SET_DASH_BOARD_API_CALL="set_dash_board_api_call";
    public static String TOTAL_COLLECTION="total_collection";
    public static String TOTAL_INVOICE="total_invoice";

    public static boolean isOnline(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assert connectivityManager != null;
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo != null && anInfo.isConnected()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }



    public static boolean checkValidEmail(String email, EditText editEmail, Context context) {
        email = editEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            return false;
        }
        return true;
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;

    }

    public static  String convertTimeForm(Context context,String time){
        SimpleDateFormat inSdf = new SimpleDateFormat("hh:mm:ss",Locale.getDefault());
        try{
            Date date = inSdf.parse(time);
            SimpleDateFormat outSdf = new SimpleDateFormat("hh.mm a",Locale.getDefault());
            return outSdf.format(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return null;
    }


    public static void openDialog(Context context, String title, String mgs) {
        final Dialog openDialog = new Dialog(context, R.style.Theme_AppCompat_Dialog_Alert);
        openDialog.setContentView(R.layout.alert);
        TextView alertTitle = (TextView) openDialog.findViewById(R.id.alertTitle);
        TextView alertDesc = (TextView) openDialog.findViewById(R.id.alertDesc);
        TextView btnOk = (TextView) openDialog.findViewById(R.id.btnOk);
        TextView btnNO = (TextView) openDialog.findViewById(R.id.btnNO);
        alertTitle.setText(title);
        alertDesc.setText(mgs);
        btnNO.setVisibility(View.GONE);
        btnOk.setText("OK");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
            }
        });
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog.dismiss();
            }
        });
        openDialog.show();
    }


    public static final String CHECK_BOX_CHECKED_TRUE = "YES";
    public static final String CHECK_BOX_CHECKED_FALSE = "NO";

    public static   ArrayList<ArrayList<HashMap<String, String>>> childItems = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> parentItems = new ArrayList<>();


    public class Parameter {
        public static final String IS_CHECKED = "is_checked";
        public static final String SUB_CATEGORY_NAME = "sub_category_name";
        public static final String CATEGORY_NAME = "category_name";
        public static final String CATEGORY_ID = "category_id";
        public static final String SUB_ID = "sub_id";
    }



    @SuppressLint("NewApi")
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;

        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static String getAsStringOrNull(Context context,JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? null : jsonElement.getAsString();
    }




    public static String getFullAddress(Context context,double lat, double log) {
        Address locationAddress;
        String currentLocation="";
        locationAddress = getAddress(context,lat, log);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "," + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "," + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "," + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "," + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "," + country;

            }
        } else
            Log.e("Error Message: ", "Something went wrong");
        return currentLocation;
    }

    private static Address getAddress(Context context,double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 5);
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }


    public static void logOut(Activity mActivity){
        SharedPreferences pref = mActivity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        DBHandler db= new DBHandler(mActivity);
        db.deleteAll();
        editor.putString(AppConstant.IS_LOGIN,"");
        editor.apply();
        editor.putString(AppConstant.TOKEN,"");
        editor.apply();
        editor.putString(AppConstant.LOGIN_RESPONSE,"");
        editor.apply();
        editor.putLong(AppConstant.SET_DASH_BOARD_API_CALL,0);
        editor.apply();
        //WorkManager.getInstance(mActivity).cancelAllWork();
        //mActivity.unregisterReceiver(Home.myBroadCastReceiver);
        ComponentName componentName = new ComponentName(mActivity, Home.OwnReceiver.class);
        PackageManager packageManager = mActivity.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        mActivity.stopService(new Intent(mActivity, EndlessService.class));
        Intent launchIntent = new Intent(mActivity, LaunchActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(launchIntent);
        mActivity.finish();
    }

    public static  final String PACKAGE_NAME = "utils";
    public static  final String RESULT_DATA_KEY = PACKAGE_NAME+".RESULT_DATA_KEY";
    public static  final String RECEIVER = PACKAGE_NAME+".RECEIVER";
    public static  final String LOCATION_DATA_EXTRA = PACKAGE_NAME+".LOCATION_DATA_EXTRA";
    public static final int SUCCESS_RESULT =1;
    public static final int FAIL_RESULT =0;

}
