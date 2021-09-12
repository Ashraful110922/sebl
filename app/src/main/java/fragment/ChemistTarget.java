package fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.opl.one.oplsales.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import adapter.ChemListAdapter;
import adapter.ChemistListAdapter;
import adapter.DrListAdapter;
import adapter.MarketAdapter;
import adapter.RosterAdapter;
import dialog.SetTimeChemistFragment;
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


public class ChemistTarget extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private Spinner spCRoster,spMktChemist;
    private TextView btnSelectChemist;
    private Dialog dialog;
    private int intRosterId= 0,chemistId=0;
    private ArrayList<TinyUser> list= new ArrayList<TinyUser>();
    private List<TinyUser> cList = new ArrayList<TinyUser>();
    private List<TinyUser> tempCList = new ArrayList<TinyUser>();
    private List<ParamMarket> mkList = new ArrayList<ParamMarket>();
    private TextView ddPick,ttPicker,btnCTarget;
    private TextInputEditText opinion2Chemist;
    private Calendar currentT;
    private SimpleDateFormat ddf,ttf;
    private String mktCode="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_chemist, container, false);
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
        pb = (ProgressBar) getView().findViewById(R.id.pbTargetChemist);
        spCRoster = (Spinner) getView().findViewById(R.id.spCRoster);
        spMktChemist = (Spinner) getView().findViewById(R.id.spMktChemist);
        ddPick = (TextView) getView().findViewById(R.id.ddPick);
        ttPicker = (TextView) getView().findViewById(R.id.ttPicker);
        opinion2Chemist = (TextInputEditText) getView().findViewById(R.id.opinion2Chemist);
        btnCTarget = (TextView) getView().findViewById(R.id.btnCTarget);
        btnSelectChemist = (TextView) getView().findViewById(R.id.btnSelectChemist);




        currentT=Calendar.getInstance();
        //ddf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ddf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        currentT.add(Calendar.DAY_OF_MONTH, 1);
        ddPick.setText(ddf.format(currentT.getTime()));
        ddPick.setOnClickListener(new View.OnClickListener() {
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
                        ddPick.setText(new StringBuilder().append(mm).append("/").append(dd).append("/").append(year).toString());
                        //ddPick.setText(new StringBuilder().append(year).append("-").append(mm).append("-").append(dd).toString());
                    }
                });
                if (getActivity()!=null)
                pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });
        ttf = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        ttPicker.setText(ttf.format(currentT.getTime()));
        ttPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"This is open ",Toast.LENGTH_SHORT).show();
                DialogFragment dFragment = new SetTimeChemistFragment();
                dFragment.show(getChildFragmentManager(),"Time Picker");
            }
        });



        mkList.clear();
        mkList.add(0,new ParamMarket("0","Select market"));
        MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
        spMktChemist.setAdapter(marketAdapter);
        spMktChemist.setEnabled(false);

        list.clear();
        list.add(0,new TinyUser(0,"Select roster"));
        list.add(1,new TinyUser(1,"Morning"));
        list.add(2,new TinyUser(2,"Evening"));
        RosterAdapter myAdapter = new RosterAdapter(context,R.layout.spinner_item,list);
        spCRoster.setAdapter(myAdapter);

        spCRoster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((position > 0) && (list.size()>1)) {
                    String date  =ddPick.getText().toString().trim();
                    getListMarketForPlan(pref.getString(AppConstant.TOKEN,""),date);
                    intRosterId = list.get(position).getId();
                }else {
                    intRosterId = 0;
                    spMktChemist.setEnabled(false);
                    mkList.clear();
                    mkList.add(0,new ParamMarket("0","Select market"));
                    MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                    spMktChemist.setAdapter(marketAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (AppConstant.isOnline(context)){
            //getMarketList(pref.getString(AppConstant.TOKEN,""));
        }else {
            AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
        }

        btnCTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dd =ddPick.getText().toString().trim();
                String tt = ttPicker.getText().toString().trim();
                String remark = opinion2Chemist.getText().toString().trim();
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
                } else if(chemistId<=0){
                    bundle.putString("ERROR_MESSAGE","Please select chemist");
                    modal.setArguments(bundle);
                    modal.show(getChildFragmentManager(),"showError");
                    return;
                }else {
                    JsonObject object = new JsonObject();
                    object.addProperty("RosterID",intRosterId);
                    object.addProperty("ChemistID",chemistId);
                    object.addProperty("visitDate",dd);
                    object.addProperty("VisitTime",tt);
                    object.addProperty("Opinion",remark);
                    Log.e("MyData",new Gson().toJson(object));
                    if (AppConstant.isOnline(context)){
                        setPlanChemist(pref.getString(AppConstant.TOKEN,""),object);
                    }else {
                        AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                    }
                }
            }
        });
    }

    private void setPlanChemist(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setPlanChemist(header,object);
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
                        opinion2Chemist.setText("");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if (Build.VERSION.SDK_INT >= 26) {
                            ft.setReorderingAllowed(false);
                        }
                        ft.detach(ChemistTarget.this).attach(ChemistTarget.this).commit();
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
                        spMktChemist.setEnabled(true);
                        TypeToken<ArrayList<ParamMarket>> token = new TypeToken<ArrayList<ParamMarket>>() {};
                        mkList = new Gson().fromJson(serverResponse.get("markets").getAsJsonArray(), token.getType());
                        mkList.add(0,new ParamMarket("0","Select market"));
                        mkList.add(mkList.size(),new ParamMarket("","Add new market"));
                        MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                        spMktChemist.setAdapter(marketAdapter);
                        spMktChemist.post(new Runnable() {
                            @Override
                            public void run() {
                                spMktChemist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                                getListChemistMarketForPlan(pref.getString(AppConstant.TOKEN,""),mktCode);
                                            }
                                        }else {
                                            cList.clear();
                                            btnSelectChemist.setText("Select chemist");
                                            chemistId=0;
                                            btnSelectChemist.setEnabled(false);
                                            //EditText editText = dialog.findViewById(R.id.edit_text);
                                           /* ListView listView = dialog.findViewById(R.id.list_view);
                                            ChemistListAdapter adapter = new ChemistListAdapter(context,R.layout.spinner_item, cList);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();*/
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        mktCode = "";
                                    }
                                });
                            }
                        });

                    }else {
                        mkList.add(0,new ParamMarket("0","Select market"));
                        mkList.add(mkList.size(),new ParamMarket("","Add new market"));

                        MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                        spMktChemist.setAdapter(marketAdapter);
                        spMktChemist.post(new Runnable() {
                            @Override
                            public void run() {
                                spMktChemist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


    private void getListChemistMarketForPlan(String header,String mkCode) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getListChemistMarketForPlan(header,mkCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData",new Gson().toJson(serverResponse));
                if (response.code() ==200 && serverResponse!=null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    if (status && serverResponse.has("chemists") && serverResponse.get("chemists").getAsJsonArray().size()>0){
                        cList.clear();
                        btnSelectChemist.setEnabled(true);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        cList = new Gson().fromJson(serverResponse.get("chemists").getAsJsonArray(), token.getType());
                        cList.add(cList.size(),new TinyUser(0,"Add new chemist"));
                        dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_searchable_spinner);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        EditText editText = dialog.findViewById(R.id.edit_text);
                        ListView listView = dialog.findViewById(R.id.list_view);
                        ChemistListAdapter adapter = new ChemistListAdapter(context,R.layout.spinner_item, cList);
                        btnSelectChemist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listView.setAdapter(adapter);
                                editText.setText("");
                                dialog.show();
                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        int textLength = s.length();
                                        tempCList.clear();
                                        for(TinyUser c: cList){
                                            if (textLength <= c.getName().length()) {
                                                if (c.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                                    tempCList.add(c);
                                                }
                                            }
                                        }
                                        ChemistListAdapter listAdapter = new ChemistListAdapter(context, R.layout.spinner_item, tempCList);
                                        listView.setAdapter(listAdapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                TinyUser user = listAdapter.getItem(position);
                                                btnSelectChemist.setText(Html.fromHtml("<font color='#000000'><b><small>"+user.getName()+"</small></b></font><br><small><font color='#0000FF'>"+user.getChemistID() +"</font></small><hr>"));
                                                if (user.getName().equalsIgnoreCase("Add new chemist")){
                                                    NewDoctor doctor = new NewDoctor();
                                                    Bundle ars = new Bundle();
                                                    ars.putString("EDIT_TYPE","chemistAdd");
                                                    doctor.setArguments(ars);
                                                    getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                    myCommunicator.setContentFragment(doctor, true);
                                                }else {
                                                    chemistId = user.getChemistID();
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
                                        TinyUser user = adapter.getItem(position);
                                        btnSelectChemist.setText(Html.fromHtml("<font color='#000000'><b><small>"+user.getName()+"</small></b></font><br><small><font color='#0000FF'>"+user.getChemistID() +"</font></small><hr>"));
                                        if (user.getName().equalsIgnoreCase("Add new chemist")){
                                            NewDoctor doctor = new NewDoctor();
                                            Bundle ars = new Bundle();
                                            ars.putString("EDIT_TYPE","chemistAdd");
                                            doctor.setArguments(ars);
                                            getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                            myCommunicator.setContentFragment(doctor, true);
                                        }else {
                                            chemistId = user.getChemistID();
                                        }
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                    }else {
                        btnSelectChemist.setEnabled(false);
                        chemistId=0;
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



