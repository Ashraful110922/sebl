package fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.opl.one.oplsales.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import adapter.BrandAdapter;
import helper.BaseFragment;
import interfac.ApiService;
import model.Brand;
import model.Temp;
import model.TinyUser;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;
import utils.Utils;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;

public class DoctorVisited extends BaseFragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private ImageView takeImage;
    private static File dir = null;
    private int width, REQUEST_CAMERA = 789;
    private String userChoosenTask = "", strImagePath ;
    private int MAX_IMAGE_DIMENSION = 1024;
    private FusedLocationProviderClient mFusedLocationClient;
    private double lat=23.7595338, lng=90.3897478;
    private static String currentLocation;
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;
    private GoogleApiClient googleApiClient;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private TextInputLayout tilOrderValue,tilCollectValue;
    private TextInputEditText visitComments,etOrderVal,etCollVal;
    private RecyclerView mRecyclerView;

    private List<Brand> list = new ArrayList<Brand>();

    private Calendar current;
    private SimpleDateFormat tf,df;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_visit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);

        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        googleApiClient = new GoogleApiClient.Builder(context).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
        bundle = this.getArguments();
        intUit();
    }


    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.save_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_checkVisit) {
                    dataVerify();
                    return true;
                }
                return false;
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = (displayMetrics.widthPixels);
        Typeface ceRaProBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CeraProBold.otf");
        Typeface ceRaProRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CeraProRegular.otf");
        pb = (ProgressBar) getView().findViewById(R.id.pbDoctor);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recMOrPItem);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridVertical);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        takeImage = (ImageView) getView().findViewById(R.id.takeImage);
        visitComments = (TextInputEditText) getView().findViewById(R.id.visitComments);
        tilOrderValue = (TextInputLayout) getView().findViewById(R.id.tilOrderValue);
        tilCollectValue = (TextInputLayout) getView().findViewById(R.id.tilCollectValue);
        etOrderVal = (TextInputEditText) getView().findViewById(R.id.etOrderVal);
        etCollVal = (TextInputEditText) getView().findViewById(R.id.etCollVal);

        if (bundle!=null && "doctor".equals(bundle.getString("EXECUTE_TYPE")) || (bundle!=null && "doctor market".equals(bundle.getString("EXECUTE_TYPE")))|| (bundle!=null && "emp market".equals(bundle.getString("EXECUTE_TYPE")))){
            toolbar.setTitle("DOCTOR EXECUTE");
            tilOrderValue.setVisibility(View.GONE);
            tilCollectValue.setVisibility(View.GONE);
        }else {
            toolbar.setTitle("CUSTOMER EXECUTE");
            //tilOrderValue.setVisibility(View.VISIBLE);
            //.setVisibility(View.VISIBLE);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        list.clear();
        initData();
       BrandAdapter adapter = new BrandAdapter(context, list, new BrandAdapter.MyAdapterListener() {
           @Override
           public void OnChangeValue(JsonObject reportType) {
               Log.e("response<<", new GsonBuilder().serializeNulls().create().toJson(reportType));
           }
        });
        mRecyclerView.setAdapter(adapter);

    }

    private void initData() {
        String sectionOneName = "Brand1";
        List<Temp> sectionOneItems = new ArrayList<>();
        for (int i=0 ; i <= 9; i++){
            Temp user = new Temp();
            user.setName("Name: "+i);
            sectionOneItems.add(user);
        }

        String sectionTwoName = "Brand2";
        List<Temp> sectionTwoItems = new ArrayList<>();
        for (int i=0 ; i <= 9; i++){
            Temp user = new Temp();
            user.setName("Name: "+i);
            sectionTwoItems.add(user);
        }

        String sectionThreeName = "Brand3";
        List<Temp> sectionThreeItems = new ArrayList<>();
        for (int i=0 ; i <= 9; i++){
            Temp user = new Temp();
            user.setName("Name: "+i);
            sectionThreeItems.add(user);
        }

        String sectionFourName = "Brand4";
        List<Temp> sectionFourItems = new ArrayList<>();
        for (int i=0 ; i <= 9; i++){
            Temp user= new Temp();
            user.setName("Name: "+i);
            sectionFourItems.add(user);
        }

        list.add(new Brand(sectionOneName, sectionOneItems));
        list.add(new Brand(sectionTwoName, sectionTwoItems));
        list.add(new Brand(sectionThreeName, sectionThreeItems));
        list.add(new Brand(sectionFourName, sectionFourItems));

    }

    private void dataVerify() {
        String doctorId="0",rosterId="0",mktId="",comments,orderVal,collVal;
        comments = visitComments.getText().toString().trim();
        orderVal = etOrderVal.getText().toString().trim();
        collVal = etCollVal.getText().toString().trim();
        RequestBody dId = null, latT = null, lonT = null, address = null,opinion=null,tBody=null,order=null,coll=null,rId=null,mId=null,date=null;
        File imageFile = null;
        MultipartBody.Part photo = null;
        if (!TextUtils.isEmpty(strImagePath)){
                imageFile = new File(strImagePath);
                photo = MultipartBody.Part.createFormData("ImageUrl", imageFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), imageFile));
        }else {
            Toast.makeText(context,"Please take picture using camera",Toast.LENGTH_SHORT).show();
            return;
        }


        if (bundle!=null && !TextUtils.isEmpty(bundle.getString("dId"))){
            doctorId = bundle.getString("dId");
        }

        if (bundle!=null && !TextUtils.isEmpty(bundle.getString("rId"))){
            rosterId = bundle.getString("rId");
        }

        if (bundle!=null && !TextUtils.isEmpty(bundle.getString("mId"))){
            mktId = bundle.getString("mId");
        }

        if (!TextUtils.isEmpty(doctorId)) {
            dId = toRequestBody(doctorId);
        }

        if (!TextUtils.isEmpty(rosterId)) {
            rId = toRequestBody(rosterId);
        }

        if (!TextUtils.isEmpty(mktId)) {
            mId = toRequestBody(mktId);
        }

        if (!TextUtils.isEmpty(String.valueOf(lat))) {
            latT = toRequestBody(String.valueOf(lat));
        } else {
            latT = toRequestBody("");
        }
        if (!TextUtils.isEmpty(String.valueOf(lng))) {
            lonT = toRequestBody(String.valueOf(lng));
        } else {
            lonT = toRequestBody("");
        }
        current=Calendar.getInstance();

        tf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String tt= tf.format(current.getTime());
        String dd = df.format(current.getTime());

        if (!TextUtils.isEmpty(tt)) {
            tBody = toRequestBody(tt);
        } else {
            tBody = toRequestBody("");
        }
        if (!TextUtils.isEmpty(currentLocation)) {
            address = toRequestBody(currentLocation);
        } else {
            address = toRequestBody("");
        }

        if (!TextUtils.isEmpty(comments)){
            opinion = toRequestBody(comments);
        }else {
            opinion = toRequestBody("");
        }


        if (!TextUtils.isEmpty(orderVal)){
            order = toRequestBody(orderVal);
        }else {
            order = toRequestBody("");
        }
        if (!TextUtils.isEmpty(collVal)){
            coll = toRequestBody(collVal);
        }else {
            coll = toRequestBody("");
        }


        if (!TextUtils.isEmpty(dd)) {
            date = toRequestBody(dd);
        }

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("VisitTime", tBody);
        map.put("Latitude",latT);
        map.put("Longitude",lonT);
        map.put("Remarks",opinion);
        map.put("LLAddress",address);
        if (AppConstant.isOnline(context)) {
            if (bundle!=null && "doctor".equals(bundle.getString("EXECUTE_TYPE"))){
                map.put("PlanID", dId);
                //Log.e("response",bundle.getString("dId"));
                //Log.e("response",pref.getString(AppConstant.TOKEN,""));
                updatePlanDoctor(pref.getString(AppConstant.TOKEN,""),photo,map);
            }else if(bundle!=null && "chemist".equals(bundle.getString("EXECUTE_TYPE"))){
                map.put("PlanID", dId);
                map.put("InvoiceAmount",order);
                map.put("CollectionAmount",coll);
                updatePlanChemist(pref.getString(AppConstant.TOKEN,""),photo,map);
            }else if(bundle!=null && "doctor market".equals(bundle.getString("EXECUTE_TYPE"))){
                map.put("RosterID",rId);
                map.put("DoctorID",dId);
                map.put("MarketScheduleID",mId);
                map.put("visitDate",date);
                /*Log.e("MyData","Current Time: "+tt);
                Log.e("MyData","Current Date: "+dd);
                Log.e("MyData","Latitude: "+lat);
                Log.e("MyData","Longitude: "+lng);
                Log.e("MyData","Remarks: "+comments);
                Log.e("MyData","LLAddress: "+currentLocation);
                Log.e("MyData","Doctor ID: "+doctorId);
                Log.e("MyData","RosterId: "+rosterId);
                Log.e("MyData","MarketId: "+mktId);*/
                planSetExecuteDoctor(pref.getString(AppConstant.TOKEN,""),photo,map);
            }else if(bundle!=null && "chemist market".equals(bundle.getString("EXECUTE_TYPE"))){
                map.put("RosterID",rId);
                map.put("ChemistID",dId);
                map.put("MarketScheduleID",mId);
                map.put("visitDate",date);
                map.put("InvoiceAmount",order);
                map.put("CollectionAmount",coll);
            /*    Log.e("MyData","Current Time: "+tt);
                Log.e("MyData","Current Date: "+dd);
                Log.e("MyData","Latitude: "+lat);
                Log.e("MyData","Longitude: "+lng);
                Log.e("MyData","Remarks: "+comments);
                Log.e("MyData","LLAddress: "+currentLocation);
                Log.e("MyData","ChemistId: "+doctorId);
                Log.e("MyData","RosterId: "+rosterId);
                Log.e("MyData","MarketId: "+mktId);*/
                planSetExecuteChemist(pref.getString(AppConstant.TOKEN,""),photo,map);
            }else if (bundle!=null && "emp market".equals(bundle.getString("EXECUTE_TYPE"))){
                map.put("RosterID",rId);
                map.put("EmpCode",dId);
                map.put("MarketScheduleID",mId);
                map.put("visitDate",date);
               /* Log.e("MyData","Current Time: "+tt);
                Log.e("MyData","Current Date: "+dd);
                Log.e("MyData","Latitude: "+lat);
                Log.e("MyData","Longitude: "+lng);
                Log.e("MyData","Remarks: "+comments);
                Log.e("MyData","LLAddress: "+currentLocation);
                Log.e("MyData","EmpCode: "+doctorId);
                Log.e("MyData","RosterId: "+rosterId);
                Log.e("MyData","MarketScheduleID: "+mktId);*/
                planSetExecuteEmp(pref.getString(AppConstant.TOKEN,""),photo,map);

            }
        } else {
            AppConstant.openDialog(context, "No Internet", context.getResources().getString(R.string.internet_error));
        }
    }

    private void selectImage() {
        final String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/onePharma";
        dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final CharSequence[] items = {"Take Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utils.checkPermission(context);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                }  else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            startActivityForResult(intent, REQUEST_CAMERA);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }

    }


    private void onCaptureImageResult(Intent data) {
        try {
            final Bundle extras = data.getExtras();
            final Bitmap b = (Bitmap) extras.get("data");
            final long time = System.currentTimeMillis();
            final Bitmap bit = getResizedBitmap(b, (width ) , (width) );
            strImagePath = saveBitmapIntoSdcard(bit, time + ".png");
            Log.e("MyData","Path: Path:,,,,,"+strImagePath);
            takeImage.setImageBitmap(bit);
            saveBitmapIntoSdcard(bit, time + ".png");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        final int width = bm.getWidth();
        final int height = bm.getHeight();
        final float scaleWidth = (float) newWidth / width;
        final float scaleHeight = (float) newHeight / height;
        final Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private String saveBitmapIntoSdcard(Bitmap bitmap22, String filename) throws IOException {
        createBaseDirectory();
        File file=null;
        try {
            new Date();
            OutputStream out = null;
             file = new File(this.dir, "/" + filename);
            if (file.exists()) {
                file.delete();
            }
            out = new FileOutputStream(file);
            bitmap22.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            //Log.e("MyData",">>>"+file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (final Exception e) {
            e.printStackTrace();
        }
       return null;
    }
    public void createBaseDirectory() {
        final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        dir = new File(extStorageDirectory + "/onePharma/");
        if (dir.mkdir()) {
            System.out.println("Directory created");
        } else {
            System.out.println("Directory is not created or exists");
        }
        //strImagePath = dir.getPath();
        //Log.e("MyData",">>>"+strImagePath);
    }

   /* public void createBaseDirctory() {
        final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        Log.e("MyData","<><>"+extStorageDirectory);
        dir = new File(extStorageDirectory + "/onePharma");
        if (dir.mkdir()) {
            System.out.println("Directory created");
        } else {
            System.out.println("Directory is not created or exists");
        }
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(),new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            Log.e("location",""+lat);
                            Log.e("location",""+lng);
                            currentLocation= AppConstant.getFullAddress(context,lat,lng);
                            //Log.e("currentLocation","<>Check location<>"+currentLocation);
                        }
                    }
                });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
    }


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(context).setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            Log.e("warning: ", "You need to install Google Play Services to use the App properly");
        }
    }

    private RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void updatePlanDoctor(String header, MultipartBody.Part image, HashMap<String, RequestBody> map) {
        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.updatePlanDoctor(header,image,map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response", new Gson().toJson(serverResponse));
                if(response.code() == 200 && serverResponse!= null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if (status) {
                        getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
                //Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Error: ", t.getMessage());
            }
        });
    }
    private void updatePlanChemist(String header, MultipartBody.Part image, HashMap<String, RequestBody> map) {
        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.updatePlanChemist(header,image,map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                //Log.e("response", new Gson().toJson(serverResponse));
                if(response.code() == 200 && serverResponse!= null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if (status) {
                        getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
                //Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Error: ", t.getMessage());
            }
        });
    }
    private void planSetExecuteDoctor(String header, MultipartBody.Part image, HashMap<String, RequestBody> map) {
        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.planSetExecuteDoctor(header,image,map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response", new Gson().toJson(serverResponse));
                if(response.code() == 200 && serverResponse!= null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if (status) {
                        getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
                Log.e("Error: ", t.getMessage());
            }
        });
    }
    private void planSetExecuteChemist(String header, MultipartBody.Part image, HashMap<String, RequestBody> map) {
        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.planSetExecuteChemist(header,image,map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response", new Gson().toJson(serverResponse));
                if(response.code() == 200 && serverResponse!= null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if (status) {
                        getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
                Log.e("Error: ", t.getMessage());
            }
        });
    }

    private void planSetExecuteEmp(String header, MultipartBody.Part image, HashMap<String, RequestBody> map) {
        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.planSetExecuteEmp(header,image,map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response", new Gson().toJson(serverResponse));
                if(response.code() == 200 && serverResponse!= null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if (status) {
                        getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
                Log.e("Error: ", t.getMessage());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.save_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

   /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_checkVisit:
                Toast.makeText(context,"This is click item",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context =null;
    }
}



