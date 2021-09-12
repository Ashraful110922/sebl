package fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
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
import adapter.ChemListAdapter;
import adapter.ChemistListAdapter;
import adapter.DepotAdapter;
import adapter.DoctorListAdapter;
import adapter.DrListAdapter;
import adapter.MarketAdapter;
import adapter.RegionAdapter;
import adapter.TerritoryAdapter;
import adapter.ZoneAdapter;
import dialog.SetTimeReportFragment;
import helper.BaseFragment;
import helper.DMYPDialog;
import interfac.ApiService;
import model.ParamArea;
import model.ParamChemist;
import model.ParamData;
import model.ParamDepot;
import model.ParamDoctor;
import model.ParamMarket;
import model.ParamRegion;
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


public class ReportDoctor extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RelativeLayout notFound;
    private TextView btnDrReport, ddDrPick, ttDrPicker;
    private LinearLayout viewDrZone, viewDrDpo, viewDrRegion, viewDrArea, viewDrTerritory, viewDrMarket, viewDoctor, viewChemist;
    private Spinner spDrZone, spDrDpo, spDrRegion, spDrArea, spDrTerritory, spDrMarket, spDoctor, spChemist;
    private TextView btnChem;
    private Calendar current;
    private SimpleDateFormat ddf, ttf;
    private Dialog dialog;
    private List<ParamZone> zList = new ArrayList<ParamZone>();
    private List<ParamDepot> dList = new ArrayList<ParamDepot>();
    private List<ParamRegion> rList = new ArrayList<ParamRegion>();
    private List<ParamArea> aList = new ArrayList<ParamArea>();
    private List<ParamTerritory> tList = new ArrayList<ParamTerritory>();
    private List<ParamMarket> mkList = new ArrayList<ParamMarket>();
    private List<ParamDoctor> drList = new ArrayList<ParamDoctor>();
    private List<ParamChemist> cheList = new ArrayList<ParamChemist>();
    private List<ParamChemist> tempList = new ArrayList<ParamChemist>();
    private int drId, chemistId;
    private String zoneId = "", depotId = "", regionId = "", areaId = "", territoryId = "", marketId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_dr, container, false);
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
        pb = (ProgressBar) getView().findViewById(R.id.pbDrReport);
        notFound = (RelativeLayout) getView().findViewById(R.id.drReportNoFound);
        btnDrReport = (TextView) getView().findViewById(R.id.btnDrReport);
        ddDrPick = (TextView) getView().findViewById(R.id.ddDrPick);
        ttDrPicker = (TextView) getView().findViewById(R.id.ttDrPicker);
        viewDrZone = (LinearLayout) getView().findViewById(R.id.viewDrZone);
        viewDrDpo = (LinearLayout) getView().findViewById(R.id.viewDrDpo);
        viewDrRegion = (LinearLayout) getView().findViewById(R.id.viewDrRegion);
        viewDrArea = (LinearLayout) getView().findViewById(R.id.viewDrArea);
        viewDrTerritory = (LinearLayout) getView().findViewById(R.id.viewDrTerritory);
        viewDrMarket = (LinearLayout) getView().findViewById(R.id.viewDrMarket);
        viewDoctor = (LinearLayout) getView().findViewById(R.id.viewDoctor);
        viewChemist = (LinearLayout) getView().findViewById(R.id.viewChemist);
        spDrZone = (Spinner) getView().findViewById(R.id.spDrZone);
        spDrDpo = (Spinner) getView().findViewById(R.id.spDrDpo);
        spDrRegion = (Spinner) getView().findViewById(R.id.spDrRegion);
        spDrArea = (Spinner) getView().findViewById(R.id.spDrArea);
        spDrTerritory = (Spinner) getView().findViewById(R.id.spDrTerritory);
        spDrMarket = (Spinner) getView().findViewById(R.id.spDrMarket);
        spDoctor = (Spinner) getView().findViewById(R.id.spDoctor);
        //spChemist = (Spinner) getView().findViewById(R.id.spChemist);
        btnChem = (TextView) getView().findViewById(R.id.btnChem);

        viewDoctor.setVisibility(View.GONE);
        viewChemist.setVisibility(View.GONE);
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
        ddDrPick.setText(ddf.format(current.getTime()));
        ttDrPicker.setText(ddf.format(current.getTime()));
        ddDrPick.setOnClickListener(new View.OnClickListener() {
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
                        ddDrPick.setText(new StringBuilder().append(mm).append("-").append(dd).append("-").append(year).toString());
                    }
                });
                if (getActivity() != null)
                    pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });

        //ttf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        //ttDrPicker.setText(ttf.format(current.getTime()));
        ttDrPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DialogFragment dFragment = new SetTimeReportFragment();
                dFragment.show(getChildFragmentManager(), "Time Picker");*/
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
                        ttDrPicker.setText(new StringBuilder().append(mm).append("-").append(dd).append("-").append(year).toString());
                    }
                });
                if (getActivity() != null)
                    pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");

            }
        });


        btnDrReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bundle != null && bundle.getString(AppConstant.TYPE_DR_CHEMIST, "").equals("typeDr") && getActivity() != null) {

                    String date= ddDrPick.getText().toString().trim();
                    String time= ttDrPicker.getText().toString().trim();

                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneId);
                    object.addProperty("DepotCode",depotId);
                    object.addProperty("RegionCode",regionId);
                    object.addProperty("AreaCode",areaId);
                    object.addProperty("TerritoryCode",territoryId);
                    object.addProperty("MarketCode",marketId);
                    object.addProperty("Id",drId);
                    object.addProperty("FromDate",date);
                    object.addProperty("ToDate",time);
                    //object.addProperty("time",time);
                   Log.e("MyData",new Gson().toJson(object));

                    if (drId>0){
                        ReportMgt dr = new ReportMgt();
                        Bundle dBundle = new Bundle();
                        dBundle.putString(AppConstant.TYPE_DR_CHEMIST, "typeDr");
                        dBundle.putString("SET_PARAMS",new Gson().toJson(object));
                        dr.setArguments(dBundle);
                        getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                        myCommunicator.setContentFragment(dr, true);
                    }else {
                        Toast.makeText(context,"Please select doctor",Toast.LENGTH_SHORT).show();
                        return;
                    }

            /*        Log.e("MyData","ZoneCode: "+zoneId);
                    Log.e("MyData","DepotCode: "+depotId);
                    Log.e("MyData","RegionCode: "+regionId);
                    Log.e("MyData","AreaCode: "+areaId);
                    Log.e("MyData","TerritoryCode: "+territoryId);
                    Log.e("MyData","MarketCode: "+marketId);
                    Log.e("MyData","Id: "+drId);
                    Log.e("MyData","FromDate: "+date);
                    Log.e("MyData","ToDate: "+date);
                    Log.e("MyData","time: "+time);*/


                } else if (bundle != null && bundle.getString(AppConstant.TYPE_DR_CHEMIST, "").equals("typeChemist") && getActivity() != null) {
                    String date= ddDrPick.getText().toString().trim();
                    String time= ttDrPicker.getText().toString().trim();
                    JsonObject object = new JsonObject();
                    object.addProperty("ZoneCode",zoneId);
                    object.addProperty("DepotCode",depotId);
                    object.addProperty("RegionCode",regionId);
                    object.addProperty("AreaCode",areaId);
                    object.addProperty("TerritoryCode",territoryId);
                    object.addProperty("MarketCode",marketId);
                    if (chemistId>0){
                        object.addProperty("Id",chemistId);
                    }else {
                        Toast.makeText(context,"Please select chemist",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    object.addProperty("FromDate",date);
                    object.addProperty("ToDate",time);

                    Log.e("MyData",new Gson().toJson(object));
                    ReportMgt chemist = new ReportMgt();
                    Bundle cBundle = new Bundle();
                    cBundle.putString(AppConstant.TYPE_DR_CHEMIST, "typeChemist");
                    cBundle.putString("SET_PARAMS",new Gson().toJson(object));
                    chemist.setArguments(cBundle);
                    getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                    myCommunicator.setContentFragment(chemist, true);
                }
            }
        });

        if (bundle != null && bundle.getString(AppConstant.TYPE_DR_CHEMIST, "").equals("typeDr")) {
            toolbar.setTitle("DOCTOR REPORT");
            viewChemist.setVisibility(View.GONE);
            viewDoctor.setVisibility(View.VISIBLE);
            getALLParameterDoctor(pref.getString(AppConstant.TOKEN, ""));
        } else if (bundle != null && bundle.getString(AppConstant.TYPE_DR_CHEMIST, "").equals("typeChemist")) {
            toolbar.setTitle("CHEMIST REPORT");
            viewDoctor.setVisibility(View.GONE);
            viewChemist.setVisibility(View.VISIBLE);
            getALLParameterChemist(pref.getString(AppConstant.TOKEN, ""));
        }
    }

    private void getALLParameterDoctor(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<ParamData> call = api.getALLParameterDoctor(header);
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
                    marketId = "";
                    drId=0;
                    zList.clear();
                    zList.addAll(serverResponse.getZones());
                    zList.add(0, new ParamZone("0", "Select zone"));
                    ZoneAdapter adapter = new ZoneAdapter(context, R.layout.spinner_item, zList);
                    spDrZone.setAdapter(adapter);
                    spDrZone.post(new Runnable() {
                        @Override
                        public void run() {
                            spDrZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if ((position > 0) && (zList.size() > 1)) {
                                        zoneId = zList.get(position).getZONE_CODE();
                                        if (zList.get(position).getDepots().size() > 0) {
                                            dList.clear();
                                            dList.addAll(zList.get(position).getDepots());
                                            dList.add(0, new ParamDepot("0", "Select depot"));
                                            DepotAdapter aDepot = new DepotAdapter(context, R.layout.spinner_item, dList);
                                            spDrDpo.setAdapter(aDepot);
                                            spDrDpo.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spDrDpo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if ((position > 0) && (dList.size() > 1)) {
                                                                depotId = dList.get(position).getDEPOT_CODE();
                                                                if (dList.get(position).getRegions().size() > 0) {
                                                                    rList.clear();
                                                                    rList.addAll(dList.get(position).getRegions());
                                                                    rList.add(0, new ParamRegion("0", "Select region"));
                                                                    RegionAdapter aRegion = new RegionAdapter(context, R.layout.spinner_item, rList);
                                                                    spDrRegion.setAdapter(aRegion);
                                                                    spDrRegion.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            spDrRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                @Override
                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                    if ((position > 0) && (rList.size() > 1)) {
                                                                                        regionId = rList.get(position).getREGION_CODE();
                                                                                        if (rList.get(position).getAreas().size() > 0) {
                                                                                            aList.clear();
                                                                                            aList.addAll(rList.get(position).getAreas());
                                                                                            aList.add(0, new ParamArea("0", "Select area"));
                                                                                            AreaAdapter aArea = new AreaAdapter(context, R.layout.spinner_item, aList);
                                                                                            spDrArea.setAdapter(aArea);
                                                                                            spDrArea.post(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    spDrArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                        @Override
                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                            if ((position > 0) && (aList.size() > 1)) {
                                                                                                                areaId = aList.get(position).getAREA_CODE();
                                                                                                                if (aList.get(position).getTerritories().size() > 0) {
                                                                                                                    tList.clear();
                                                                                                                    tList.addAll(aList.get(position).getTerritories());
                                                                                                                    tList.add(0, new ParamTerritory("0", "Select territory"));
                                                                                                                    TerritoryAdapter aTerritory = new TerritoryAdapter(context, R.layout.spinner_item, tList);
                                                                                                                    spDrTerritory.setAdapter(aTerritory);
                                                                                                                    spDrTerritory.post(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            spDrTerritory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                @Override
                                                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                    if ((position > 0) && (tList.size() > 1)) {
                                                                                                                                        territoryId = tList.get(position).getTERRITORY_CODE();
                                                                                                                                        if (tList.get(position).getMarkets().size() > 0) {
                                                                                                                                            mkList.clear();
                                                                                                                                            mkList.addAll(tList.get(position).getMarkets());
                                                                                                                                            mkList.add(0, new ParamMarket("0", "Select market"));
                                                                                                                                            MarketAdapter aMarket = new MarketAdapter(context, R.layout.spinner_item, mkList);
                                                                                                                                            spDrMarket.setAdapter(aMarket);
                                                                                                                                            spDrMarket.post(new Runnable() {
                                                                                                                                                @Override
                                                                                                                                                public void run() {
                                                                                                                                                    spDrMarket.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                            if ((position > 0) && (mkList.size() > 1)) {
                                                                                                                                                                marketId = mkList.get(position).getCode();
                                                                                                                                                                if (mkList.get(position).getDoctors().size() > 0) {
                                                                                                                                                                    drList.clear();
                                                                                                                                                                    drList.addAll(mkList.get(position).getDoctors());
                                                                                                                                                                    drList.add(0, new ParamDoctor(0, "Select doctor"));
                                                                                                                                                                    DoctorListAdapter aDr = new DoctorListAdapter(context, R.layout.spinner_item, drList);
                                                                                                                                                                    spDoctor.setAdapter(aDr);
                                                                                                                                                                    spDoctor.post(new Runnable() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void run() {
                                                                                                                                                                            spDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                                    if ((position > 0) && (drList.size() > 1) && !TextUtils.isEmpty(drList.get(position).getDoctorName())) {
                                                                                                                                                                                        drId = drList.get(position).getDoctorID();
                                                                                                                                                                                    } else {
                                                                                                                                                                                        drList.clear();
                                                                                                                                                                                        spDoctor.setAdapter(null);
                                                                                                                                                                                        drId = 0;
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                                                                }
                                                                                                                                                                            });
                                                                                                                                                                            if (drList.size() == 2) {
                                                                                                                                                                                spDoctor.setSelection(1);
                                                                                                                                                                                spDoctor.setEnabled(false);
                                                                                                                                                                            } else {
                                                                                                                                                                                spDoctor.setEnabled(true);
                                                                                                                                                                            }

                                                                                                                                                                        }
                                                                                                                                                                    });
                                                                                                                                                                }
                                                                                                                                                            } else {
                                                                                                                                                                marketId = "";
                                                                                                                                                            }
                                                                                                                                                        }

                                                                                                                                                        @Override
                                                                                                                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                    if (mkList.size() == 2) {
                                                                                                                                                        spDrMarket.setSelection(1);
                                                                                                                                                        spDrMarket.setEnabled(false);
                                                                                                                                                    } else {
                                                                                                                                                        spDrMarket.setEnabled(true);
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
                                                                                                                                spDrTerritory.setSelection(1);
                                                                                                                                spDrTerritory.setEnabled(false);
                                                                                                                            } else {
                                                                                                                                spDrTerritory.setEnabled(true);
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
                                                                                                        spDrArea.setSelection(1);
                                                                                                        spDrArea.setEnabled(false);
                                                                                                    } else {
                                                                                                        spDrArea.setEnabled(true);
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
                                                                                spDrRegion.setSelection(1);
                                                                                spDrRegion.setEnabled(false);
                                                                            } else {
                                                                                spDrRegion.setEnabled(true);
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
                                                        spDrDpo.setSelection(1);
                                                        spDrDpo.setEnabled(false);
                                                    } else {
                                                        spDrDpo.setEnabled(true);
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
                                spDrZone.setSelection(1);
                                spDrZone.setEnabled(false);
                            } else {
                                spDrZone.setEnabled(true);
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


    private void getALLParameterChemist(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<ParamData> call = api.getALLParameterChemist(header);
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
                    marketId = "";
                    chemistId = 0;
                    zList.clear();
                    zList.addAll(serverResponse.getZones());
                    zList.add(0, new ParamZone("0", "Select zone"));
                    ZoneAdapter adapter = new ZoneAdapter(context, R.layout.spinner_item, zList);
                    spDrZone.setAdapter(adapter);
                    spDrZone.post(new Runnable() {
                        @Override
                        public void run() {
                            spDrZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if ((position > 0) && (zList.size() > 1)) {
                                        zoneId = zList.get(position).getZONE_CODE();
                                        if (zList.get(position).getDepots().size() > 0) {
                                            dList.clear();
                                            dList.addAll(zList.get(position).getDepots());
                                            dList.add(0, new ParamDepot("0", "Select depot"));
                                            DepotAdapter aDepot = new DepotAdapter(context, R.layout.spinner_item, dList);
                                            spDrDpo.setAdapter(aDepot);
                                            spDrDpo.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spDrDpo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if ((position > 0) && (dList.size() > 1)) {
                                                                depotId = dList.get(position).getDEPOT_CODE();
                                                                if (dList.get(position).getRegions().size() > 0) {
                                                                    rList.clear();
                                                                    rList.addAll(dList.get(position).getRegions());
                                                                    rList.add(0, new ParamRegion("0", "Select region"));
                                                                    RegionAdapter aRegion = new RegionAdapter(context, R.layout.spinner_item, rList);
                                                                    spDrRegion.setAdapter(aRegion);
                                                                    spDrRegion.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            spDrRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                @Override
                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                    if ((position > 0) && (rList.size() > 1)) {
                                                                                        regionId = rList.get(position).getREGION_CODE();
                                                                                        if (rList.get(position).getAreas().size() > 0) {
                                                                                            aList.clear();
                                                                                            aList.addAll(rList.get(position).getAreas());
                                                                                            aList.add(0, new ParamArea("0", "Select area"));
                                                                                            AreaAdapter aArea = new AreaAdapter(context, R.layout.spinner_item, aList);
                                                                                            spDrArea.setAdapter(aArea);
                                                                                            spDrArea.post(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    spDrArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                        @Override
                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                            if ((position > 0) && (aList.size() > 1)) {
                                                                                                                areaId = aList.get(position).getAREA_CODE();
                                                                                                                if (aList.get(position).getTerritories().size() > 0) {
                                                                                                                    tList.clear();
                                                                                                                    tList.addAll(aList.get(position).getTerritories());
                                                                                                                    tList.add(0, new ParamTerritory("0", "Select territory"));
                                                                                                                    TerritoryAdapter aTerritory = new TerritoryAdapter(context, R.layout.spinner_item, tList);
                                                                                                                    spDrTerritory.setAdapter(aTerritory);
                                                                                                                    spDrTerritory.post(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            spDrTerritory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                @Override
                                                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                    if ((position > 0) && (tList.size() > 1)) {
                                                                                                                                        territoryId = tList.get(position).getTERRITORY_CODE();
                                                                                                                                        if (tList.get(position).getMarkets().size() > 0) {
                                                                                                                                            mkList.clear();
                                                                                                                                            mkList.addAll(tList.get(position).getMarkets());
                                                                                                                                            mkList.add(0, new ParamMarket("0", "Select market"));
                                                                                                                                            MarketAdapter aMarket = new MarketAdapter(context, R.layout.spinner_item, mkList);
                                                                                                                                            spDrMarket.setAdapter(aMarket);
                                                                                                                                            spDrMarket.post(new Runnable() {
                                                                                                                                                @Override
                                                                                                                                                public void run() {
                                                                                                                                                    spDrMarket.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                            if ((position > 0) && (mkList.size() > 1)) {
                                                                                                                                                                marketId = mkList.get(position).getCode();
                                                                                                                                                                if (mkList.get(position).getChemists().size()>1 ){
                                                                                                                                                                    cheList.clear();
                                                                                                                                                                    cheList.addAll(mkList.get(position).getChemists());
                                                                                                                                                                    cheList.add(cheList.size(),new ParamChemist(0,"Add new chemist"));
                                                                                                                                                                    dialog = new Dialog(context);
                                                                                                                                                                    dialog.setContentView(R.layout.dialog_searchable_spinner);
                                                                                                                                                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                                                                                                    EditText editText = dialog.findViewById(R.id.edit_text);
                                                                                                                                                                    ListView listView = dialog.findViewById(R.id.list_view);
                                                                                                                                                                    ChemListAdapter aChemist = new ChemListAdapter(context, R.layout.spinner_item, cheList);
                                                                                                                                                                    btnChem.setOnClickListener(new View.OnClickListener() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onClick(View v) {
                                                                                                                                                                            listView.setAdapter(aChemist);
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
                                                                                                                                                                                    for(ParamChemist c: cheList){
                                                                                                                                                                                        if (textLength <= c.getChemistName().length()) {
                                                                                                                                                                                            if (c.getChemistName().toLowerCase().contains(s.toString().toLowerCase())) {
                                                                                                                                                                                                tempList.add(c);
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                    ChemListAdapter listAdapter = new ChemListAdapter(context, R.layout.spinner_item, tempList);
                                                                                                                                                                                    listView.setAdapter(listAdapter);
                                                                                                                                                                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                                                                                                                                        @Override
                                                                                                                                                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                                            ParamChemist user = listAdapter.getItem(position);
                                                                                                                                                                                            btnChem.setText(Html.fromHtml("<font color='#000000'><b><small>"+user.getChemistName()+"</small></b></font><br><small><font color='#0000FF'>"+user.getChemistID() +"</font></small><hr>"));
                                                                                                                                                                                            if (user.getChemistName().equalsIgnoreCase("Add new chemist")){
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
                                                                                                                                                                                    ParamChemist user = aChemist.getItem(position);
                                                                                                                                                                                        btnChem.setText(Html.fromHtml("<font color='#000000'><b><small>"+user.getChemistName()+"</small></b></font><br><small><font color='#0000FF'>"+user.getChemistID() +"</font></small><hr>"));
                                                                                                                                                                                    if (user.getChemistName().equalsIgnoreCase("Add new chemist")){
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
                                                                                                                                                                    cheList.clear();
                                                                                                                                                                    chemistId =0;
                                                                                                                                                                }
                                                                                                                                                            } else {
                                                                                                                                                                marketId = "";
                                                                                                                                                            }
                                                                                                                                                        }

                                                                                                                                                        @Override
                                                                                                                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                    if (mkList.size() == 2) {
                                                                                                                                                        spDrMarket.setSelection(1);
                                                                                                                                                        spDrMarket.setEnabled(false);
                                                                                                                                                    } else {
                                                                                                                                                        spDrMarket.setEnabled(true);
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
                                                                                                                                spDrTerritory.setSelection(1);
                                                                                                                                spDrTerritory.setEnabled(false);
                                                                                                                            } else {
                                                                                                                                spDrTerritory.setEnabled(true);
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
                                                                                                        spDrArea.setSelection(1);
                                                                                                        spDrArea.setEnabled(false);
                                                                                                    } else {
                                                                                                        spDrArea.setEnabled(true);
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
                                                                                spDrRegion.setSelection(1);
                                                                                spDrRegion.setEnabled(false);
                                                                            } else {
                                                                                spDrRegion.setEnabled(true);
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
                                                        spDrDpo.setSelection(1);
                                                        spDrDpo.setEnabled(false);
                                                    } else {
                                                        spDrDpo.setEnabled(true);
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
                                spDrZone.setSelection(1);
                                spDrZone.setEnabled(false);
                            } else {
                                spDrZone.setEnabled(true);
                            }
                        }
                    });
                }
                else if (serverResponse != null && serverResponse.getMessage().trim().equals("Invalid Token.")){
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



