package fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import adapter.AreaAdapter;
import adapter.DepotAdapter;
import adapter.MioAdapter;
import adapter.RegionAdapter;
import adapter.TerritoryAdapter;
import adapter.ZoneAdapter;
import helper.BaseFragment;
import helper.DMYPDialog;
import interfac.ApiService;
import model.ParamArea;
import model.ParamData;
import model.ParamDepot;
import model.ParamMio;
import model.ParamRegion;
import model.ParamTerritory;
import model.ParamZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;


public class ReportMio extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RelativeLayout notFound;
    private LinearLayout btnViewMio;
    private TextView dateMioPick, btnDrRptShow, btnCheRptShow, btnCurLocation, btnRoadMap;
    private Spinner spZone, spDpo, spRegion, spArea, spTerritory, spMio;
    private Calendar current;
    private SimpleDateFormat ddf;
    private List<ParamZone> zList = new ArrayList<ParamZone>();
    private List<ParamDepot> dList = new ArrayList<ParamDepot>();
    private List<ParamRegion> rList = new ArrayList<ParamRegion>();
    private List<ParamArea> aList = new ArrayList<ParamArea>();
    private List<ParamTerritory> tList = new ArrayList<ParamTerritory>();
    private List<ParamMio> mList = new ArrayList<ParamMio>();
    private String zoneId , depotId , regionId , areaId ,territoryId ,mioId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_list, container, false);
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
        pb = (ProgressBar) getView().findViewById(R.id.pbListReport);
        notFound = (RelativeLayout) getView().findViewById(R.id.listReportNoFound);
        dateMioPick = (TextView) getView().findViewById(R.id.dateMioPick);
        btnDrRptShow = (TextView) getView().findViewById(R.id.btnDrRptShow);
        btnCheRptShow = (TextView) getView().findViewById(R.id.btnCheRptShow);
        spZone = (Spinner) getView().findViewById(R.id.spZone);
        spDpo = (Spinner) getView().findViewById(R.id.spDpo);
        spRegion = (Spinner) getView().findViewById(R.id.spRegion);
        spArea = (Spinner) getView().findViewById(R.id.spArea);
        spTerritory = (Spinner) getView().findViewById(R.id.spTerritory);
        spMio = (Spinner) getView().findViewById(R.id.spMio);
        btnViewMio = (LinearLayout) getView().findViewById(R.id.btnViewMio);
        btnCurLocation = (TextView) getView().findViewById(R.id.btnCurLocation);
        btnRoadMap = (TextView) getView().findViewById(R.id.btnRoadMap);

        if (bundle != null && bundle.getString(AppConstant.TYPE_REPORT).equals("Mio")) {
            toolbar.setTitle("MIO");
            btnCurLocation.setVisibility(View.GONE);
            btnRoadMap.setVisibility(View.GONE);
            btnViewMio.setVisibility(View.VISIBLE);
        } else if (bundle != null && bundle.getString(AppConstant.TYPE_REPORT).equals("CurrentLocation")) {
            toolbar.setTitle("CURRENT LOCATION");
            btnViewMio.setVisibility(View.GONE);
            btnRoadMap.setVisibility(View.GONE);
            dateMioPick.setVisibility(View.GONE);
            btnCurLocation.setVisibility(View.VISIBLE);


        } else if (bundle != null && bundle.getString(AppConstant.TYPE_REPORT).equals("RoadLocation")) {
            toolbar.setTitle("ROAD MAP");
            btnViewMio.setVisibility(View.GONE);
            btnCurLocation.setVisibility(View.GONE);
            dateMioPick.setVisibility(View.VISIBLE);
            btnRoadMap.setVisibility(View.VISIBLE);
        }


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
        dateMioPick.setText(ddf.format(current.getTime()));
        dateMioPick.setOnClickListener(new View.OnClickListener() {
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
                        dateMioPick.setText(new StringBuilder().append(mm).append("-").append(dd).append("-").append(year).toString());
                    }
                });
                if (getActivity() != null)
                    pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });


        btnDrRptShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    String dateFrom = dateMioPick.getText().toString().trim();
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneId);
                    object.addProperty("DepotCode",depotId);
                    object.addProperty("RegionCode",regionId);
                    object.addProperty("AreaCode",areaId);
                    object.addProperty("TerritoryCode",territoryId);
                    object.addProperty("EmpCode",mioId);
                    object.addProperty("FromDate",dateFrom);
                    object.addProperty("ToDate",dateFrom);
                    ReportMgt mgt = new ReportMgt();
                    Bundle mBundle =new Bundle();
                    mBundle.putString(AppConstant.TYPE_DR_CHEMIST,"typeDoctorReport");
                    mBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    mgt.setArguments(mBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                    myCommunicator.setContentFragment(mgt, true);

                }
            }
        });

        btnCheRptShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    String dateFrom = dateMioPick.getText().toString().trim();
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneId);
                    object.addProperty("DepotCode",depotId);
                    object.addProperty("RegionCode",regionId);
                    object.addProperty("AreaCode",areaId);
                    object.addProperty("TerritoryCode",territoryId);
                    object.addProperty("EmpCode",mioId);
                    object.addProperty("FromDate",dateFrom);
                    object.addProperty("ToDate",dateFrom);
                    ReportMgt mgt = new ReportMgt();
                    Bundle mBundle = new Bundle();
                    mBundle.putString(AppConstant.TYPE_DR_CHEMIST, "typeChemistReport");
                    mBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    mgt.setArguments(mBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                    myCommunicator.setContentFragment(mgt, true);
                }
            }
        });

        btnCurLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity()!=null){
                    String dateFrom = dateMioPick.getText().toString().trim();
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneId);
                    object.addProperty("DepotCode",depotId);
                    object.addProperty("RegionCode",regionId);
                    object.addProperty("AreaCode",areaId);
                    object.addProperty("TerritoryCode",territoryId);
                    object.addProperty("EmpCode",mioId);
                    object.addProperty("Date",dateFrom);
