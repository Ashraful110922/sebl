package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opl.one.oplsales.R;
import helper.BaseFragment;
import helper.ClearEditText;
import interfac.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;

public class PasswordChange extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private TextView btnChangePass;
    private TextInputEditText etPassNew,etPassRetype,etOldPass;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.change_pass, container, false);
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
        toolbar.setTitle("Password Change");

        Typeface ceRaProBold = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProBold.otf");
        Typeface ceRaProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProRegular.otf");

        pb = (ProgressBar) getView().findViewById(R.id.pbChangePass);
        btnChangePass = (TextView) getView().findViewById(R.id.btnChangePass);
        etOldPass = (TextInputEditText) getView().findViewById(R.id.etOldPass);
        etPassNew = (TextInputEditText) getView().findViewById(R.id.etPassNew);
        etPassRetype = (TextInputEditText) getView().findViewById(R.id.etPassRetype);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isOnline(context)){
                    dataVerify();
                }else {
                    AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                }
            }
        });

    }

    private void dataVerify(){
      /*  JsonParser parser = new JsonParser();
        if (!TextUtils.isEmpty(pref.getString("LOGIN_RESPONSE",""))){
            JsonObject login_response = (JsonObject) parser.parse(pref.getString("LOGIN_RESPONSE",""));
        }*/
        String oPass,nPass,rTypePass;
        etOldPass.setError(null);
        etPassNew.setError(null);
        etPassRetype.setError(null);
        oPass = etOldPass.getText().toString().trim();
        nPass = etPassNew.getText().toString().trim();
        rTypePass = etPassRetype.getText().toString().trim();

        if(TextUtils.isEmpty(nPass)) {
            etPassNew.setError("Required New Password");
            etPassNew.requestFocus();
        }else if(TextUtils.isEmpty(rTypePass)){
            etPassRetype.setError("Required Retype password");
            etPassRetype.requestFocus();
        }else if(!nPass.equalsIgnoreCase(rTypePass)){
            Toast.makeText(context,"Password not same",Toast.LENGTH_SHORT).show();
        }else {
            JsonObject object = new JsonObject();
            object.addProperty("previousPassword",oPass);
            object.addProperty("newPassword",nPass);
            //Log.e("object",new Gson().toJson(object));
            //Log.e("object",pref.getString(AppConstant.TOKEN,""));
            changePassword(pref.getString(AppConstant.TOKEN,""),object);
        }
        }


    private void changePassword( String header, JsonObject jsonObject) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.changePassword(header,jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                //Log.e("object", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null){
                    String mgs = serverResponse.get("message").getAsString();
                    Toast.makeText(context,mgs,Toast.LENGTH_SHORT).show();
                    if (serverResponse.get("status").getAsBoolean()){
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
    public void onDestroy() {
        super.onDestroy();
    }


}



