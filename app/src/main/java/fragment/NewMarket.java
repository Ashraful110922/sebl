package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
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
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;
import helper.BaseFragment;
import interfac.ApiService;
import modal.ErrorMessageModal;
import modal.LocationDialog;
import model.ParamMarket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import service.FetchAddressIntentService;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;

public class NewMarket extends BaseFragment implements LocationDialog.OnChooseReasonListener{
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private CheckBox chAgree;
    private String check = "NotAgree";
    private double latitude=0.00,longitude=0.00;
    private TextView btnMkt;
    private TextInputLayout viewMarketCode;
    private TextInputEditText setMarket,etMktCode,etMktName,etMktAddress;
    private ParamMarket market= new ParamMarket();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.market_new, container, false);
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

        pb = (ProgressBar) getView().findViewById(R.id.pbMktNew);
        viewMarketCode = (TextInputLayout) getView().findViewById(R.id.viewMarketCode);
        chAgree =(CheckBox) getView().findViewById(R.id.mktAgree);
        etMktCode = (TextInputEditText)getView().findViewById(R.id.etMktCode);
        setMarket = (TextInputEditText) getView().findViewById(R.id.setMarket);
        etMktName = (TextInputEditText) getView().findViewById(R.id.etMktName);
        etMktAddress = (TextInputEditText) getView().findViewById(R.id.etMktAddress);
        btnMkt = (TextView) getView().findViewById(R.id.btnMkt);

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

        setMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
               if (latitude>0.00 && longitude>0.00){
                   bundle.putDouble("LATITUDE",latitude);
                   bundle.putDouble("LONGITUDE",longitude);
               }
                assert getActivity() != null;
                LocationDialog editNameDialogSheet = LocationDialog.newInstance();
                editNameDialogSheet.setArguments(bundle);
                editNameDialogSheet.show(getChildFragmentManager(),LocationDialog.TAG);

            }
        });

        if (bundle!=null){
            if (bundle.getString("TYPE_MARKET").equals("marketAdd")){
                viewMarketCode.setVisibility(View.GONE);
                toolbar.setTitle("MARKET ADD");
            } else if (bundle.getString("TYPE_MARKET").equals("marketEdit")) {
                market = bundle.getParcelable("SEND_OBJECT");
                viewMarketCode.setVisibility(View.VISIBLE);
                etMktCode.setEnabled(false);
                if (!TextUtils.isEmpty(market.getCode())){
                    etMktCode.setText(market.getCode());
                }
                if (!TextUtils.isEmpty(market.getName())){
                    etMktName.setText(market.getName());
                }

                if ( !TextUtils.isEmpty(market.getLatitude()) && Double.parseDouble(market.getLatitude()) >=0.00){
                    latitude = Double.parseDouble(market.getLatitude());
                }

                if (!TextUtils.isEmpty(market.getLongitude()) &&Double.parseDouble(market.getLongitude()) >= 0.00){
                    longitude = Double.parseDouble(market.getLongitude());
                }
                if (!TextUtils.isEmpty(market.getAddress())){
                    etMktAddress.setText(market.getAddress());
                }
                //Log.e("MyData","Latitude: "+latitude);
                //Log.e("MyData","Longitude: "+longitude);
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

        }


        btnMkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isOnline(context)){
                    dataVerify();
                }else {
                    AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                }
            }
        });
        Log.e("MyData",String.format("%s",pref.getString(AppConstant.TOKEN,"")));
    }

    private void dataVerify() {
        String mktName,mktAddress,llAddress;
        etMktName.setError(null);
        etMktAddress.setError(null);
        mktName= etMktName.getText().toString().trim();
        llAddress = setMarket.getText().toString().trim();
        mktAddress = etMktAddress.getText().toString().trim();
        Bundle error = new Bundle();
        ErrorMessageModal modal = new ErrorMessageModal();

        if(TextUtils.isEmpty(mktName)){
            error.putString("ERROR_MESSAGE","Enter name");
            modal.setArguments(error);
            modal.show(getChildFragmentManager(),"showError");
            etMktName.requestFocus();
        }else if(check.equalsIgnoreCase("NotAgree")){
            error.putString("ERROR_MESSAGE","Check Yes/No");
            modal.setArguments(error);
            modal.show(getChildFragmentManager(),"showError");
        }else if(latitude <= 0.00 && longitude <= 0.00){
            error.putString("ERROR_MESSAGE","Select place using google map");
            modal.setArguments(error);
            modal.show(getChildFragmentManager(),"showError");
        }else {
            if ((bundle.getString("TYPE_MARKET")).equals("marketAdd")){
                JsonObject object = new JsonObject();
                object.addProperty("MarketId",0);
                object.addProperty("Name",mktName);
                if (!TextUtils.isEmpty(mktAddress)){
                    object.addProperty("Address",mktAddress);
                }else {
                    object.addProperty("Address",llAddress);
                }
                object.addProperty("Latitude",latitude);
                object.addProperty("Longitude",longitude);
                setMarket(pref.getString(AppConstant.TOKEN,""),object);
                //Log.e("Response",new Gson().toJson(object));
            }else if((bundle.getString("TYPE_MARKET")).equals("marketEdit")){
                JsonObject object = new JsonObject();
                object.addProperty("MarketId",market.getMarketId());
                object.addProperty("Name",mktName);
                if (!TextUtils.isEmpty(mktAddress)){
                    object.addProperty("Address",mktAddress);
                }else {
                    object.addProperty("Address",llAddress);
                }
                object.addProperty("Latitude",latitude);
                object.addProperty("Longitude",longitude);
                //Log.e("Response",new Gson().toJson(object));
                setMarket(pref.getString(AppConstant.TOKEN,""),object);

            }
        }
    }


    private void setMarket(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setMarket(header,object);
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
                setMarket.setText(resultData.getString(AppConstant.RESULT_DATA_KEY));
            }else {
                Toast.makeText(context,resultData.getString(AppConstant.RESULT_DATA_KEY),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



