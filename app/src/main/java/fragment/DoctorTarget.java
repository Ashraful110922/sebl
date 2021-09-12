package fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.MyApplication;
import com.opl.one.oplsales.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import adapter.ChemListAdapter;
import adapter.DrListAdapter;
import adapter.MarketAdapter;
import adapter.RosterAdapter;
import dialog.SetTimeDoctorFragment;
import helper.BaseFragment;
import helper.DMYPDialog;
import interfac.ApiService;
import modal.ErrorMessageModal;
import model.ParamChemist;
import model.ParamMarket;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;


public class DoctorTarget extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private TextView dPick,tPicker,btnDrTarget,btnMkt;
    private TextInputEditText opinion2Dr;
    private Spinner spRoster,spDoctorChemist;
    //spMktDr;
    private int intRosterId= 0,intDrId=0;
    private ArrayList<TinyUser> list= new ArrayList<TinyUser>();
    private ArrayList<TinyUser> dList= new ArrayList<TinyUser>();
    private List<ParamMarket> mkList = new ArrayList<ParamMarket>();
    private List<ParamMarket> tempList = new ArrayList<ParamMarket>();
    private Calendar current;
    private SimpleDateFormat df,tf;
    private String mktCode="";
    private Dialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_common, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        intUit();
    }

    private void intUit() {
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        pb = (ProgressBar) getView().findViewById(R.id.pbTargetDoctor);
        //mioName = (TextView) getView().findViewById(R.id.mioName);
        //aCode = (TextView) getView().findViewById(R.id.aCode);
        dPick = (TextView) getView().findViewById(R.id.dPick);
        tPicker = (TextView) getView().findViewById(R.id.tPicker);
        btnDrTarget = (TextView) getView().findViewById(R.id.btnDrTarget);
        btnMkt = (TextView) getView().findViewById(R.id.btnMkt);
        opinion2Dr = (TextInputEditText) getView().findViewById(R.id.opinion2Dr);
        spRoster = (Spinner) getView().findViewById(R.id.spRoster);
        spDoctorChemist = (Spinner) getView().findViewById(R.id.spDoctorChemist);
        //spMktDr = (Spinner) getView().findViewById(R.id.spMktDr);

       /* if (!TextUtils.isEmpty(pref.getString("EMP_NAME",""))){
            mioName.setText(pref.getString("EMP_NAME",""));
        }
        if (!TextUtils.isEmpty(pref.getString("TERRITORY_CODE",""))){
            aCode.setText(Html.fromHtml("<font color='#000000'>T. Code :</font>"+pref.getString("TERRITORY_CODE","")));
        }else {
            aCode.setText(Html.fromHtml("<font color='#000000'>T. Code :N/A</font>"));
        }*/
        current=Calendar.getInstance();
        //df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        current.add(Calendar.DAY_OF_MONTH, 1);
        dPick.setText(df.format(current.getTime()));

        dPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMYPDialog pickerDialog = new DMYPDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String dd, mm;
                        if (day > 9 && day <= 31) {
                            dd = String.format( Locale.getDefault(),"%01d", day);
                        } else {
                            dd = String.format( Locale.getDefault(),"%02d", day);
                        }
                        int m = month + 1;
                        if (m > 9 && m <= 12) {
                            mm = String.format( Locale.getDefault(),"%01d", m);
                        } else {
                            mm = String.format( Locale.getDefault(),"%02d", m);
                        }
                        //dPick.setText(year + "-" + mm + "-" + dd);
                        dPick.setText(mm + "/" + dd + "/" + year);
                    }
                });
                pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });

        tf = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        tPicker.setText(tf.format(current.getTime()));

        tPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new SetTimeDoctorFragment();
                dFragment.show(getChildFragmentManager(),"Time Picker");
            }
        });



        dList.clear();
        dList.add(0,new TinyUser(0,"Select doctor"));
        dList.add(dList.size(),new TinyUser(0,"Add new market"));
        DrListAdapter adapter = new DrListAdapter(context,R.layout.spinner_item, dList);
        spDoctorChemist.setAdapter(adapter);
        spDoctorChemist.setEnabled(false);

      /*  mkList.clear();
        mkList.add(0,new ParamMarket("0","Select market"));
        MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
        spMktDr.setAdapter(marketAdapter);
        spMktDr.setEnabled(false);*/
        btnMkt.setText("Select market");
        btnMkt.setEnabled(false);

        list.clear();
        list.add(0,new TinyUser(0,"Select roster"));
        list.add(1,new TinyUser(1,"Morning"));
        list.add(2,new TinyUser(2,"Evening"));

        RosterAdapter rosterAdapter = new RosterAdapter(context,R.layout.spinner_item,list);
        spRoster.setAdapter(rosterAdapter);

        spRoster.post(new Runnable() {
            @Override
            public void run() {
                spRoster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ((position > 0) && (list.size()>1)) {
                            String date= dPick.getText().toString().trim();
                            intRosterId = list.get(position).getId();
                            mktCode = "";
                            intDrId = 0;
                            btnMkt.setText("Select market");
                            btnMkt.setEnabled(false);

                            dList.clear();
                            dList.add(0,new TinyUser(0,"Select doctor"));
                            spDoctorChemist.setSelection(0);

                            getListMarketForPlan(pref.getString(AppConstant.TOKEN,""),date);
                        }else {
                            spDoctorChemist.setEnabled(false);
                            mkList.clear();
                            btnMkt.setText("Select market");
                            btnMkt.setEnabled(false);
                            intRosterId =0;
                            dList.clear();
                            dList.add(0,new TinyUser(0,"Select doctor"));
                            spDoctorChemist.setSelection(0);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
        // Log.e("response","Token: "+pref.getString(AppConstant.TOKEN,""));
        if (AppConstant.isOnline(context)){
            //getMarketList(pref.getString(AppConstant.TOKEN,""));
        }else {
            AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
        }

        btnDrTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dd =dPick.getText().toString().trim();
                String tt = tPicker.getText().toString().trim();
                String remark = opinion2Dr.getText().toString().trim();
                Bundle bundle = new Bundle();
                ErrorMessageModal modal = new ErrorMessageModal();
                if (intRosterId<=0){
                    bundle.putString("ERROR_MESSAGE","Please select roster type");
                    modal.setArguments(bundle);
                    modal.show(getChildFragmentManager(),"showError");
                    return;
                }else if(TextUtils.isEmpty(mktCode)){
                    bundle.putString("ERROR_MESSAGE","Please select market");
                    modal.setArguments(bundle);
                    modal.show(getChildFragmentManager(),"showError");
                    return;
                }
                else if(intDrId<=0){
                    bundle.putString("ERROR_MESSAGE","Please select doctor");
                    modal.setArguments(bundle);
                    modal.show(getChildFragmentManager(),"showError");
                    return;
                }else {
                    JsonObject object = new JsonObject();
                    object.addProperty("RosterID",intRosterId);
                    object.addProperty("DoctorID",intDrId);
                    object.addProperty("visitDate",dd);
                    object.addProperty("VisitTime",tt);
                    object.addProperty("Opinion",remark);
                    /*Log.e("MyData","RosterID: "+intRosterId);
                    Log.e("MyData","intDrId: "+intDrId);
                    Log.e("MyData","MarketCode: "+mktCode);
                    Log.e("MyData","Date: "+dd );
                    Log.e("MyData","Time: "+tt);
                    Log.e("MyData","Opinion: "+remark);*/
                    Log.e("MyData",new Gson().toJson(object));

                    if (AppConstant.isOnline(context)){
                        setPlanDoctor(pref.getString(AppConstant.TOKEN,""),object);
                    }else {
                        AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                    }
                    //Log.e("Response","Token: "+pref.getString(AppConstant.TOKEN,""));
                    //Log.e("Response",new Gson().toJson(object));
                }
            }
        });
    }

    private void setPlanDoctor(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setPlanDoctor(header,object);
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
                        opinion2Dr.setText("");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if (Build.VERSION.SDK_INT >= 26) {
                            ft.setReorderingAllowed(false);
                        }
                        ft.detach(DoctorTarget.this).attach(DoctorTarget.this).commit();
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

    private void getListMarketForPlan(String header,String date) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getListMarketForPlan(header,date);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                if (response.code() ==200 && serverResponse!=null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    mkList.clear();
                    if (status && serverResponse.has("markets") && serverResponse.get("markets").getAsJsonArray().size()>0){
                        btnMkt.setEnabled(true);
                        spDoctorChemist.setEnabled(false);
                        TypeToken<ArrayList<ParamMarket>> token = new TypeToken<ArrayList<ParamMarket>>() {};
                        mkList = new Gson().fromJson(serverResponse.get("markets").getAsJsonArray(), token.getType());
                        mkList.add(0,new ParamMarket("0","Select market"));
                        mkList.add(mkList.size(),new ParamMarket("","Add new market"));
                        if (mkList.size()>1){
                            dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_searchable_spinner);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            EditText editText = dialog.findViewById(R.id.edit_text);
                            ListView listView = dialog.findViewById(R.id.list_view);
                            MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                            btnMkt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listView.setAdapter(marketAdapter);
                                    editText.setText("");
                                    dialog.show();
                                    editText.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }
                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            int textLength = s.length();
                                            tempList.clear();
                                            for(ParamMarket c: mkList){
                                                if (textLength <= c.getName().length()) {
                                                    if (c.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                                        tempList.add(c);
                                                    }
                                                }
                                            }
                                            MarketAdapter listAdapter = new MarketAdapter(context, R.layout.spinner_item, tempList);
                                            listView.setAdapter(listAdapter);
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    ParamMarket user = listAdapter.getItem(position);
                                                        btnMkt.setText(Html.fromHtml("<font color='#000000'><b><small>"+user.getName()+"</small></b></font><br><small><font color='#0000FF'>"+user.getCode() +"</font></small><hr>"));
                                                    if (mkList.size()>0){
                                                        if (mkList.get(position).getName().equalsIgnoreCase("Add new market")){
                                                            NewMarket market = new NewMarket();
                                                            Bundle ars = new Bundle();
                                                            ars.putString("TYPE_MARKET","marketAdd");
                                                            market.setArguments(ars);
                                                            getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                            myCommunicator.setContentFragment(market, true);
                                                        }else {
                                                            mktCode = mkList.get(position).getCode();
                                                            getListDoctorMarketForPlan(pref.getString(AppConstant.TOKEN,""),mktCode);
                                                        }
                                                    }else {
                                                        mktCode = "";
                                                        spDoctorChemist.setEnabled(false);
                                                        dList.clear();
                                                        dList.add(0,new TinyUser(0,"Select doctor"));
                                                        dList.add(dList.size(),new TinyUser(0,"Add new market"));
                                                        DrListAdapter adapter = new DrListAdapter(context,R.layout.spinner_item, dList);
                                                        spDoctorChemist.setAdapter(adapter);
                                                    }
                                                    dialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });

                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            ParamMarket user = marketAdapter.getItem(position);
                                            btnMkt.setText(Html.fromHtml("<font color='#000000'><b><small>"+user.getName()+"</small></b></font><br><small><font color='#0000FF'>"+user.getCode() +"</font></small><hr>"));
                                            if (user.getName().equalsIgnoreCase("Add new market")){
                                                NewMarket market = new NewMarket();
                                                Bundle ars = new Bundle();
                                                ars.putString("TYPE_MARKET","marketAdd");
                                                market.setArguments(ars);
                                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                myCommunicator.setContentFragment(market, true);
                                            }else {
                                                mktCode = mkList.get(position).getCode();
                                                getListDoctorMarketForPlan(pref.getString(AppConstant.TOKEN,""),mktCode);
                                            }
                                            dialog.dismiss();
                                        }
                                    });

                                }
                            });
                        }else {
                            mktCode = "";
                        }
                        /*MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                        spMktDr.setAdapter(marketAdapter);
                        spMktDr.post(new Runnable() {
                            @Override
                            public void run() {
                                spMktDr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                                getListDoctorMarketForPlan(pref.getString(AppConstant.TOKEN,""),mktCode);
                                            }
                                        }else {
                                            spDoctorChemist.setEnabled(false);
                                            dList.clear();
                                            dList.add(0,new TinyUser(0,"Select doctor"));
                                            dList.add(dList.size(),new TinyUser(0,"Add new market"));
                                            DrListAdapter adapter = new DrListAdapter(context,R.layout.spinner_item, dList);
                                            spDoctorChemist.setAdapter(adapter);
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        mktCode = "";

                                    }
                                });
                            }
                        });*/

                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void getListDoctorMarketForPlan(String header,String mkCode) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getListDoctorMarketForPlan(header,mkCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData",new Gson().toJson(serverResponse));
                if (response.code() ==200 && serverResponse!=null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    dList.clear();
                    if (status && serverResponse.has("doctors") && serverResponse.get("doctors").getAsJsonArray().size()>0){
                        spDoctorChemist.setEnabled(true);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        dList = new Gson().fromJson(serverResponse.get("doctors").getAsJsonArray(), token.getType());
                        dList.add(0,new TinyUser(0,"Select doctor"));
                        dList.add(dList.size(),new TinyUser(0,"Add new market"));
                        DrListAdapter adapter = new DrListAdapter(context,R.layout.spinner_item, dList);
                        spDoctorChemist.setAdapter(adapter);
                        spDoctorChemist.post(new Runnable() {
                            @Override
                            public void run() {
                                spDoctorChemist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position>0){
                                            if (dList.get(position).getName().equalsIgnoreCase("Add new market")){
                                                NewMarket market = new NewMarket();
                                                Bundle ars = new Bundle();
                                                ars.putString("TYPE_MARKET","marketAdd");
                                                market.setArguments(ars);
                                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                myCommunicator.setContentFragment(market, true);
                                            }else {
                                                intDrId = dList.get(position).getDoctorId();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        intDrId = 0;
                                    }
                                });
                            }
                        });
                    }else {
                        dList.clear();
                        dList.add(0,new TinyUser(0,"Select doctor"));
                        dList.add(dList.size(),new TinyUser(0,"Add new market"));
                        DrListAdapter drListAdapter = new DrListAdapter(context,R.layout.spinner_item, dList);
                        spDoctorChemist.setAdapter(drListAdapter);
                        spDoctorChemist.post(new Runnable() {
                            @Override
                            public void run() {
                                spDoctorChemist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position>0){
                                            if (dList.get(position).getName().equalsIgnoreCase("Add new market")){
                                                NewMarket market = new NewMarket();
                                                Bundle ars = new Bundle();
                                                ars.putString("TYPE_MARKET","marketAdd");
                                                market.setArguments(ars);
                                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                myCommunicator.setContentFragment(market, true);
                                            }else {
                                                intDrId = dList.get(position).getDoctorId();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        intDrId = 0;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



