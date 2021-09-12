package fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import adapter.AreaAdapter;
import adapter.DepotAdapter;
import adapter.EmpSearchAdapter;
import adapter.RegionAdapter;
import adapter.SearchKeyAdapter;
import adapter.TerritoryAdapter;
import adapter.ZoneAdapter;
import helper.BaseFragment;
import helper.DMYPDialog;
import interfac.ApiService;
import model.ParamArea;
import model.ParamData;
import model.ParamDepot;
import model.ParamEmp;
import model.ParamRegion;
import model.ParamTerritory;
import model.ParamZone;
import model.Temp;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;


public class ReportEmp extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RelativeLayout notFound;
    private LinearLayout btnViewMio;
    private TextView dateEmpPick, btnEmpRpt, btnDoctorRpt,btnChenRpt;
    private Spinner spRptSearch,spZone, spDpo, spRegion, spArea, spTerritory, spRptEmp;
    private Calendar current;
    private SimpleDateFormat ddf;
    private ArrayList<Temp> searchList= new ArrayList<Temp>();
    private List<ParamZone> zList = new ArrayList<ParamZone>();
    private List<ParamDepot> dList = new ArrayList<ParamDepot>();
    private List<ParamRegion> rList = new ArrayList<ParamRegion>();
    private List<ParamArea> aList = new ArrayList<ParamArea>();
    private List<ParamTerritory> tList = new ArrayList<ParamTerritory>();
    private List<ParamEmp> empList = new ArrayList<ParamEmp>();
    private String type="",code="",codeType="",zoneCode="",depotCode="",regionCode="",areaCode="",territoryCode="",empCode="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_emp, container, false);
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

        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        pb = (ProgressBar) getView().findViewById(R.id.pbEmpRpt);
        notFound = (RelativeLayout) getView().findViewById(R.id.empRptNoFound);
        dateEmpPick = (TextView) getView().findViewById(R.id.dateEmpPick);
        btnEmpRpt = (TextView) getView().findViewById(R.id.btnEmpRpt);
        btnDoctorRpt = (TextView) getView().findViewById(R.id.btnDoctorRpt);
        btnChenRpt = (TextView) getView().findViewById(R.id.btnChenRpt);
        spRptSearch = (Spinner) getView().findViewById(R.id.spRptSearch);
        spZone = (Spinner) getView().findViewById(R.id.spRptZone);
        spDpo = (Spinner) getView().findViewById(R.id.spRptDpo);
        spRegion = (Spinner) getView().findViewById(R.id.spRptRegion);
        spArea = (Spinner) getView().findViewById(R.id.spRptArea);
        spTerritory = (Spinner) getView().findViewById(R.id.spRptTerritory);
        spRptEmp = (Spinner) getView().findViewById(R.id.spRptEmp);
        btnViewMio = (LinearLayout) getView().findViewById(R.id.btnViewMio);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }
            }
        });
        current = Calendar.getInstance();
        ddf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        dateEmpPick.setText(ddf.format(current.getTime()));
        dateEmpPick.setOnClickListener(new View.OnClickListener() {
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
                        dateEmpPick.setText(new StringBuilder().append(mm).append("-").append(dd).append("-").append(year).toString());
                    }
                });
                if (getActivity() != null)
                    pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });

        btnEmpRpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && !TextUtils.isEmpty(empCode)) {
                    String dateFrom = dateEmpPick.getText().toString().trim();
                   /* Log.e("MyData","ZoneCode: "+zoneCode);
                    Log.e("MyData","DepotCode: "+depotCode);
                    Log.e("MyData","RegionCode: "+regionCode);
                    Log.e("MyData","AreaCode: "+areaCode);
                    Log.e("MyData","TerritoryCode: "+territoryCode);
                    Log.e("MyData","EmpCode: "+empCode);
                    Log.e("MyData","Date: "+dateFrom);
                    Log.e("MyData",pref.getString(AppConstant.TOKEN, ""))*/;
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneCode);
                    object.addProperty("DepotCode",depotCode);
                    object.addProperty("RegionCode",regionCode);
                    object.addProperty("AreaCode",areaCode);
                    object.addProperty("TerritoryCode",territoryCode);
                    object.addProperty("EmpCode",empCode);
                    object.addProperty("FromDate",dateFrom);
                    object.addProperty("ToDate",dateFrom);
                    ReportMgt mgt = new ReportMgt();
                    Bundle mBundle =new Bundle();
                    mBundle.putString(AppConstant.TYPE_DR_CHEMIST,"typeEmployeeReport");
                    mBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    mgt.setArguments(mBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                    myCommunicator.addContentFragment(mgt, true);
                }else {
                    Toast.makeText(context,"Make sure select employee",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDoctorRpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && !TextUtils.isEmpty(empCode)) {
                    String dateFrom = dateEmpPick.getText().toString().trim();
                   /* Log.e("MyData","ZoneCode: "+zoneCode);
                    Log.e("MyData","DepotCode: "+depotCode);
                    Log.e("MyData","RegionCode: "+regionCode);
                    Log.e("MyData","AreaCode: "+areaCode);
                    Log.e("MyData","TerritoryCode: "+territoryCode);
                    Log.e("MyData","EmpCode: "+empCode);
                    Log.e("MyData","Date: "+dateFrom);
                    Log.e("MyData",pref.getString(AppConstant.TOKEN, ""));*/
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneCode);
                    object.addProperty("DepotCode",depotCode);
                    object.addProperty("RegionCode",regionCode);
                    object.addProperty("AreaCode",areaCode);
                    object.addProperty("TerritoryCode",territoryCode);
                    object.addProperty("EmpCode",empCode);
                    object.addProperty("FromDate",dateFrom);
                    object.addProperty("ToDate",dateFrom);
                    ReportMgt mgt = new ReportMgt();
                    Bundle mBundle =new Bundle();
                    mBundle.putString(AppConstant.TYPE_DR_CHEMIST,"typeDoctorReport");
                    mBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    mgt.setArguments(mBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                    myCommunicator.addContentFragment(mgt, true);
                }else {
                    Toast.makeText(context,"Make sure select employee",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnChenRpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && !TextUtils.isEmpty(empCode)){
                    String dateFrom = dateEmpPick.getText().toString().trim();
                /*    Log.e("MyData","ZoneCode: "+zoneCode);
                    Log.e("MyData","DepotCode: "+depotCode);
                    Log.e("MyData","RegionCode: "+regionCode);
                    Log.e("MyData","AreaCode: "+areaCode);
                    Log.e("MyData","TerritoryCode: "+territoryCode);
                    Log.e("MyData","EmpCode: "+empCode);
                    Log.e("MyData","Date: "+dateFrom);
                    Log.e("MyData",pref.getString(AppConstant.TOKEN, ""));*/
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneCode);
                    object.addProperty("DepotCode",depotCode);
                    object.addProperty("RegionCode",regionCode);
                    object.addProperty("AreaCode",areaCode);
                    object.addProperty("TerritoryCode",territoryCode);
                    object.addProperty("EmpCode",empCode);
                    object.addProperty("FromDate",dateFrom);
                    object.addProperty("ToDate",dateFrom);
                    ReportMgt mgt = new ReportMgt();
                    Bundle mBundle = new Bundle();
                    mBundle.putString(AppConstant.TYPE_DR_CHEMIST, "typeChemistReport");
                    mBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    mgt.setArguments(mBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                    myCommunicator.addContentFragment(mgt, true);
                }else {
                    Toast.makeText(context,"Make sure select employee",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (bundle!=null && "Emp".equals(bundle.getString(AppConstant.TYPE_REPORT)))
            toolbar.setTitle("EMPLOYEE");
          //Log.e("MyData",pref.getString(AppConstant.TOKEN,""));
        if (AppConstant.isOnline(context)) {
            getGetInitParameter(pref.getString(AppConstant.TOKEN, ""));
        } else {
            AppConstant.openDialog(context, "No Internet", context.getResources().getString(R.string.internet_error));
        }
    }

    private void getGetInitParameter(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<ParamData> call = api.getSearchList(header);
        call.enqueue(new Callback<ParamData>() {
            @Override
            public void onResponse(@NonNull Call<ParamData> call, @NonNull retrofit2.Response<ParamData> response) {
                pb.setVisibility(View.GONE);
                ParamData serverResponse = response.body();
                Log.e("MyData", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ) {
                    if (serverResponse.getSearch().size() > 0){
                        searchList.clear();
                        searchList.add(0,new Temp("","Select search key"));
                        searchList.addAll(serverResponse.getSearch());
                        SearchKeyAdapter searchKeyAdapter = new SearchKeyAdapter(context,R.layout.spinner_item,searchList);
                        spRptSearch.setAdapter(searchKeyAdapter);
                        spRptSearch.post(new Runnable() {
                            @Override
                            public void run() {
                                spRptSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        if ((position > 0) && (searchList.size()>1)){
                                            type = searchList.get(position).getCode();
                                            if (spRptSearch.isEnabled() && !TextUtils.isEmpty(type)){
                                                //Log.e("MyDataCheck","Type: "+type);
                                                //Log.e("MyDataCheck","Code: "+code);
                                                //Log.e("MyDataCheck","CodeType: "+codeType);
                                                getEmployeeReportDynamicData(pref.getString(AppConstant.TOKEN, ""),"",type,"");
                                               /* spZone.setSelection(0);
                                                spTerritory.setAdapter(null);
                                                spArea.setAdapter(null);
                                                spRegion.setAdapter(null);
                                                spDpo.setAdapter(null);*/
                                                if (serverResponse.getZones().size() > 0){
                                                    zList.clear();
                                                    zList.add(0, new ParamZone("0", "Select zone"));
                                                    zList.addAll(serverResponse.getZones());
                                                    if (zList.size() > 1) {
                                                        ZoneAdapter adapter = new ZoneAdapter(context, R.layout.spinner_item, zList);
                                                        spZone.setAdapter(adapter);
                                                        spZone.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                spZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                    @Override
                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                        if (position > 0 && zList.size() > 1) {
                                                                            codeType= "Z";
                                                                            zoneCode = zList.get(position).getZONE_CODE();
                                                                            code = zList.get(position).getZONE_CODE();
                                                                          if ( !TextUtils.isEmpty(type)){
                                                                                //Log.e("MyDataCheck","Type: "+type);
                                                                                //Log.e("MyDataCheck","Code: "+code);
                                                                                //Log.e("MyDataCheck","CodeType: "+codeType);
                                                                                getEmployeeReportDynamicData(pref.getString(AppConstant.TOKEN, ""),code,type,codeType);

                                                                            }
                                                                          //Log.e("MyDataCheck","without select depot: "+zList.get(position).getDepots().size());
                                                                            if (zList.get(position).getDepots().size() > 1) {
                                                                                dList.clear();
                                                                                dList.add(0, new ParamDepot("0", "Select depot"));
                                                                                dList.addAll(zList.get(position).getDepots());
                                                                                //Log.e("MyDataCheck","Depot size: "+dList.size());
                                                                                if (dList.size() > 1 &&  !TextUtils.isEmpty(dList.get(position).getDEPOT_CODE())) {
                                                                                    DepotAdapter aDepot = new DepotAdapter(context, R.layout.spinner_item, dList);
                                                                                    spDpo.setAdapter(aDepot);
                                                                                    spDpo.post(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            spDpo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                @Override
                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                    if (position > 0 && dList.size() > 1) {
                                                                                                        codeType= "D";
                                                                                                        code = dList.get(position).getDEPOT_CODE();
                                                                                                        depotCode = dList.get(position).getDEPOT_CODE();
                                                                                                        if (spDpo.isEnabled() && !TextUtils.isEmpty(type)){
                                                                                                            //Log.e("MyDataCheck","Type: "+type);
                                                                                                            //Log.e("MyDataCheck","Code: "+code);
                                                                                                            //Log.e("MyDataCheck","CodeType: "+codeType);
                                                                                                            getEmployeeReportDynamicData(pref.getString(AppConstant.TOKEN, ""),code,type,codeType);
                                                                                                        }
                                                                                                        if (dList.get(position).getRegions().size() > 1) {
                                                                                                            rList.clear();
                                                                                                            rList.add(0, new ParamRegion("0", "Select region"));
                                                                                                            rList.addAll(dList.get(position).getRegions());
                                                                                                            //Log.e("MyDataCheck","region list size: "+rList.size());
                                                                                                            if (rList.size() > 1) {
                                                                                                                RegionAdapter regionAdapter = new RegionAdapter(context, R.layout.spinner_item, rList);
                                                                                                                spRegion.setAdapter(regionAdapter);
                                                                                                                spRegion.post(new Runnable() {
                                                                                                                    @Override
                                                                                                                    public void run() {
                                                                                                                        spRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                            @Override
                                                                                                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                if (position > 0 && rList.size() > 1) {
                                                                                                                                    codeType= "R";
                                                                                                                                    code = rList.get(position).getREGION_CODE();
                                                                                                                                    regionCode = rList.get(position).getREGION_CODE();
                                                                                                                                    if ( spRegion.isEnabled() && !TextUtils.isEmpty(type)){
                                                                                                                                        //Log.e("MyDataCheck","Type: "+type);
                                                                                                                                        //Log.e("MyDataCheck","Code: "+code);
                                                                                                                                        //Log.e("MyDataCheck","CodeType: "+codeType);
                                                                                                                                        getEmployeeReportDynamicData(pref.getString(AppConstant.TOKEN, ""),code,type,codeType);
                                                                                                                                    }
                                                                                                                                    if (rList.get(position).getAreas().size() > 1) {
                                                                                                                                        aList.clear();
                                                                                                                                        aList.add(0, new ParamArea("0", "Select area"));
                                                                                                                                        aList.addAll(rList.get(position).getAreas());

                                                                                                                                        if (aList.size() > 1 && aList.get(position).getAreaName()!= null) {
                                                                                                                                            AreaAdapter aArea = new AreaAdapter(context, R.layout.spinner_item, aList);
                                                                                                                                            spArea.setAdapter(aArea);
                                                                                                                                            spArea.post(new Runnable() {
                                                                                                                                                @Override
                                                                                                                                                public void run() {
                                                                                                                                                    spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                            if (position > 0 && aList.size() > 1) {
                                                                                                                                                                codeType= "A";
                                                                                                                                                                code = aList.get(position).getAREA_CODE();
                                                                                                                                                                areaCode = aList.get(position).getAREA_CODE();
                                                                                                                                                                if (spArea.isEnabled() && !TextUtils.isEmpty(type)){
                                                                                                                                                                    //Log.e("MyDataCheck","Type: "+type);
                                                                                                                                                                    //Log.e("MyDataCheck","Code: "+code);
                                                                                                                                                                    //Log.e("MyDataCheck","CodeType: "+codeType);
                                                                                                                                                                    getEmployeeReportDynamicData(pref.getString(AppConstant.TOKEN, ""),code,type,codeType);
                                                                                                                                                                }
                                                                                                                                                                if (aList.get(position).getTerritories().size() > 1) {
                                                                                                                                                                    tList.clear();
                                                                                                                                                                    tList.addAll(aList.get(position).getTerritories());
                                                                                                                                                                    tList.add(0, new ParamTerritory("0", "Select territory"));
                                                                                                                                                                    if (tList.size() > 1 && tList.get(position).getTerritoryName()!=null ) {
                                                                                                                                                                        TerritoryAdapter aTerritory = new TerritoryAdapter(context, R.layout.spinner_item, tList);
                                                                                                                                                                        spTerritory.setAdapter(aTerritory);
                                                                                                                                                                        spTerritory.post(new Runnable() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void run() {
                                                                                                                                                                                spTerritory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                                        if (position > 0 && tList.size() > 1) {
                                                                                                                                                                                            codeType= "T";
                                                                                                                                                                                            code = tList.get(position).getTERRITORY_CODE();
                                                                                                                                                                                            territoryCode = tList.get(position).getTERRITORY_CODE();
                                                                                                                                                                                            if ( spTerritory.isEnabled() && !TextUtils.isEmpty(type)){
                                                                                                                                                                                                //Log.e("MyDataCheck","Type: "+type);
                                                                                                                                                                                                //Log.e("MyDataCheck","Code: "+code);
                                                                                                                                                                                                //Log.e("MyDataCheck","CodeType: "+codeType);
                                                                                                                                                                                                getEmployeeReportDynamicData(pref.getString(AppConstant.TOKEN, ""),code,type,codeType);
                                                                                                                                                                                            }
                                                                                                                                                                                        }else {
                                                                                                                                                                                            territoryCode = "";
                                                                                                                                                                                            empCode = "";
                                                                                                                                                                                            codeType="";
                                                                                                                                                                                            code="";
                                                                                                                                                                                        }
                                                                                                                                                                                    }

                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                                                                                    }
                                                                                                                                                                                });
                                                                                                                                                                                if (tList.size() == 2) {
                                                                                                                                                                                    spTerritory.setSelection(1);
                                                                                                                                                                                    spTerritory.setEnabled(false);
                                                                                                                                                                                } else {
                                                                                                                                                                                    spTerritory.setEnabled(true);
                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        });
                                                                                                                                                                    }else {
                                                                                                                                                                        empList.clear();
                                                                                                                                                                        spRptEmp.setAdapter(null);
                                                                                                                                                                        tList.clear();
                                                                                                                                                                        spTerritory.setAdapter(null);
                                                                                                                                                                        codeType = "";
                                                                                                                                                                        code="";
                                                                                                                                                                        territoryCode ="";
                                                                                                                                                                        empCode = "";
                                                                                                                                                                    }
                                                                                                                                                                }else {
                                                                                                                                                                    empList.clear();
                                                                                                                                                                    spRptEmp.setAdapter(null);
                                                                                                                                                                    tList.clear();
                                                                                                                                                                    spTerritory.setAdapter(null);
                                                                                                                                                                    codeType ="";
                                                                                                                                                                    code ="";
                                                                                                                                                                    //areaCode ="";
                                                                                                                                                                    territoryCode ="";
                                                                                                                                                                    empCode = "";
                                                                                                                                                                }
                                                                                                                                                            } else {
                                                                                                                                                                empList.clear();
                                                                                                                                                                spRptEmp.setAdapter(null);
                                                                                                                                                                tList.clear();
                                                                                                                                                                spTerritory.setAdapter(null);
                                                                                                                                                                codeType="";
                                                                                                                                                                code="";
                                                                                                                                                                areaCode ="";
                                                                                                                                                                territoryCode ="";
                                                                                                                                                                empCode = "";
                                                                                                                                                            }
                                                                                                                                                        }

                                                                                                                                                        @Override
                                                                                                                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                    if (aList.size() == 2) {
                                                                                                                                                        spArea.setSelection(1);
                                                                                                                                                        spArea.setEnabled(false);
                                                                                                                                                    } else {
                                                                                                                                                        spArea.setEnabled(true);
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                        }else {
                                                                                                                                            empList.clear();
                                                                                                                                            spRptEmp.setAdapter(null);
                                                                                                                                            tList.clear();
                                                                                                                                            spTerritory.setAdapter(null);
                                                                                                                                            aList.clear();
                                                                                                                                            spArea.setAdapter(null);


                                                                                                                                            codeType = "";
                                                                                                                                            code="";
                                                                                                                                            areaCode ="";
                                                                                                                                            territoryCode ="";
                                                                                                                                            empCode = "";
                                                                                                                                        }
                                                                                                                                    }else {
                                                                                                                                        empList.clear();
                                                                                                                                        spRptEmp.setAdapter(null);
                                                                                                                                        tList.clear();
                                                                                                                                        spTerritory.setAdapter(null);
                                                                                                                                        aList.clear();
                                                                                                                                        spArea.setAdapter(null);

                                                                                                                                        codeType ="";
                                                                                                                                        code ="";
                                                                                                                                        //regionCode ="";
                                                                                                                                        areaCode ="";
                                                                                                                                        territoryCode ="";
                                                                                                                                        empCode = "";
                                                                                                                                    }
                                                                                                                                } else {
                                                                                                                                    empList.clear();
                                                                                                                                    spRptEmp.setAdapter(null);
                                                                                                                                    tList.clear();
                                                                                                                                    spTerritory.setAdapter(null);
                                                                                                                                    aList.clear();
                                                                                                                                    spArea.setAdapter(null);
                                                                                                                                    codeType ="";
                                                                                                                                    code="";


                                                                                                                                    regionCode ="";
                                                                                                                                    areaCode ="";
                                                                                                                                    territoryCode ="";
                                                                                                                                    empCode = "";
                                                                                                                                }
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onNothingSelected(AdapterView<?> parent) {

                                                                                                                            }
                                                                                                                        });

                                                                                                                        if (rList.size() == 2) {
                                                                                                                            spRegion.setSelection(1);
                                                                                                                            spRegion.setEnabled(false);
                                                                                                                        } else {
                                                                                                                            spRegion.setEnabled(true);
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                            }else {
                                                                                                                empList.clear();
                                                                                                                spRptEmp.setAdapter(null);
                                                                                                                tList.clear();
                                                                                                                spTerritory.setAdapter(null);
                                                                                                                aList.clear();
                                                                                                                spArea.setAdapter(null);
                                                                                                                rList.clear();
                                                                                                                spRegion.setAdapter(null);

                                                                                                                codeType = "";
                                                                                                                code="";
                                                                                                                regionCode ="";
                                                                                                                areaCode ="";
                                                                                                                territoryCode ="";
                                                                                                                empCode = "";

                                                                                                            }
                                                                                                        }else {
                                                                                                            empList.clear();
                                                                                                            spRptEmp.setAdapter(null);
                                                                                                            tList.clear();
                                                                                                            spTerritory.setAdapter(null);
                                                                                                            aList.clear();
                                                                                                            spArea.setAdapter(null);
                                                                                                            rList.clear();
                                                                                                            spRegion.setAdapter(null);
                                                                                                            codeType ="";
                                                                                                            code ="";
                                                                                                            //depotCode= "";
                                                                                                            regionCode ="";
                                                                                                            areaCode ="";
                                                                                                            territoryCode ="";
                                                                                                            empCode = "";
                                                                                                        }
                                                                                                    } else {
                                                                                                        empList.clear();
                                                                                                        spRptEmp.setAdapter(null);
                                                                                                        tList.clear();
                                                                                                        spTerritory.setAdapter(null);
                                                                                                        aList.clear();
                                                                                                        spArea.setAdapter(null);
                                                                                                        rList.clear();
                                                                                                        spRegion.setAdapter(null);
                                                                                                        codeType ="";
                                                                                                        code ="";
                                                                                                        depotCode= "";
                                                                                                        regionCode ="";
                                                                                                        areaCode ="";
                                                                                                        territoryCode ="";
                                                                                                        empCode = "";
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onNothingSelected(AdapterView<?> parent) {

                                                                                                }
                                                                                            });
                                                                                            if (dList.size() == 2) {
                                                                                                spDpo.setSelection(1);
                                                                                                spDpo.setEnabled(false);
                                                                                            } else {
                                                                                                spDpo.setEnabled(true);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }else {
                                                                                    empList.clear();
                                                                                    spRptEmp.setAdapter(null);
                                                                                    tList.clear();
                                                                                    spTerritory.setAdapter(null);
                                                                                    aList.clear();
                                                                                    spArea.setAdapter(null);
                                                                                    rList.clear();
                                                                                    spRegion.setAdapter(null);

                                                                                    codeType = "";
                                                                                    code="";
                                                                                    depotCode= "";
                                                                                    regionCode ="";
                                                                                    areaCode ="";
                                                                                    territoryCode ="";
                                                                                    empCode = "";
                                                                                }
                                                                            }else {
                                                                                empList.clear();
                                                                                spRptEmp.setAdapter(null);
                                                                                tList.clear();
                                                                                spTerritory.setAdapter(null);
                                                                                aList.clear();
                                                                                spArea.setAdapter(null);
                                                                                rList.clear();
                                                                                spRegion.setAdapter(null);
                                                                                dList.clear();
                                                                                spDpo.setAdapter(null);

                                                                                codeType = "";
                                                                                code="";
                                                                                //zoneCode = "";
                                                                                depotCode= "";
                                                                                regionCode ="";
                                                                                areaCode ="";
                                                                                territoryCode ="";
                                                                                empCode = "";
                                                                            }
                                                                        } else {
                                                                            empList.clear();
                                                                            spRptEmp.setAdapter(null);
                                                                            tList.clear();
                                                                            spTerritory.setAdapter(null);
                                                                            aList.clear();
                                                                            spArea.setAdapter(null);
                                                                            rList.clear();
                                                                            spRegion.setAdapter(null);
                                                                            dList.clear();
                                                                            spDpo.setAdapter(null);
                                                                            codeType = "";
                                                                            code="";
                                                                            zoneCode = "";
                                                                            depotCode= "";
                                                                            regionCode ="";
                                                                            areaCode ="";
                                                                            territoryCode ="";
                                                                            empCode = "";
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                                    }
                                                                });
                                                                if (zList.size() == 2) {
                                                                    spZone.setSelection(1);
                                                                    spZone.setEnabled(false);
                                                                } else {
                                                                    spZone.setEnabled(true);
                                                                }
                                                            }
                                                        });
                                                    }else {
                                                        empList.clear();
                                                        spRptEmp.setAdapter(null);
                                                        tList.clear();
                                                        spTerritory.setAdapter(null);
                                                        aList.clear();
                                                        spArea.setAdapter(null);
                                                        rList.clear();
                                                        spRegion.setAdapter(null);
                                                        dList.clear();
                                                        spDpo.setAdapter(null);
                                                        spZone.setSelection(0);

                                                        codeType = "";
                                                        code="";
                                                        zoneCode="";
                                                        depotCode ="";
                                                        regionCode = "";
                                                        areaCode = "";
                                                        territoryCode = "";
                                                    }
                                                }else {
                                                    empList.clear();
                                                    spRptEmp.setAdapter(null);
                                                    tList.clear();
                                                    spTerritory.setAdapter(null);
                                                    aList.clear();
                                                    spArea.setAdapter(null);
                                                    rList.clear();
                                                    spRegion.setAdapter(null);
                                                    dList.clear();
                                                    spDpo.setAdapter(null);
                                                    codeType = "";
                                                    code="";
                                                    zoneCode="";
                                                    depotCode ="";
                                                    regionCode = "";
                                                    areaCode = "";
                                                    territoryCode = "";
                                                }
                                            }
                                        }else {
                                            empList.clear();
                                            spRptEmp.setAdapter(null);
                                            tList.clear();
                                            spTerritory.setAdapter(null);
                                            aList.clear();
                                            spArea.setAdapter(null);
                                            rList.clear();
                                            spRegion.setAdapter(null);
                                            dList.clear();
                                            spDpo.setAdapter(null);
                                            spZone.setSelection(0);

                                            codeType = "";
                                            code="";
                                            type="";
                                            zoneCode="";
                                            depotCode ="";
                                            regionCode = "";
                                            areaCode = "";
                                            territoryCode = "";
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
                else if(serverResponse.getMessage().trim().equals("Invalid Token.")){
                    AppConstant.logOut(MainActivity.getInstance());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ParamData> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void getEmployeeReportDynamicData(String header,String code,String type,String codeType) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getEmployeeReportDynamicData(header,code,type,codeType);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
               // Log.e("MyData", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null){
                    if (serverResponse.get("Employees").getAsJsonArray().size() > 0){
                        empList.clear();
                        TypeToken<ArrayList<ParamEmp>> token = new TypeToken<ArrayList<ParamEmp>>() {};
                        empList.add(0,new ParamEmp("","Select employee"));
                        empList.addAll(new Gson().fromJson(serverResponse.get("Employees").getAsJsonArray(), token.getType()));
                        //Log.e("MyDataCheck","size: "+empList.size());
                       if (empList.size()>1){
                           EmpSearchAdapter empSearchAdapter = new EmpSearchAdapter(context,R.layout.spinner_item,empList);
                           spRptEmp.setAdapter(empSearchAdapter);
                           spRptEmp.post(new Runnable() {
                               @Override
                               public void run() {
                                   spRptEmp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                       @Override
                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                           if (position>0 && empList.size()>1){
                                               empCode = empList.get(position).getMIOCode();
                                           }else {
                                               empCode="";
                                           }
                                       }
                                       @Override
                                       public void onNothingSelected(AdapterView<?> parent) {

                                       }
                                   });
                               }
                           });
                       }
                    }else {
                        empList.clear();
                        spRptEmp.setAdapter(null);
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



