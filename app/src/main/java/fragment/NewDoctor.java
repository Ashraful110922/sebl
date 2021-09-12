package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapter.ChemistAdapter;
import adapter.MarketAdapter;
import adapter.MethodAdapter;
import helper.BaseFragment;
import helper.ClearEditText;
import interfac.ApiService;
import modal.ErrorMessageModal;
import modal.LocationDialog;
import model.ParamMarket;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import service.FetchAddressIntentService;
import utils.AppConstant;
import utils.MySpinner;

import static android.content.Context.MODE_PRIVATE;

public class NewDoctor extends BaseFragment implements LocationDialog.OnChooseReasonListener{
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private MySpinner spMethod;
    private ArrayList<TinyUser> mList = new ArrayList<TinyUser>();
    private LinearLayout viewMethod;
    private TextView btnDoctorSubmit;
    private ClearEditText etDoctorAccount,etDoctorTel;
    private TextInputLayout dcCodeView,hintDCName,hintSpeciality,hintInstitution,hintDesignation,hintCertificate,hintPatientNo,hintProprietor;
    private TextInputEditText etDoctorCode,etDoctorName,etDoctorAddress,etDoctorMobile,etDoctorSpeciality,
            etTypePay,setLoc,etDrInstitution,etDesignation,etCertificate,etPatientNo,etProprietor;
    private CheckBox chAgree;
    private String check = "NotAgree";
    private TinyUser user =new TinyUser();
    private Spinner spMkt;
    private List<ParamMarket> mkList = new ArrayList<ParamMarket>();
    private double latitude=0.00,longitude=0.00;
    private String mktCode="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_new, container, false);
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