//                    Log.e("MyData","ZoneCode: "+zoneId);
//                    Log.e("MyData","DepotCode: "+depotId);
//                    Log.e("MyData","RegionCode: "+regionId);
//                    Log.e("MyData","AreaCode: "+areaId);
//                    Log.e("MyData","TerritoryCode: "+territoryId);
//                    Log.e("MyData","EmpCode: "+mioId);
                    //Log.e("date",dateFrom);

                    ReportInMap mio = new ReportInMap();
                    Bundle clBundle= new Bundle();
                    clBundle.putString(AppConstant.TYPE_REPORT,"CurrentLocation");
                    clBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    mio.setArguments(clBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation,R.anim.exit_animation);
                    myCommunicator.setContentFragment(mio,true);
                }
            }
        });

        btnRoadMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity()!=null){
                    String dateFrom = dateMioPick.getText().toString().trim();
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneId);
                    object.addProperty("DepotCode",depotId);
                    object.addProperty("RegionCode",regionId);
                    object.addProperty("AreaCode",areaId);
                    object.addProperty("TerritoryCode",territoryId);
                    object.addProperty("EmpCode",mioId);
                    object.addProperty("Date",dateFrom);
                    ReportInMap mio = new ReportInMap();
                    Bundle clBundle= new Bundle();
                    clBundle.putString(AppConstant.TYPE_REPORT,"RoadLocation");
                    clBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    mio.setArguments(clBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation,R.anim.exit_animation);
                    myCommunicator.setContentFragment(mio,true);
                }
            }
        });
        //Log.e("Token",pref.getString(AppConstant.TOKEN,""));
        if (AppConstant.isOnline(context)) {
            getGetALLParameter(pref.getString(AppConstant.TOKEN, ""));
        } else {
            AppConstant.openDialog(context, "No Internet", context.getResources().getString(R.string.internet_error));
        }
    }

    private void getGetALLParameter(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<ParamData> call = api.getALLParameter(header);
        call.enqueue(new Callback<ParamData>() {
            @Override
            public void onResponse(@NonNull Call<ParamData> call, @NonNull retrofit2.Response<ParamData> response) {
                pb.setVisibility(View.GONE);
                ParamData serverResponse = response.body();
                Log.e("MyData", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null && serverResponse.getZones().size() > 0) {
                    zoneId = "";
                    depotId = "";
                    regionId = "";
                    areaId = "";
                    territoryId = "";
                    mioId="";
                    zList.clear();
                    zList.addAll(serverResponse.getZones());
                    zList.add(0, new ParamZone("0", "Select zone"));
                    ZoneAdapter adapter = new ZoneAdapter(context, R.layout.spinner_item, zList);
                    spZone.setAdapter(adapter);
                    spZone.post(new Runnable() {
                        @Override
                        public void run() {
                            spZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if ((position > 0) && (zList.size() > 1)) {
                                        zoneId = zList.get(position).getZONE_CODE();
                                        Log.e("Data","<>size<>"+zList.get(position).getDepots().size());
                                        if (zList.get(position).getDepots().size() > 0) {
                                            dList.clear();
                                            dList.addAll(zList.get(position).getDepots());
                                            dList.add(0, new ParamDepot("0", "Select depot"));
                                            DepotAdapter aDepot = new DepotAdapter(context, R.layout.spinner_item, dList);
                                            spDpo.setAdapter(aDepot);
                                            spDpo.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spDpo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if ((position > 0) && (dList.size() > 1)) {
                                                                depotId = dList.get(position).getDEPOT_CODE();
                                                                if (dList.get(position).getRegions().size() > 0) {
                                                                    rList.clear();
                                                                    rList.addAll(dList.get(position).getRegions());
                                                                    rList.add(0, new ParamRegion("0", "Select region"));

                                                                    RegionAdapter aRegion = new RegionAdapter(context, R.layout.spinner_item, rList);
                                                                    spRegion.setAdapter(aRegion);
                                                                    spRegion.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            spRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                @Override
                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                    if ((position > 0) && (rList.size() > 1)) {
                                                                                        regionId = rList.get(position).getREGION_CODE();
                                                                                        if (rList.get(position).getAreas().size() > 0) {
                                                                                            aList.clear();
                                                                                            aList.addAll(rList.get(position).getAreas());
                                                                                            aList.add(0, new ParamArea("0", "Select area"));
                                                                                            AreaAdapter aArea = new AreaAdapter(context, R.layout.spinner_item, aList);
                                                                                            spArea.setAdapter(aArea);
                                                                                            spArea.post(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                        @Override
                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                            if ((position > 0) && (aList.size() > 1)) {
                                                                                                                areaId = aList.get(position).getAREA_CODE();
                                                                                                                if (aList.get(position).getTerritories().size()>0){
                                                                                                                    tList.clear();
                                                                                                                    tList.addAll(aList.get(position).getTerritories());
                                                                                                                    tList.add(0, new ParamTerritory("0", "Select territory"));
                                                                                                                    TerritoryAdapter aTerritory = new TerritoryAdapter(context, R.layout.spinner_item, tList);
                                                                                                                    spTerritory.setAdapter(aTerritory);
                                                                                                                    spTerritory.post(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            spTerritory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                @Override
                                                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                    if ((position > 0) && (tList.size() > 1)) {
                                                                                                                                        territoryId = tList.get(position).getTERRITORY_CODE();
                                                                                                                                        if (tList.get(position).getMios().size()>0){
                                                                                                                                            mList.clear();
                                                                                                                                            mList.addAll(tList.get(position).getMios());
                                                                                                                                            mList.add(0, new ParamMio("0", "Select mio"));
                                                                                                                                            MioAdapter aMio = new MioAdapter(context, R.layout.spinner_item, mList);
                                                                                                                                            spMio.setAdapter(aMio);
                                                                                                                                            spMio.post(new Runnable() {
                                                                                                                                                @Override
                                                                                                                                                public void run() {
                                                                                                                                                    spMio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                            if ((position > 0) && (mList.size() > 1)) {
                                                                                                                                                                mioId = mList.get(position).getEMP_ID();
                                                                                                                                                            } else {
                                                                                                                                                                mioId = "";
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                        @Override
                                                                                                                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                    if (mList.size() == 2) {
                                                                                                                                                        spMio.setSelection(1);
                                                                                                                                                        spMio.setEnabled(false);
                                                                                                                                                    }else{
                                                                                                                                                        spMio.setEnabled(true);
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                        }
                                                                                                                                    } else {
                                                                                                                                        territoryId = "";
                                                                                                                                    }
                                                                                                                                }
                                                                                                                                @Override
                                                                                                                                public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                }
                                                                                                                            });
                                                                                                                            if (tList.size() == 2) {
                                                                                                                                spTerritory.setSelection(1);
                                                                                                                                spTerritory.setEnabled(false);
                                                                                                                            }else {
                                                                                                                                spTerritory.setEnabled(true);
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            } else {
                                                                                                                areaId = "";
                                                                                                            }
                                                                                                        }
                                                                                                        @Override
                                                                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                                                                        }
                                                                                                    });
                                                                                                    if (aList.size() == 2) {
                                                                                                        spArea.setSelection(1);
                                                                                                        spArea.setEnabled(false);
                                                                                                    }else {
                                                                                                        spArea.setEnabled(true);
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                    } else {
                                                                                        regionId = "";
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onNothingSelected(AdapterView<?> parent) {
                                                                                }
                                                                            });
                                                                            if (rList.size() == 2) {
                                                                                spRegion.setSelection(1);
                                                                                spRegion.setEnabled(false);
                                                                            }else {
                                                                                spRegion.setEnabled(true);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                depotId = "";
                                                            }
                                                        }
                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                        }
                                                    });
                                                    if (dList.size() == 2) {
                                                        spDpo.setSelection(1);
                                                        spDpo.setEnabled(false);
                                                    }else {
                                                        spDpo.setEnabled(true);
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        zoneId = "";
                                    }
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                            if (zList.size() == 2) {
                                spZone.setSelection(1);
                                spZone.setEnabled(false);
                            }else {
                                spZone.setEnabled(true);
                            }
                        }
                    });
                }
                else if (serverResponse != null && serverResponse.getMessage().trim().equals("Invalid Token.")) {
                    AppConstant.logOut(MainActivity.getInstance());
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



