package fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import adapter.AreaAdapter;
import adapter.ChemAdapter;
import adapter.DepotAdapter;
import adapter.DrAdapter;
import adapter.EmployeeAdapter;
import adapter.MarketAdapter;
import adapter.MioAdapter;
import adapter.PlanAdapter;
import adapter.RegionAdapter;
import adapter.RosterAdapter;
import adapter.TerritoryAdapter;
import adapter.ZoneAdapter;
import helper.BaseFragment;
import helper.DMYPDialog;
import helper.DividerItemDecoration;
import interfac.ApiService;
import model.ParamArea;
import model.ParamData;
import model.ParamDepot;
import model.ParamMarket;
import model.ParamMio;
import model.ParamRegion;
import model.ParamSchedule;
import model.ParamTerritory;
import model.ParamZone;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;


public class MarketCompleted extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private TextView dGetMkt, btnDrList, btnCheList, btnEmp;
    private Calendar current;
    private SimpleDateFormat df;
    private Spinner spDCExecute, spExePlan, spExeZone, spExeDpo, spExeRegion, spExeArea, spExeTerritory, spExeMio, spTakeMkt;
    private RecyclerView mRecyclerView;
    private SearchView search;
    private List<TinyUser> list = new ArrayList<TinyUser>();
    private ArrayList<TinyUser> rsList = new ArrayList<TinyUser>();
    private List<ParamMarket> mkList = new ArrayList<ParamMarket>();
    private List<ParamMarket> temMktList = new ArrayList<ParamMarket>();
    private RelativeLayout notFound;
    private int intRosterId = 0,mktSid=0;
    private ChemAdapter reportAdapter;
    private EmployeeAdapter employeeAdapter;
    private DrAdapter drAdapter;
    private List<ParamSchedule> sList = new ArrayList<ParamSchedule>();
    private List<ParamZone> zList = new ArrayList<ParamZone>();
    private List<ParamDepot> dList = new ArrayList<ParamDepot>();
    private List<ParamRegion> rList = new ArrayList<ParamRegion>();
    private List<ParamArea> aList = new ArrayList<ParamArea>();
    private List<ParamTerritory> tList = new ArrayList<ParamTerritory>();
    private List<ParamMio> mList = new ArrayList<ParamMio>();
    private String code="", type = "";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.market_complete, container, false);
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
        pb = (ProgressBar) getView().findViewById(R.id.pbMktReport);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recExMkt);
        GridLayoutManager gridVertical = new GridLayoutManager(context, 1, RecyclerView.VERTICAL, false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, context.getResources().getDrawable(R.drawable.divider)));
        mRecyclerView.setLayoutManager(gridVertical);
        dGetMkt = (TextView) getView().findViewById(R.id.dGetMkt);
        btnDrList = (TextView) getView().findViewById(R.id.btnDrList);
        btnCheList = (TextView) getView().findViewById(R.id.btnCheList);
        spDCExecute = (Spinner) getView().findViewById(R.id.spDCExecute);
        spExePlan = (Spinner) getView().findViewById(R.id.spExePlan);
        spExeZone = (Spinner) getView().findViewById(R.id.spExeZone);
        spExeDpo = (Spinner) getView().findViewById(R.id.spExeDpo);
        spExeRegion = (Spinner) getView().findViewById(R.id.spExeRegion);
        spExeArea = (Spinner) getView().findViewById(R.id.spExeArea);
        spExeTerritory = (Spinner) getView().findViewById(R.id.spExeTerritory);
        spExeMio = (Spinner) getView().findViewById(R.id.spExeMio);
        spTakeMkt = (Spinner) getView().findViewById(R.id.spTakeMkt);
        notFound = (RelativeLayout) getView().findViewById(R.id.mktNoFound);
        search = (SearchView) getView().findViewById(R.id.search);
        btnEmp = (TextView) getView().findViewById(R.id.btnEmp);

        notFound.setVisibility(View.GONE);
        rsList.clear();
        rsList.add(0, new TinyUser(0, "Select roster"));
        rsList.add(1, new TinyUser(1, "Morning"));
        rsList.add(2, new TinyUser(2, "Evening"));

        current = Calendar.getInstance();
        //df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        dGetMkt.setText(df.format(current.getTime()));

        dGetMkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DMYPDialog pickerDialog = new DMYPDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String dd, mm;
                        if (day > 9 && day <= 31) {
                            dd = String.format(Locale.getDefault(), "%01d", day);
                        } else {
                            dd = String.format(Locale.getDefault(), "%02d", day);
                        }
                        int m = month + 1;
                        if (m > 9 && m <= 12) {
                            mm = String.format(Locale.getDefault(), "%01d", m);
                        } else {
                            mm = String.format(Locale.getDefault(), "%02d", m);
                        }
                        dGetMkt.setText(mm + "/" + dd + "/" + year);
                        /*RosterAdapter rosterAdapter = new RosterAdapter(context,R.layout.spinner_item,rsList);
                        spDCExecute.setAdapter(rosterAdapter);
                        intRosterId =0;*/
                    }
                });
                pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });

        RosterAdapter rosterAdapter = new RosterAdapter(context, R.layout.spinner_item, rsList);
        spDCExecute.setAdapter(rosterAdapter);
        spDCExecute.post(new Runnable() {
            @Override
            public void run() {
                spDCExecute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ((position > 0) && (rsList.size() > 1)) {
                            String date = dGetMkt.getText().toString().trim();
                            intRosterId = rsList.get(position).getId();
                            //getListMarketForPlan(pref.getString(AppConstant.TOKEN,""),date);
                           /* Log.e("MyData","<><>"+date);
                            Log.e("MyData","<><>"+intRosterId);
                            Log.e("MyData","<><>"+pref.getString(AppConstant.TOKEN,""));*/
                            getMarketScheduleData(pref.getString(AppConstant.TOKEN, ""), date, intRosterId);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

        btnEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mktSid>0)
                getEmployeeDynamicData(pref.getString(AppConstant.TOKEN, ""),code,type);
                else
                    Toast.makeText(context,"Select schedule",Toast.LENGTH_SHORT).show();
            }
        });

        btnDrList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isOnline(context)) {
                    if (mktSid>0)
                        getDoctorsDynamicData(pref.getString(AppConstant.TOKEN, ""), code,type);
                    else
                        Toast.makeText(context,"Select schedule",Toast.LENGTH_SHORT).show();
                } else {
                    AppConstant.openDialog(context, "No Internet", context.getResources().getString(R.string.internet_error));
                }
            }
        });

        btnCheList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isOnline(context)) {
                    if (mktSid>0)
                     getChemistsDynamicData(pref.getString(AppConstant.TOKEN, ""),code,type);
                     else
                    Toast.makeText(context,"Select schedule",Toast.LENGTH_SHORT).show();
                } else {
                    AppConstant.openDialog(context, "No Internet", context.getResources().getString(R.string.internet_error));
                }
            }
        });
        //Initialize whe page load
        list.clear();
        employeeAdapter = new EmployeeAdapter(context, "gone edit", list, new EmployeeAdapter.MyAdapterListener() {
            @Override
            public void rowClick(View v, int position) {

            }
            @Override
            public void editClick(View v, int position) {

            }
        });
        mRecyclerView.setAdapter(employeeAdapter);


        list.clear();
         drAdapter = new DrAdapter(context, "gone edit", list, new DrAdapter.MyAdapterListener() {
            @Override
            public void rowClick(View v, int position) {

            }

            @Override
            public void editClick(View v, int position) {
            }
        });
        mRecyclerView.setAdapter(drAdapter);

        reportAdapter = new ChemAdapter(context, "gone edit", list, new ChemAdapter.MyAdapterListener() {
            @Override
            public void rowClick(View v, int position) {

            }

            @Override
            public void rowEdit(View v, int position) {

            }
        });
        mRecyclerView.setAdapter(reportAdapter);
        //End Initialize whe page load
      /*  search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                reportAdapter.getFilter().filter(newText);
                return false;
            }
        });*/
    }


    private void getDoctorsDynamicData(String header, String code,String type) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getDoctorsDynamicData(header, code,type);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                //Log.e("MyData", new Gson().toJson(serverResponse));
                list.clear();
                if (response.code() == 200 && serverResponse != null) {
                    //boolean status = serverResponse.get("status").getAsBoolean();
                    if (serverResponse.has("Doctors") && serverResponse.get("Doctors").getAsJsonArray().size() > 0) {
                        notFound.setVisibility(View.GONE);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {
                        };
                        list = new Gson().fromJson(serverResponse.get("Doctors").getAsJsonArray(), token.getType());
                         drAdapter = new DrAdapter(context, "gone edit", list, new DrAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                DoctorVisited visited = new DoctorVisited();
                                Bundle ars = new Bundle();
                                TinyUser user = list.get(position);
                                //Log.e("MyData","Doctor ID: "+user.getDoctorID());
                                //Log.e("MyData","Roster ID: "+intRosterId);
                                //Log.e("MyData","Schedule ID: "+mktSid);
                                ars.putString("dId", String.valueOf(user.getDoctorID()));
                                ars.putString("rId", String.valueOf(intRosterId));
                                ars.putString("mId", String.valueOf(mktSid));
                                ars.putString("EXECUTE_TYPE", "doctor market");
                                visited.setArguments(ars);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.addContentFragment(visited, true);
                            }

                            @Override
                            public void editClick(View v, int position) {

                            }
                        });
                        mRecyclerView.setAdapter(drAdapter);
                    } else {
                        notFound.setVisibility(View.VISIBLE);
                        mRecyclerView.setAdapter(drAdapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }
    private void getChemistsDynamicData(String header, String code,String type) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getChemistsDynamicData(header, code,type);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData", new Gson().toJson(serverResponse));
                list.clear();
                if (response.code() == 200 && serverResponse != null) {
                    if (serverResponse.has("Chemists") && serverResponse.get("Chemists").getAsJsonArray().size() > 0) {
                        notFound.setVisibility(View.GONE);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {
                        };
                        list = new Gson().fromJson(serverResponse.get("Chemists").getAsJsonArray(), token.getType());
                        reportAdapter = new ChemAdapter(context, "gone edit", list, new ChemAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                DoctorVisited visited = new DoctorVisited();
                                Bundle ars = new Bundle();
                                TinyUser user = list.get(position);
                                /*Log.e("MyData","Chemist ID: "+user.getChemistID());
                                Log.e("MyData","RosterID: "+intRosterId);
                                Log.e("MyData","Schedule Id: "+mktSid);*/
                                ars.putString("dId", String.valueOf(user.getChemistID()));
                                ars.putString("rId", String.valueOf(intRosterId));
                                ars.putString("mId", String.valueOf(mktSid));
                                ars.putString("EXECUTE_TYPE", "chemist market");
                                visited.setArguments(ars);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.addContentFragment(visited, true);
                            }

                            @Override
                            public void rowEdit(View v, int position) {

                            }
                        });
                        mRecyclerView.setAdapter(reportAdapter);
                    } else {
                        notFound.setVisibility(View.VISIBLE);
                        mRecyclerView.setAdapter(reportAdapter);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }
    private void getEmployeeDynamicData(String header,String code,String type) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getEmployeeDynamicData(header,code,type);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null){
                   //boolean status = serverResponse.get("status").getAsBoolean();
                    list.clear();
                    if (serverResponse.has("Employees") && serverResponse.get("Employees").getAsJsonArray().size() > 0){
                        notFound.setVisibility(View.GONE);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("Employees").getAsJsonArray(), token.getType());
                        employeeAdapter = new EmployeeAdapter(context, "gone edit", list, new EmployeeAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                DoctorVisited visited = new DoctorVisited();
                                TinyUser user = list.get(position);
                                Bundle ars = new Bundle();
                                ars.putString("dId", user.getMIOCode());
                                ars.putString("rId", String.valueOf(intRosterId));
                                ars.putString("mId", String.valueOf(mktSid));
                                ars.putString("EXECUTE_TYPE", "emp market");
                                visited.setArguments(ars);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.addContentFragment(visited, true);

                            }
                            @Override
                            public void editClick(View v, int position) {

                            }
                        });
                        mRecyclerView.setAdapter(employeeAdapter);
                    }else {
                        notFound.setVisibility(View.VISIBLE);
                        mRecyclerView.setAdapter(employeeAdapter);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }
    private void getMarketScheduleData(String header, String date, int rosId) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<ParamData> call = api.getMarketScheduleData(header, date, rosId);
        call.enqueue(new Callback<ParamData>() {
            @Override
            public void onResponse(@NonNull Call<ParamData> call, @NonNull retrofit2.Response<ParamData> response) {
                pb.setVisibility(View.GONE);
                ParamData serverResponse = response.body();
                Log.e("response", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null && serverResponse.getSchedules().size() > 0) {
                    type = "";
                    code = "";
                    sList.clear();
                    sList.addAll(serverResponse.getSchedules());
                    sList.add(0, new ParamSchedule(0, "Select plan"));
                    if (sList.size() > 1) {
                        PlanAdapter planAdapter = new PlanAdapter(context, R.layout.spinner_item, sList);
                        spExePlan.setAdapter(planAdapter);
                        spExePlan.post(new Runnable() {
                            @Override
                            public void run() {
                                spExePlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if (position > 0 && sList.size() > 1) {
                                            mktSid = sList.get(position).getMarketScheduleID();
                                            if (sList.get(position).getZones().size() > 0) {
                                                zList.clear();
                                                zList.add(0, new ParamZone("0", "Select zone"));
                                                zList.addAll(sList.get(position).getZones());
                                                if (zList.size() > 1) {
                                                    ZoneAdapter adapter = new ZoneAdapter(context, R.layout.spinner_item, zList);
                                                    spExeZone.setAdapter(adapter);
                                                    spExeZone.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            spExeZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                @Override
                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                    if (position > 0 && zList.size() > 1) {
                                                                        code = zList.get(position).getZONE_CODE();
                                                                        type = "Z";
                                                                        if (zList.get(position).getDepots().size() > 0) {
                                                                            dList.clear();
                                                                            dList.add(0, new ParamDepot("0", "Select depot"));
                                                                            dList.addAll(zList.get(position).getDepots());
                                                                            if (dList.size() > 1 && !TextUtils.isEmpty(dList.get(position).getDepotName())) {
                                                                                DepotAdapter aDepot = new DepotAdapter(context, R.layout.spinner_item, dList);
                                                                                spExeDpo.setAdapter(aDepot);
                                                                                spExeDpo.post(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        spExeDpo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                            @Override
                                                                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                if (position > 0 && dList.size() > 1) {
                                                                                                    code = dList.get(position).getDEPOT_CODE();
                                                                                                    type = "D";
                                                                                                    if (dList.get(position).getRegions().size() > 0) {
                                                                                                        rList.clear();
                                                                                                        rList.addAll(dList.get(position).getRegions());
                                                                                                        rList.add(0, new ParamRegion("0", "Select region"));
                                                                                                        if (rList.size() > 1 && !TextUtils.isEmpty(rList.get(position).getRegionName())) {
                                                                                                            RegionAdapter aRegion = new RegionAdapter(context, R.layout.spinner_item, rList);
                                                                                                            spExeRegion.setAdapter(aRegion);
                                                                                                            spExeRegion.post(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    spExeRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                        @Override
                                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                            if (position > 0 && rList.size() > 1) {
                                                                                                                                code = rList.get(position).getREGION_CODE();
                                                                                                                                type = "R";
                                                                                                                                if (rList.get(position).getAreas().size() > 0) {
                                                                                                                                    aList.clear();
                                                                                                                                    aList.addAll(rList.get(position).getAreas());
                                                                                                                                    aList.add(0, new ParamArea("0", "Select area"));
                                                                                                                                    if (aList.size() > 1 && !TextUtils.isEmpty(aList.get(position).getAreaName())) {
                                                                                                                                        AreaAdapter aArea = new AreaAdapter(context, R.layout.spinner_item, aList);
                                                                                                                                        spExeArea.setAdapter(aArea);
                                                                                                                                        spExeArea.post(new Runnable() {
                                                                                                                                            @Override
                                                                                                                                            public void run() {
                                                                                                                                                spExeArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                        if (position > 0 && aList.size() > 1) {
                                                                                                                                                            type = "A";
                                                                                                                                                            code = aList.get(position).getAREA_CODE();
                                                                                                                                                            if (aList.get(position).getTerritories().size() > 0) {
                                                                                                                                                                tList.clear();
                                                                                                                                                                tList.addAll(aList.get(position).getTerritories());
                                                                                                                                                                tList.add(0, new ParamTerritory("0", "Select territory"));
                                                                                                                                                                if (tList.size() > 1 && !TextUtils.isEmpty(tList.get(position).getTerritoryName())) {
                                                                                                                                                                    TerritoryAdapter aTerritory = new TerritoryAdapter(context, R.layout.spinner_item, tList);
                                                                                                                                                                    spExeTerritory.setAdapter(aTerritory);
                                                                                                                                                                    spExeTerritory.post(new Runnable() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void run() {
                                                                                                                                                                            spExeTerritory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                                    if (position > 0 && tList.size() > 1) {
                                                                                                                                                                                        code = tList.get(position).getTERRITORY_CODE();
                                                                                                                                                                                        type = "T";
                                                                                                                                                                                        if (tList.get(position).getMios().size() > 0) {
                                                                                                                                                                                            mList.clear();
                                                                                                                                                                                            mList.addAll(tList.get(position).getMios());
                                                                                                                                                                                            mList.add(0, new ParamMio("0", "Select mio"));
                                                                                                                                                                                            if (mList.size() > 1 && !TextUtils.isEmpty(mList.get(position).getEMPLOYEE_NAME())) {
                                                                                                                                                                                                MioAdapter aMio = new MioAdapter(context, R.layout.spinner_item, mList);
                                                                                                                                                                                                spExeMio.setAdapter(aMio);
                                                                                                                                                                                                spExeMio.post(new Runnable() {
                                                                                                                                                                                                    @Override
                                                                                                                                                                                                    public void run() {
                                                                                                                                                                                                        spExeMio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                                                                            @Override
                                                                                                                                                                                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                                                                if (position > 0 && mList.size() > 1) {
                                                                                                                                                                                                                    code = mList.get(position).getEMP_ID();
                                                                                                                                                                                                                    type = "M";
                                                                                                                                                                                                                    if (mList.get(position).getMarkets().size() > 0) {
                                                                                                                                                                                                                        mkList.clear();
                                                                                                                                                                                                                        mkList.addAll(mList.get(position).getMarkets());
                                                                                                                                                                                                                        mkList.add(0, new ParamMarket("0", "Select market"));
                                                                                                                                                                                                                        if (mkList.size() > 1 && !TextUtils.isEmpty(mkList.get(position).getName())) {
                                                                                                                                                                                                                            MarketAdapter marketAdapter = new MarketAdapter(context, R.layout.spinner_item, mkList);
                                                                                                                                                                                                                            spTakeMkt.setAdapter(marketAdapter);
                                                                                                                                                                                                                            spTakeMkt.post(new Runnable() {
                                                                                                                                                                                                                                @Override
                                                                                                                                                                                                                                public void run() {
                                                                                                                                                                                                                                    spTakeMkt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                                                                                            if (position > 0 && mkList.size() > 1) {
                                                                                                                                                                                                                                                code = mkList.get(position).getCode();
                                                                                                                                                                                                                                                type = "Mk";
                                                                                                                                                                                                                                            } else {
                                                                                                                                                                                                                                                code="";
                                                                                                                                                                                                                                                type = "";
                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                        }

                                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                    });
                                                                                                                                                                                                                                    if (mkList.size() == 2) {
                                                                                                                                                                                                                                        spTakeMkt.setSelection(1);
                                                                                                                                                                                                                                        spTakeMkt.setEnabled(false);
                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                        spTakeMkt.setEnabled(true);
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                            });
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                    //mkList.clear();
                                                                                                                                                                                                                    spTakeMkt.setAdapter(null);
                                                                                                                                                                                                                    //spTakeMkt.setEnabled(false);
                                                                                                                                                                                                                    code="";
                                                                                                                                                                                                                }
                                                                                                                                                                                                            }

                                                                                                                                                                                                            @Override
                                                                                                                                                                                                            public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                                                                                                            }
                                                                                                                                                                                                        });

                                                                                                                                                                                                        if (mList.size() == 2) {
                                                                                                                                                                                                            spExeMio.setSelection(1);
                                                                                                                                                                                                            spExeMio.setEnabled(false);
                                                                                                                                                                                                        } else {
                                                                                                                                                                                                            spExeMio.setEnabled(true);
                                                                                                                                                                                                        }

                                                                                                                                                                                                    }
                                                                                                                                                                                                });
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    } else {
                                                                                                                                                                                        //mkList.clear();
                                                                                                                                                                                        spTakeMkt.setAdapter(null);
                                                                                                                                                                                        //spTakeMkt.setEnabled(false);

                                                                                                                                                                                        //mList.clear();
                                                                                                                                                                                        spExeMio.setAdapter(null);
                                                                                                                                                                                        //spExeMio.setEnabled(false);
                                                                                                                                                                                        code ="";
                                                                                                                                                                                    }
                                                                                                                                                                                }

                                                                                                                                                                                @Override
                                                                                                                                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                                                                                }
                                                                                                                                                                            });
                                                                                                                                                                            if (tList.size() == 2) {
                                                                                                                                                                                spExeTerritory.setSelection(1);
                                                                                                                                                                                spExeTerritory.setEnabled(false);
                                                                                                                                                                            } else {
                                                                                                                                                                                spExeTerritory.setEnabled(true);
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    });
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        } else {
                                                                                                                                                            //mkList.clear();
                                                                                                                                                            spTakeMkt.setAdapter(null);
                                                                                                                                                            //spTakeMkt.setEnabled(false);

                                                                                                                                                            //mList.clear();
                                                                                                                                                            spExeMio.setAdapter(null);
                                                                                                                                                            //spExeMio.setEnabled(false);

                                                                                                                                                            //tList.clear();
                                                                                                                                                            spExeTerritory.setAdapter(null);
                                                                                                                                                            //spExeTerritory.setEnabled(false);

                                                                                                                                                            code="";
                                                                                                                                                        }
                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                                if (aList.size() == 2) {
                                                                                                                                                    spExeArea.setSelection(1);
                                                                                                                                                    spExeArea.setEnabled(false);
                                                                                                                                                } else {
                                                                                                                                                    spExeArea.setEnabled(true);
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        });
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            } else {
                                                                                                                                //mkList.clear();
                                                                                                                                spTakeMkt.setAdapter(null);
                                                                                                                                //spTakeMkt.setEnabled(false);

                                                                                                                                //mList.clear();
                                                                                                                                spExeMio.setAdapter(null);
                                                                                                                                //spExeMio.setEnabled(false);

                                                                                                                                //tList.clear();
                                                                                                                                spExeTerritory.setAdapter(null);
                                                                                                                                //spExeTerritory.setEnabled(false);

                                                                                                                                //aList.clear();
                                                                                                                                spExeArea.setAdapter(null);
                                                                                                                                //spExeArea.setEnabled(false);

                                                                                                                                code="";
                                                                                                                            }
                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                        }
                                                                                                                    });

                                                                                                                    if (rList.size() == 2) {
                                                                                                                        spExeRegion.setSelection(1);
                                                                                                                        spExeRegion.setEnabled(false);
                                                                                                                    } else {
                                                                                                                        spExeRegion.setEnabled(true);
                                                                                                                    }

                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    }
                                                                                                } else {

                                                                                                    //mkList.clear();
                                                                                                    spTakeMkt.setAdapter(null);
                                                                                                    //spTakeMkt.setEnabled(false);

                                                                                                    //mList.clear();
                                                                                                    spExeMio.setAdapter(null);
                                                                                                    //spExeMio.setEnabled(false);

                                                                                                    //tList.clear();
                                                                                                    spExeTerritory.setAdapter(null);
                                                                                                    //spExeTerritory.setEnabled(false);

                                                                                                    //aList.clear();
                                                                                                    spExeArea.setAdapter(null);
                                                                                                    //spExeArea.setEnabled(false);

                                                                                                    //rList.clear();
                                                                                                    spExeRegion.setAdapter(null);
                                                                                                    //spExeRegion.setEnabled(false);
                                                                                                    code ="";
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onNothingSelected(AdapterView<?> parent) {

                                                                                            }
                                                                                        });
                                                                                        if (dList.size() == 2) {
                                                                                            spExeDpo.setSelection(1);
                                                                                            spExeDpo.setEnabled(false);
                                                                                        } else {
                                                                                            spExeDpo.setEnabled(true);
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    } else {
                                                                        //mkList.clear();
                                                                        spTakeMkt.setAdapter(null);
                                                                        //spTakeMkt.setEnabled(false);

                                                                        //mList.clear();
                                                                        spExeMio.setAdapter(null);
                                                                        //spExeMio.setEnabled(false);

                                                                        //tList.clear();
                                                                        spExeTerritory.setAdapter(null);
                                                                        //spExeTerritory.setEnabled(false);

                                                                        //aList.clear();
                                                                        spExeArea.setAdapter(null);
                                                                        //spExeArea.setEnabled(false);

                                                                        //rList.clear();
                                                                        spExeRegion.setAdapter(null);
                                                                        //spExeRegion.setEnabled(false);

                                                                        //dList.clear();
                                                                        spExeDpo.setAdapter(null);
                                                                        //spExeDpo.setEnabled(false);

                                                                        code="";
                                                                    }
                                                                }

                                                                @Override
                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                }
                                                            });
                                                            if (zList.size() == 2) {
                                                                spExeZone.setSelection(1);
                                                                spExeZone.setEnabled(false);
                                                            } else {
                                                                spExeZone.setEnabled(true);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            //mkList.clear();
                                            spTakeMkt.setAdapter(null);
                                            //spTakeMkt.setEnabled(false);

                                            //mList.clear();
                                            spExeMio.setAdapter(null);
                                            //spExeMio.setEnabled(false);

                                            //tList.clear();
                                            spExeTerritory.setAdapter(null);
                                            //spExeTerritory.setEnabled(false);

                                            //aList.clear();
                                            spExeArea.setAdapter(null);
                                            //spExeArea.setEnabled(false);

                                            //rList.clear();
                                            spExeRegion.setAdapter(null);
                                            //spExeRegion.setEnabled(false);

                                            //dList.clear();
                                            spExeDpo.setAdapter(null);
                                            //spExeDpo.setEnabled(false);

                                            //zList.clear();
                                            spExeZone.setAdapter(null);
                                            //spExeZone.setEnabled(false);
                                            code ="";
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ParamData> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