        pb = (ProgressBar) getView().findViewById(R.id.pbDNew);
        spMethod = (MySpinner) getView().findViewById(R.id.spMethod);
        viewMethod = (LinearLayout) getView().findViewById(R.id.viewMethod);
        //tvTypePay = (TextView) getView().findViewById(R.id.tvTypePay);
        etTypePay = (TextInputEditText) getView().findViewById(R.id.etTypePay);
        etDoctorCode = (TextInputEditText) getView().findViewById(R.id.etDoctorCode);
        etDoctorName = (TextInputEditText) getView().findViewById(R.id.etDoctorName);
        etDoctorAddress = (TextInputEditText) getView().findViewById(R.id.etDoctorAddress);
        etDoctorAccount = (ClearEditText) getView().findViewById(R.id.etDoctorAccount);
        etDoctorMobile = (TextInputEditText) getView().findViewById(R.id.etDoctorMobile);
        etDoctorTel = (ClearEditText) getView().findViewById(R.id.etDoctorTel);
        etDoctorSpeciality= (TextInputEditText) getView().findViewById(R.id.etDoctorSpeciality);
        setLoc = (TextInputEditText) getView().findViewById(R.id.setLoc);
        chAgree =(CheckBox) getView().findViewById(R.id.chAgree);
        btnDoctorSubmit= (TextView) getView().findViewById(R.id.btnDoctorSubmit);
        dcCodeView = (TextInputLayout) getView().findViewById(R.id.dcCodeView);
        hintDCName = (TextInputLayout) getView().findViewById(R.id.hintDCName);
        hintSpeciality = (TextInputLayout) getView().findViewById(R.id.hintSpeciality);
        hintInstitution = (TextInputLayout) getView().findViewById(R.id.hintInstitution);
        hintDesignation = (TextInputLayout) getView().findViewById(R.id.hintDesignation);
        hintCertificate = (TextInputLayout) getView().findViewById(R.id.hintCertificate);
        hintPatientNo = (TextInputLayout) getView().findViewById(R.id.hintPatientNo);
        hintProprietor = (TextInputLayout) getView().findViewById(R.id.hintProprietor);
        etDrInstitution = (TextInputEditText) getView().findViewById(R.id.etDrInstitution);
        etDesignation = (TextInputEditText) getView().findViewById(R.id.etDesignation);
        etCertificate = (TextInputEditText) getView().findViewById(R.id.etCertificate);
        etPatientNo = (TextInputEditText) getView().findViewById(R.id.etPatientNo);
        etProprietor = (TextInputEditText) getView().findViewById(R.id.etProprietor);
        spMkt = (Spinner) getView().findViewById(R.id.spMkt);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });


        chAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check = "Agree";
                } else {
                    check = "NotAgree";
                }
            }
        });

        mList.clear();
        mList.add(new TinyUser(0, "Select Method"));
        mList.add(new TinyUser(1, "Bank"));
        mList.add(new TinyUser(2, "Bkash"));
        mList.add(new TinyUser(3, "Rocket"));
        mList.add(new TinyUser(4, "Nagad"));
        mList.add(new TinyUser(5, "Sure cash"));

        MethodAdapter adapter = new MethodAdapter(context, mList);
        spMethod.setAdapter(adapter);
        viewMethod.setVisibility(View.GONE);
        spMethod.post(new Runnable() {
            @Override
            public void run() {
                spMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ((position > 0)) {
                            viewMethod.setVisibility(View.VISIBLE);
                            String method = mList.get(position).getName();
                            if (position == 1) {
                                //tvTypePay.setText("Account No");
                                etTypePay.setHint("Account No");
                            } else if (position == 2) {
                                //tvTypePay.setText("Bkash No");
                                etTypePay.setHint("Bkash No");
                            } else if (position == 3) {
                                //tvTypePay.setText("Rocket No");
                                etTypePay.setHint("Rocket No");
                            } else if (position == 4) {
                                //tvTypePay.setText("Nagad No");
                                etTypePay.setHint("Nagad No");
                            } else if (position == 5) {
                                //tvTypePay.setText("Sure cash No");
                                etTypePay.setHint("Sure cash No");
                            }
                        } else {
                            Toast.makeText(context, "Select Method", Toast.LENGTH_SHORT).show();
                            viewMethod.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
        setLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble("LATITUDE",latitude);
                bundle.putDouble("LONGITUDE",longitude);
                assert getActivity() != null;
                LocationDialog editNameDialogSheet = LocationDialog.newInstance();
                editNameDialogSheet.setArguments(bundle);
                editNameDialogSheet.show(getChildFragmentManager(),LocationDialog.TAG);
            }
        });


        btnDoctorSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isOnline(context)){
                    dataVerify();
                }else {
                    AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                }
            }
        });

        if (bundle!=null && Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("dr")){
            user = bundle.getParcelable("SEND_OBJECT");
            toolbar.setTitle("DOCTOR UPDATE");
            btnDoctorSubmit.setText("DOCTOR UPDATE");
            hintProprietor.setVisibility(View.GONE);
            etDoctorCode.setEnabled(false);
            if (user!=null){
                etDoctorCode.setText(String.valueOf(user.getDoctorId()));

                if (!TextUtils.isEmpty(user.getName()))
                    etDoctorName.setText(user.getName());

                if (!TextUtils.isEmpty(user.getAddress()))
                    etDoctorAddress.setText(user.getAddress());

                if (!TextUtils.isEmpty(user.getMobile()))
                    etDoctorMobile.setText(user.getMobile());

                if (!TextUtils.isEmpty(user.getSpeciality()))
                    etDoctorSpeciality.setText(user.getSpeciality());

                if (!TextUtils.isEmpty(user.getInstitude()))
                    etDrInstitution.setText(user.getInstitude());

                if (!TextUtils.isEmpty(user.getDesignation()))
                    etDesignation.setText(user.getDesignation());

                if(!TextUtils.isEmpty(user.getDegree()))
                    etCertificate.setText(user.getDegree());

                if (!TextUtils.isEmpty(user.getNoOfPatient()))
                    etPatientNo.setText(user.getNoOfPatient());

                if (!TextUtils.isEmpty(user.getLatitude()) && Double.parseDouble(user.getLatitude()) >=0.00){
                    latitude = Double.parseDouble(user.getLatitude());
                }

                if (!TextUtils.isEmpty(user.getLongitude()) && Double.parseDouble(user.getLongitude()) >=0.00){
                    longitude = Double.parseDouble(user.getLongitude());
                }

                if (latitude>0.00 && longitude>0.00){
                    Location location = new Location("providerNA");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    ResultReceiver rcv= new AddressResultReceiver(new Handler());
                    Intent intent =  new Intent(MainActivity.getInstance(), FetchAddressIntentService.class);
                    intent.putExtra(AppConstant.RECEIVER,rcv);
                    intent.putExtra(AppConstant.LOCATION_DATA_EXTRA,location);
                    MainActivity.getInstance().startService(intent);
                }

            }
        }else if (bundle!=null && Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("chemist")){
            user = bundle.getParcelable("SEND_OBJECT");
            toolbar.setTitle("CHEMIST UPDATE");
            hintDCName.setHint("Chemist Name");
            btnDoctorSubmit.setText("CHEMIST UPDATE");
            etDoctorCode.setEnabled(false);
            hintProprietor.setVisibility(View.VISIBLE);
            hintSpeciality.setVisibility(View.GONE);
            hintInstitution.setVisibility(View.GONE);
            hintDesignation.setVisibility(View.GONE);
            hintCertificate.setVisibility(View.GONE);
            hintPatientNo.setVisibility(View.GONE);
            if (user!=null){
                etDoctorCode.setText(String.valueOf(user.getChemistID()));
                if (!TextUtils.isEmpty(user.getName()))
                    etDoctorName.setText(user.getName());

                if (!TextUtils.isEmpty(user.getAddress()))
                    etDoctorAddress.setText(user.getAddress());

                if (!TextUtils.isEmpty(user.getMobile()))
                    etDoctorMobile.setText(user.getMobile());

                if (!TextUtils.isEmpty(user.getPropritor()))
                    etProprietor.setText(user.getPropritor());

                if (!TextUtils.isEmpty(user.getLatitude()) && Double.parseDouble(user.getLatitude()) >=0.00){
                    latitude = Double.parseDouble(user.getLatitude());
                }

                if (!TextUtils.isEmpty(user.getLongitude()) && Double.parseDouble(user.getLongitude()) >=0.00){
                    longitude = Double.parseDouble(user.getLongitude());
                }

                if (latitude>0.00 && longitude>0.00){
                    Location location = new Location("providerNA");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    ResultReceiver rcv= new AddressResultReceiver(new Handler());
                    Intent intent =  new Intent(MainActivity.getInstance(), FetchAddressIntentService.class);
                    intent.putExtra(AppConstant.RECEIVER,rcv);
                    intent.putExtra(AppConstant.LOCATION_DATA_EXTRA,location);
                    MainActivity.getInstance().startService(intent);
                }
            }
        }else if (Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("drAdd")){
            dcCodeView.setVisibility(View.GONE);
            hintProprietor.setVisibility(View.GONE);
            hintSpeciality.setVisibility(View.VISIBLE);
            hintInstitution.setVisibility(View.VISIBLE);
            hintDesignation.setVisibility(View.VISIBLE);
            hintCertificate.setVisibility(View.VISIBLE);
            hintPatientNo.setVisibility(View.VISIBLE);
            toolbar.setTitle("ADD DOCTOR");
        }else if (Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("chemistAdd")){
            hintProprietor.setVisibility(View.VISIBLE);
            dcCodeView.setVisibility(View.GONE);
            hintSpeciality.setVisibility(View.GONE);
            hintInstitution.setVisibility(View.GONE);
            hintDesignation.setVisibility(View.GONE);
            hintCertificate.setVisibility(View.GONE);
            hintPatientNo.setVisibility(View.GONE);
            toolbar.setTitle("ADD CHEMIST");
            hintDCName.setHint("Chemist Name");
        }

        if (AppConstant.isOnline(context)){
            getMarketList(pref.getString(AppConstant.TOKEN,""));
        }else {
            AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
        }
        Log.e("Token",String.format("%s",pref.getString(AppConstant.TOKEN,"")));
    }

    private void dataVerify(){
        String dCode,dName,dAddress,dMobile,dSpeciality,proprietor,patientNo,certificate,designation,institution;
        etDoctorCode.setError(null);
        etDoctorName.setError(null);
        etDoctorAddress.setError(null);
        etDoctorMobile.setError(null);
        etDoctorSpeciality.setError(null);
        dCode = etDoctorCode.getText().toString().trim();
        dName = etDoctorName.getText().toString().trim();
        dAddress = etDoctorAddress.getText().toString().trim();
        dMobile = etDoctorMobile.getText().toString().trim();
        dSpeciality= etDoctorSpeciality.getText().toString().trim();
        proprietor = etProprietor.getText().toString().trim();
        patientNo = etPatientNo.getText().toString().trim();
        certificate = etCertificate.getText().toString().trim();
        designation = etDesignation.getText().toString().trim();
        institution = etDrInstitution.getText().toString().trim();

        Bundle error = new Bundle();
        ErrorMessageModal modal = new ErrorMessageModal();
       if(TextUtils.isEmpty(dName)){
           error.putString("ERROR_MESSAGE","Enter name");
           modal.setArguments(error);
           modal.show(getChildFragmentManager(),"showError");
           etDoctorName.requestFocus();
        }else if(TextUtils.isEmpty(dAddress)){
           error.putString("ERROR_MESSAGE","Enter  address");
           modal.setArguments(error);
           modal.show(getChildFragmentManager(),"showError");
           etDoctorAddress.requestFocus();
        }else if(TextUtils.isEmpty(mktCode)){
           error.putString("ERROR_MESSAGE","Select market");
           modal.setArguments(error);
           modal.show(getChildFragmentManager(),"showError");
        }else if(check.equalsIgnoreCase("NotAgree")){
           error.putString("ERROR_MESSAGE","Check Yes/No");
           modal.setArguments(error);
           modal.show(getChildFragmentManager(),"showError");
            //Toast.makeText(context,"Need Check",Toast.LENGTH_SHORT).show();
        }else if(latitude <= 0.00 && longitude <= 0.00){
           error.putString("ERROR_MESSAGE","Select place using google map");
           modal.setArguments(error);
           modal.show(getChildFragmentManager(),"showError");
       } else {
            if ((bundle.getString("EDIT_TYPE")).equals("drAdd")){
               /* if(TextUtils.isEmpty(dSpeciality)){
                    error.putString("ERROR_MESSAGE","Enter speciality");
                    modal.setArguments(error);
                    modal.show(getChildFragmentManager(),"showError");
                    etDoctorSpeciality.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(patientNo)){
                    error.putString("ERROR_MESSAGE","Enter patient number");
                    modal.setArguments(error);
                    modal.show(getChildFragmentManager(),"showError");
                    etPatientNo.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(certificate)){
                    error.putString("ERROR_MESSAGE","Enter certificate name");
                    modal.setArguments(error);
                    modal.show(getChildFragmentManager(),"showError");
                    etCertificate.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(designation)){
                    error.putString("ERROR_MESSAGE","Enter designation");
                    modal.setArguments(error);
                    modal.show(getChildFragmentManager(),"showError");
                    etDesignation.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(institution)){
                    error.putString("ERROR_MESSAGE","Enter institution");
                    modal.setArguments(error);
                    modal.show(getChildFragmentManager(),"showError");
                    etDrInstitution.requestFocus();
                    return;
                }*/
                JsonObject object = new JsonObject();
                object.addProperty("DoctorID",0);
                object.addProperty("DoctorName",dName);
                object.addProperty("Address",dAddress);
                object.addProperty("MobileNo",dMobile);
                object.addProperty("Speciality",dSpeciality);
                object.addProperty("Latitude",latitude);
                object.addProperty("Longitude",longitude);
                object.addProperty("MarketCode",mktCode);
                object.addProperty("Institude",institution);
                object.addProperty("Designation",designation);
                object.addProperty("Degree",certificate);
                object.addProperty("NoOfPatient",patientNo);
              /*  Log.e("MyData","DoctorID: "+0);
                Log.e("MyData","DoctorName: "+dName);
                Log.e("MyData","Address: "+dAddress);
                Log.e("MyData","MobileNo: "+dMobile);
                Log.e("MyData","Speciality: "+dSpeciality);
                Log.e("MyData","Latitude: "+latitude);
                Log.e("MyData","Longitude: "+longitude);
                Log.e("MyData","institution: "+institution);
                Log.e("MyData","Designation: "+designation);
                Log.e("MyData","Degree: "+certificate);
                Log.e("MyData","NoOfPatient: "+patientNo);*/

                setDoctor(pref.getString(AppConstant.TOKEN,""),object);
            }else if(Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("chemistAdd")){
               /* if (TextUtils.isEmpty(proprietor)){
                    error.putString("ERROR_MESSAGE","Enter proprietor name");
                    modal.setArguments(error);
                    modal.show(getChildFragmentManager(),"showError");
                    etProprietor.requestFocus();
                    return;
                }*/
                JsonObject object = new JsonObject();
                object.addProperty("ChemistID",0);
                object.addProperty("ChemistName",dName);
                object.addProperty("Address",dAddress);
                object.addProperty("MobileNo",dMobile);
                object.addProperty("ChemistType","");
                object.addProperty("Latitude",latitude);
                object.addProperty("Longitude",longitude);
                object.addProperty("MarketCode",mktCode);
                object.addProperty("Propritor",proprietor);
               /* Log.e("MyData","ChemistID: "+0);
                Log.e("MyData","ChemistName: "+dName);
                Log.e("MyData","Address: "+dAddress);
                Log.e("MyData","MobileNo: "+dMobile);
                Log.e("MyData","ChemistType: "+"");
                Log.e("MyData","Latitude: "+latitude);
                Log.e("MyData","Longitude: "+longitude);
                Log.e("MyData","Propritor: "+proprietor);*/
                setChemist(pref.getString(AppConstant.TOKEN,""),object);
                //Log.e("Response",pref.getString(AppConstant.TOKEN,""));
                //Log.e("Response",new Gson().toJson(object));
            }else if(bundle!=null && Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("dr")){
                /*if(TextUtils.isEmpty(dSpeciality)){
                    error.putString("ERROR_MESSAGE","Enter speciality");
                    modal.setArguments(error);
                    modal.show(getChildFragmentManager(),"showError");
                    etDoctorSpeciality.requestFocus();
                    return;
                }*/
                JsonObject object = new JsonObject();
                object.addProperty("DoctorID",dCode);
                object.addProperty("DoctorName",dName);
                object.addProperty("Address",dAddress);
                object.addProperty("MobileNo",dMobile);
                object.addProperty("Speciality",dSpeciality);
                object.addProperty("MarketCode",mktCode);
                object.addProperty("Latitude",latitude);
                object.addProperty("Longitude",longitude);
                object.addProperty("Institude",institution);
                object.addProperty("Designation",designation);
                object.addProperty("Degree",certificate);
                object.addProperty("NoOfPatient",patientNo);
                /*Log.e("MyData","DoctorID: "+dCode);
                Log.e("MyData","DoctorName: "+dName);
                Log.e("MyData","Address: "+dAddress);
                Log.e("MyData","MobileNo: "+dMobile);
                Log.e("MyData","Speciality: "+dSpeciality);
                Log.e("MyData","Latitude: "+latitude);
                Log.e("MyData","Longitude: "+longitude);
                Log.e("MyData","institution: "+institution);
                Log.e("MyData","Designation: "+designation);
                Log.e("MyData","Degree: "+certificate);
                Log.e("MyData","NoOfPatient: "+patientNo);*/
               // Log.e("MyData",mktCode);
                setDoctor(pref.getString(AppConstant.TOKEN,""),object);
            }else if(bundle!=null && Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("chemist")){
                JsonObject object = new JsonObject();
                object.addProperty("ChemistID",dCode);
                object.addProperty("ChemistName",dName);
                object.addProperty("Address",dAddress);
                object.addProperty("MobileNo",dMobile);
                object.addProperty("ChemistType","");
                object.addProperty("Latitude",latitude);
                object.addProperty("Longitude",longitude);
                object.addProperty("MarketCode",mktCode);
                object.addProperty("Propritor",proprietor);
                //Log.e("MyData","Market code: "+mktCode);
                //Log.e("MyData","longitude: "+longitude);
                 setChemist(pref.getString(AppConstant.TOKEN,""),object);
            }
            }
        }

    private void setDoctor(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setDoctor(header,object);
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
                        getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
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

    private void setChemist(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setChemist(header,object);
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
                        getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
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

    @Override
    public void onChooseReason(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        Location location = new Location("providerNA");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        ResultReceiver rcv= new AddressResultReceiver(new Handler());
        Intent intent =  new Intent(MainActivity.getInstance(), FetchAddressIntentService.class);
        intent.putExtra(AppConstant.RECEIVER,rcv);
        intent.putExtra(AppConstant.LOCATION_DATA_EXTRA,location);
        MainActivity.getInstance().startService(intent);
    }

    private class  AddressResultReceiver extends ResultReceiver{
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == AppConstant.SUCCESS_RESULT){
                setLoc.setText(resultData.getString(AppConstant.RESULT_DATA_KEY));
            }else {
                Toast.makeText(context,resultData.getString(AppConstant.RESULT_DATA_KEY),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getMarketList(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getMarketList(header);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                if (response.code() ==200 && serverResponse!=null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    mkList.clear();
                    if (status && serverResponse.has("markets") && serverResponse.get("markets").getAsJsonArray().size()>0){
                        TypeToken<ArrayList<ParamMarket>> token = new TypeToken<ArrayList<ParamMarket>>() {};
                        mkList = new Gson().fromJson(serverResponse.get("markets").getAsJsonArray(), token.getType());
                        mkList.add(0,new ParamMarket("0","Select market"));
                        mkList.add(mkList.size(),new ParamMarket("","Add new market"));
                        MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                        spMkt.setAdapter(marketAdapter);
                        spMkt.post(new Runnable() {
                            @Override
                            public void run() {
                                spMkt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position>0){
                                            if (mkList.get(position).getName().equalsIgnoreCase("Add new market")){
                                                NewMarket market = new NewMarket();
                                                Bundle ars = new Bundle();
                                                ars.putString("TYPE_MARKET","marketAdd");
                                                market.setArguments(ars);
                                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                myCommunicator.setContentFragment(market, true);
                                            }else {
                                                mktCode = mkList.get(position).getCode();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        mktCode = "";

                                    }
                                });
                            }
                        });
                        if(bundle!=null && Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("dr") || bundle!=null && Objects.requireNonNull(bundle.getString("EDIT_TYPE")).equals("chemist")){
                            spMkt.setSelection(indexOfAnimal(mkList,user.getMarketCode()));
                            mktCode =user.getMarketCode();
                        }

                    }else {
                        mkList.add(0,new ParamMarket("0","Select market"));
                        mkList.add(mkList.size(),new ParamMarket("","Add new market"));

                        MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                        spMkt.setAdapter(marketAdapter);
                        spMkt.post(new Runnable() {
                            @Override
                            public void run() {
                                spMkt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position>0){
                                            if (mkList.get(position).getName().equalsIgnoreCase("Add new market")){
                                                NewMarket market = new NewMarket();
                                                Bundle ars = new Bundle();
                                                ars.putString("TYPE_MARKET","marketAdd");
                                                market.setArguments(ars);
                                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                myCommunicator.setContentFragment(market, true);
                                            }else {
                                                mktCode = mkList.get(position).getCode();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        mktCode = "";
                                    }
                                });
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

    private int indexOfAnimal(List<ParamMarket> iList, String code){
        for (ParamMarket item : iList){
            if (item.getCode().contains(code)) {
                return iList.indexOf(item);
            }
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



