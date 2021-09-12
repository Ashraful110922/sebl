package fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opl.one.oplsales.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Objects;

import helper.BaseFragment;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import utils.AppConstant;
import utils.Utils;
import static android.content.Context.MODE_PRIVATE;

public class Profile extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static File dir = null;
    private int width, REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask = "", strImagePath = "";
    private int MAX_IMAGE_DIMENSION = 1024;
    private TextView tvStaffName,tv_address,tvNumberDr,tvNumberChemist,tvStaffMobile,tvTerritory,tvDesignation,tvStaffCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        intUit();
    }

    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("PROFILE");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = (displayMetrics.widthPixels) / 4;

        tvStaffName = (TextView) getView().findViewById(R.id.tvStaffName);
        tv_address = (TextView) getView().findViewById(R.id.tv_address);
        tvNumberDr = (TextView) getView().findViewById(R.id.tvNumberDr);
        tvNumberChemist = (TextView) getView().findViewById(R.id.tvNumberChemist);
        tvStaffMobile = (TextView) getView().findViewById(R.id.tvStaffMobile);
        tvTerritory = (TextView) getView().findViewById(R.id.tvTerritory);
        tvDesignation = (TextView) getView().findViewById(R.id.tvDesignation);
        tvStaffCode = (TextView) getView().findViewById(R.id.tvStaffCode);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        JsonParser parser =  new JsonParser();
        if (!TextUtils.isEmpty(pref.getString(AppConstant.LOGIN_RESPONSE, ""))){
            JsonObject object = (JsonObject) parser.parse(Objects.requireNonNull(pref.getString(AppConstant.LOGIN_RESPONSE, "")));

            if (object!=null){
                if (object.get("MIOName")!=null && !TextUtils.isEmpty(object.get("MIOName").getAsString()))
                    tvStaffName.setText(object.get("MIOName").getAsString());
                else
                    tvStaffName.setText("N/A");

                if (object.get("MobileNo")!=null && !TextUtils.isEmpty(object.get("MobileNo").getAsString()))
                    tvStaffMobile.setText(object.get("MobileNo").getAsString());
                else
                    tvStaffMobile.setText("N/A");

                if(object.get("POSTING_LOCATION")!=null && !TextUtils.isEmpty(object.get("POSTING_LOCATION").getAsString())){
                    String type = object.get("POSTING_LOCATION").getAsString();
                    switch (type) {
                        case "Z":
                            String zone= object.get("ZoneName")!=null ? object.get("ZoneName").getAsString():"";
                            tv_address.setText(String.format("%s", zone));
                            break;
                        case "D":
                            String depot=object.get("DepotName")!=null ? object.get("DepotName").getAsString():"";
                            tv_address.setText(String.format("%s","Posting :" + depot));
                            break;
                        case "R":
                            String region = object.get("RegionName")!=null ? object.get("RegionName").getAsString():"";
                            tv_address.setText(String.format("%s","Posting :" + region));
                            break;
                        case "A":
                            String area = object.get("AreaName")!=null ? object.get("AreaName").getAsString():"";
                            tv_address.setText(String.format("%s","Posting :" + area));
                            break;
                        case "T":
                            String territory= object.get("TerritoryName")!=null ? object.get("TerritoryName").getAsString():"";
                            tv_address.setText(String.format("%s","Posting :" + territory));
                            break;
                    }
                }
                if (object.get("Designation")!=null && !TextUtils.isEmpty(object.get("Designation").getAsString()))
                    tvDesignation.setText(object.get("Designation").getAsString());
                else
                    tvDesignation.setText("N/A");

                if (object.get("MIOCode")!=null && !TextUtils.isEmpty(object.get("MIOCode").getAsString()))
                    tvStaffCode.setText(object.get("MIOCode").getAsString());
                else
                    tvStaffCode.setText("N/A");

                if ( (object.get("ZoneName")!=null && !TextUtils.isEmpty(object.get("ZoneName").getAsString())) && (object.get("DepotName")!=null && !TextUtils.isEmpty(object.get("DepotName").getAsString())) && ( object.get("RegionName")!=null && !TextUtils.isEmpty(object.get("RegionName").getAsString()))
                        && (object.get("AreaName")!=null && !TextUtils.isEmpty(object.get("AreaName").getAsString())) &&(object.get("TerritoryName")!=null && !TextUtils.isEmpty(object.get("TerritoryName").getAsString())) ){
                    tvTerritory.setText(String.format("%s",object.get("ZoneName").getAsString()+"->"+object.get("DepotName").getAsString()+"->"+object.get("RegionName").getAsString()+"->"+object.get("AreaName").getAsString()+"->"+object.get("TerritoryName").getAsString()));
                }else if((object.get("ZoneName")!=null && !TextUtils.isEmpty(object.get("ZoneName").getAsString())) && (object.get("DepotName")!=null &&!TextUtils.isEmpty(object.get("DepotName").getAsString())) && (object.get("RegionName")!=null && !TextUtils.isEmpty(object.get("RegionName").getAsString()))
                        && (object.get("AreaName")!=null && !TextUtils.isEmpty(object.get("AreaName").getAsString())) && (object.get("TerritoryName")==null ||TextUtils.isEmpty(object.get("TerritoryName").getAsString())) ){
                    tvTerritory.setText(String.format("%s",object.get("ZoneName").getAsString()+"->"+object.get("DepotName").getAsString()+"->"+object.get("RegionName").getAsString()+"->"+object.get("AreaName").getAsString()));
                }else if((object.get("ZoneName")!=null && !TextUtils.isEmpty(object.get("ZoneName").getAsString())) && (object.get("DepotName")!=null && !TextUtils.isEmpty(object.get("DepotName").getAsString())) && (object.get("RegionName")!=null && !TextUtils.isEmpty(object.get("RegionName").getAsString()))
                        && (object.get("AreaName")==null || TextUtils.isEmpty(object.get("AreaName").getAsString())) && (object.get("TerritoryName")==null || TextUtils.isEmpty(object.get("TerritoryName").getAsString()))){
                    tvTerritory.setText(String.format("%s",object.get("ZoneName").getAsString()+"->"+object.get("DepotName").getAsString()+"->"+object.get("RegionName").getAsString()));
                }else if((object.get("ZoneName")!=null && !TextUtils.isEmpty(object.get("ZoneName").getAsString())) && (object.get("DepotName")!=null && !TextUtils.isEmpty(object.get("DepotName").getAsString())) && (object.get("RegionName")==null || TextUtils.isEmpty(object.get("RegionName").getAsString()))
                        && (object.get("AreaName")==null || TextUtils.isEmpty(object.get("AreaName").getAsString())) && (object.get("TerritoryName")==null || TextUtils.isEmpty(object.get("TerritoryName").getAsString()))){
                    tvTerritory.setText(String.format("%s",object.get("ZoneName").getAsString()+"->"+object.get("DepotName").getAsString()));
                }else if((object.get("ZoneName")!=null && !TextUtils.isEmpty(object.get("ZoneName").getAsString())) && (object.get("DepotName")==null || TextUtils.isEmpty(object.get("DepotName").getAsString())) && (object.get("RegionName")==null || TextUtils.isEmpty(object.get("RegionName").getAsString()))
                        && (object.get("AreaName")==null || TextUtils.isEmpty(object.get("AreaName").getAsString())) && (object.get("TerritoryName")==null || TextUtils.isEmpty(object.get("TerritoryName").getAsString()))){
                    tvTerritory.setText(String.format("%s",object.get("ZoneName").getAsString()));
                }

                if (object.get("TotalDoctor")!=null && !TextUtils.isEmpty(object.get("TotalDoctor").getAsString()))
                    tvNumberDr.setText(String.format("%s",object.get("TotalDoctor").getAsString()));
                else
                    tvNumberDr.setText("N/A");

                if (object.get("TotalChemist")!=null && !TextUtils.isEmpty(object.get("TotalChemist").getAsString()))
                    tvNumberChemist.setText(String.format("%s",object.get("TotalChemist").getAsString()));
                else
                    tvNumberChemist.setText("N/A");
            }
        }

    }

    private void selectImage() {
        final String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OPLSales";
        dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
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
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    private void onCaptureImageResult(Intent data) {
        try {
            final Bundle extras = data.getExtras();
            final Bitmap b = (Bitmap) extras.get("data");
            final long time = System.currentTimeMillis();
            final Bitmap bit = getResizedBitmap(b, (width * 3) / 4, (width * 3) / 4);
            strImagePath = saveBitmapIntoSdcard(bit, time + ".png");
            //profilePic.setImageBitmap(bit);
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
        createBaseDirctory();
        try {
            new Date();
            OutputStream out = null;
            File file = new File(this.dir, "/" + filename);
            if (file.exists()) {
                file.delete();
            }
            out = new FileOutputStream(file);
            bitmap22.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createBaseDirctory() {
        final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        dir = new File(extStorageDirectory + "/onePharma/");
        if (dir.mkdir()) {
            System.out.println("Directory created");
        } else {
            System.out.println("Directory is not created or exists");
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        try {
            final Uri selectedImageUri = data.getData();
            final long time = System.currentTimeMillis();
            final Bitmap temp = getCorrectlyOrientedImage(selectedImageUri);
            final Bitmap bit = getResizedBitmap(temp, (width * 3) / 4, (width * 3) / 4);
            strImagePath = saveBitmapIntoSdcard(bit, time + ".png");
            //profilePic.setImageBitmap(bit);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getCorrectlyOrientedImage(Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();
        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(photoUri);
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }
        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);
            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }
        return srcBitmap;
    }

    public int getOrientation(Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            }
        }
    }

    private RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }


}



