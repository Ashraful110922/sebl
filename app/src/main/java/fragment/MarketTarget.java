package fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.opl.one.oplsales.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import adapter.AreaAdapter;
import adapter.DepotAdapter;
import adapter.MarketAdapter;
import adapter.MioAdapter;
import adapter.RegionAdapter;
import adapter.RosterAdapter;
import adapter.TerritoryAdapter;
import adapter.ZoneAdapter;
import dialog.SetTimeMarketFragment;
import helper.BaseFragment;
import helper.DMYPDialog;
import interfac.ApiService;
import modal.ErrorMessageModal;
import model.ParamArea;
import model.ParamData;
import model.ParamDepot;
import model.ParamMarket;
import model.ParamMio;
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


public class MarketTarget extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private TextView dMktPick,tMktPicker,btnMktSet;
    private TextInputEditText opinionMkt;
    private Spinner spMktRst,spMktSet,spMktZone,spMktDpo,spMktRegion,spMktArea,spMktTerritory,spMktMio;
    private int intRosterId= 0,mktId=0;
    private ArrayList<TinyUser> list= new ArrayList<TinyUser>();
    private List<ParamMarket> mkList = new ArrayList<ParamMarket>();
    private Calendar current;
    private SimpleDateFormat df,tf;
    private List<ParamZone> zList = new ArrayList<ParamZone>();
    private List<ParamDepot> dList = new ArrayList<ParamDepot>();
    private List<ParamRegion> rList = new ArrayList<ParamRegion>();
    private List<ParamArea> aList = new ArrayList<ParamArea>();
    private List<ParamTerritory> tList = new ArrayList<ParamTerritory>();
    private List<ParamMio> mList = new ArrayList<ParamMio>();
    private String zoneCd , depotCd , regionCd , areaCd ,territoryId ,mioCd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.market_plan, container, false);
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
        pb = (ProgressBar) getView().findViewById(R.id.pbTrgMkt);
        //mioName = (TextView) getView().findViewById(R.id.mioName);
        //aCode = (TextView) getView().findViewById(R.id.aCode);
        dMktPick = (TextView) getView().findViewById(R.id.dMktPick);
        tMktPicker = (TextView) getView().findViewById(R.id.tMktPicker);
        btnMktSet = (TextView) getView().findViewById(R.id.btnMktSet);
        opinionMkt = (TextInputEditText) getView().findViewById(R.id.opinionMkt);
        spMktRst = (Spinner) getView().findViewById(R.id.spMktRst);
        spMktSet = (Spinner) getView().findViewById(R.id.spMktSet);
        spMktZone = (Spinner) getView().findViewById(R.id.spMktZone);
        spMktDpo = (Spinner) getView().findViewById(R.id.spMktDpo);
        spMktRegion = (Spinner) getView().findViewById(R.id.spMktRegion);
        spMktArea = (Spinner) getView().findViewById(R.id.spMktArea);
        spMktTerritory = (Spinner) getView().findViewById(R.id.spMktTerritory);
        spMktMio = (Spinner) getView().findViewById(R.id.spMktMio);

        current=Calendar.getInstance();
        //df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        current.add(Calendar.DAY_OF_MONTH, 1);
        dMktPick.setText(df.format(current.getTime()));

        dMktPick.setOnClickListener(new View.OnClickListener() {
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
                        dMktPick.setText(mm + "/" + dd + "/" + year);
                    }
                });
                pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });

        tf = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        tMktPicker.setText(tf.format(current.getTime()));

        tMktPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new SetTimeMarketFragment();
                dFragment.show(getChildFragmentManager(),"Time Picker");
            }
        });

        list.clear();
        list.add(0,new TinyUser(0,"Select roster"));
        list.add(1,new TinyUser(1,"Morning"));
        list.add(2,new TinyUser(2,"Evening"));
        RosterAdapter rosterAdapter = new RosterAdapter(context,R.layout.spinner_item,list);
        spMktRst.setAdapter(rosterAdapter);

        spMktRst.post(new Runnable() {
            @Override
            public void run() {
                spMktRst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ((position > 0) && (list.size()>1)) {
                            intRosterId = list.get(position).getId();
                        }else {
                            intRosterId =0;
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
        btnMktSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dd =dMktPick.getText().toString().trim();
                String tt = tMktPicker.getText().toString().trim();
                String remark = opinionMkt.getText().toString().trim();
                Bundle bundle = new Bundle();
                ErrorMessageModal modal = new ErrorMessageModal();
                if (intRosterId<=0){
                    bundle.putString("ERROR_MESSAGE","Please select roster type");
                    modal.setArguments(bundle);
                    modal.show(getChildFragmentManager(),"showError");
                    return;
                }else {
                    JsonObject object = new JsonObject();
                    object.addProperty("RosterID",intRosterId);
                    object.addProperty("visitDate",dd);
                    object.addProperty("VisitTime",tt);
                    object.addProperty("Opinion",remark);
                    object.addProperty("ZoneCode",zoneCd);
                    object.addProperty("DepotCode",depotCd);
                    object.addProperty("RegionCode",regionCd);
                    object.addProperty("AreaCode",areaCd);
                    object.addProperty("TerritoryCode",territoryId);
                    object.addProperty("MioCode",mioCd);
                    object.addProperty("MarketID",mktId);
                    Log.e("MyData",""+new Gson().toJson(object));
                    Log.e("MyData",pref.getString(AppConstant.TOKEN,""));
                    if (AppConstant.isOnline(context)){
                       setPlanMarket(pref.getString(AppConstant.TOKEN,""),object);
                    }else {
                        AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                    }
                }
            }
        });
        if (AppConstant.isOnline(context)){
             getGetALLParameter(pref.getString(AppConstant.TOKEN,""));
        }else {
            AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
        }
    }


    private void setPlanMarket(String header,JsonObject object){
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.setPlanMarket(header,object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData",new Gson().toJson(serverResponse));
                if(response.code()== 200 && serverResponse!=null) {
                    boolean status = serverResponse.get("status").getAsBoolean();
                    Toast.makeText(context,serverResponse.get("message").getAsString(),Toast.LENGTH_SHORT).show();
                    if(status){
                        opinionMkt.setText("");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        if (Build.VERSION.SDK_INT >= 26) {
                            ft.setReorderingAllowed(false);
                        }
                        ft.detach(MarketTarget.this).attach(MarketTarget.this).commit();
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
                Log.e("response", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null && serverResponse.getZones().size() > 0) {
                    zoneCd = "";
                    depotCd = "";
                    regionCd = "";
                    areaCd = "";
                    territoryId = "";
                    mioCd="";
                    mktId=0;
                    zList.clear();
                    zList.addAll(serverResponse.getZones());
                    zList.add(0, new ParamZone("0", "Select zone"));
                    ZoneAdapter adapter = new ZoneAdapter(context, R.layout.spinner_item, zList);
                    spMktZone.setAdapter(adapter);
                    spMktZone.post(new Runnable() {
                        @Override
                        public void run() {
                            spMktZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if ((position > 0) && (zList.size() > 1)) {
                                        zoneCd = zList.get(position).getZONE_CODE();
                                        if (zList.get(position).getDepots().size() > 0) {
                                            dList.clear();
                                            dList.addAll(zList.get(position).getDepots());
                                            dList.add(0, new ParamDepot("0", "Select depot"));
                                            DepotAdapter aDepot = new DepotAdapter(context, R.layout.spinner_item, dList);
                                            spMktDpo.setAdapter(aDepot);
                                            spMktDpo.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spMktDpo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            if ((position > 0) && (dList.size() > 1)) {
                                                                depotCd = dList.get(position).getDEPOT_CODE();
                                                                if (dList.get(position).getRegions().size() > 0) {
                                                                    rList.clear();
                                                                    rList.addAll(dList.get(position).getRegions());
                                                                    rList.add(0, new ParamRegion("0", "Select region"));

                                                                    RegionAdapter aRegion = new RegionAdapter(context, R.layout.spinner_item, rList);
                                                                    spMktRegion.setAdapter(aRegion);
                                                                    spMktRegion.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            spMktRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                @Override
                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                    if ((position > 0) && (rList.size() > 1)) {
                                                                                        regionCd = rList.get(position).getREGION_CODE();
                                                                                        if (rList.get(position).getAreas().size() > 0) {
                                                                                            aList.clear();
                                                                                            aList.addAll(rList.get(position).getAreas());
                                                                                            aList.add(0, new ParamArea("0", "Select area"));
                                                                                            AreaAdapter aArea = new AreaAdapter(context, R.layout.spinner_item, aList);
                                                                                            spMktArea.setAdapter(aArea);
                                                                                            spMktArea.post(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    spMktArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                        @Override
                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                            if ((position > 0) && (aList.size() > 1)) {
                                                                                                                areaCd = aList.get(position).getAREA_CODE();
                                                                                                                if (aList.get(position).getTerritories().size()>0){
                                                                                                                    tList.clear();
                                                                                                                    tList.addAll(aList.get(position).getTerritories());
                                                                                                                    tList.add(0, new ParamTerritory("0", "Select territory"));
                                                                                                                    TerritoryAdapter aTerritory = new TerritoryAdapter(context, R.layout.spinner_item, tList);
                                                                                                                    spMktTerritory.setAdapter(aTerritory);
                                                                                                                    spMktTerritory.post(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            spMktTerritory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                @Override
                                                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                    if ((position > 0) && (tList.size() > 1)) {
                                                                                                                                        territoryId = tList.get(position).getTERRITORY_CODE();
                                                                                                                                        if (tList.get(position).getMios().size()>0){
                                                                                                                                            mList.clear();
                                                                                                                                            mList.addAll(tList.get(position).getMios());
                                                                                                                                            mList.add(0, new ParamMio("0", "Select mio"));
                                                                                                                                            MioAdapter aMio = new MioAdapter(context, R.layout.spinner_item, mList);
                                                                                                                                            spMktMio.setAdapter(aMio);
                                                                                                                                            spMktMio.post(new Runnable() {
                                                                                                                                                @Override
                                                                                                                                                public void run() {
                                                                                                                                                    spMktMio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                            if ((position > 0) && (mList.size() > 1)) {
                                                                                                                                                                mioCd = mList.get(position).getEMP_ID();
                                                                                                                                                                mkList.clear();
                                                                                                                                                                if (mList.get(position).getMarkets().size()>0){
                                                                                                                                                                    mkList.addAll(mList.get(position).getMarkets());
                                                                                                                                                                    mkList.add(0,new ParamMarket("0","Select market"));
                                                                                                                                                                    mkList.add(mkList.size(),new ParamMarket("","Add new market"));
                                                                                                                                                                    MarketAdapter marketAdapter = new MarketAdapter(context,R.layout.spinner_item, mkList);
                                                                                                                                                                    spMktSet.setAdapter(marketAdapter);
                                                                                                                                                                    spMktSet.post(new Runnable() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void run() {
                                                                                                                                                                            spMktSet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                                                                                                                                    if (position>0 ){
                                                                                                                                                                                        if (mkList.get(position).getName().equalsIgnoreCase("Add new market")){
                                                                                                                                                                                            NewMarket market = new NewMarket();
                                                                                                                                                                                            Bundle ars = new Bundle();
                                                                                                                                                                                            ars.putString("TYPE_MARKET","marketAdd");
                                                                                                                                                                                            market.setArguments(ars);
                                                                                                                                                                                            getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                                                                                                                                                                            myCommunicator.setContentFragment(market, true);
                                                                                                                                                                                        }else {
                                                                                                                                                                                            mktId = mkList.get(position).getMarketId();
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                                                                    mktId = 0;
                                                                                                                                                                                }
                                                                                                                                                                            });
                                                                                                                                                                            if (mkList.size() == 2) {
                                                                                                                                                                                spMktSet.setSelection(1);
                                                                                                                                                                                spMktSet.setEnabled(false);
                                                                                                                                                                            }else{
                                                                                                                                                                                spMktSet.setEnabled(true);
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    });

                                                                                                                                                                }else {
                                                                                                                                                                    mkList.clear();
                                                                                                                                                                    spMktSet.setAdapter(null);
                                                                                                                                                                    spMktSet.setEnabled(false);
                                                                                                                                                                    mktId =0;
                                                                                                                                                                }
                                                                                                                                                            } else {

                                                                                                                                                                mkList.clear();
                                                                                                                                                                spMktSet.setAdapter(null);
                                                                                                                                                                spMktSet.setEnabled(false);

                                                                                                                                                                mioCd ="";
                                                                                                                                                                mktId =0;
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                        @Override
                                                                                                                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                    if (mList.size() == 2) {
                                                                                                                                                        spMktMio.setSelection(1);
                                                                                                                                                        spMktMio.setEnabled(false);
                                                                                                                                                    }else{
                                                                                                                                                        spMktMio.setEnabled(true);
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                        }
                                                                                                                                    } else {
                                                                                                                                        mkList.clear();
                                                                                                                                        spMktSet.setAdapter(null);
                                                                                                                                        spMktSet.setEnabled(false);

                                                                                                                                        mList.clear();
                                                                                                                                        spMktMio.setAdapter(null);
                                                                                                                                        spMktMio.setEnabled(false);

                                                                                                                                        territoryId = "";
                                                                                                                                        mioCd ="";
                                                                                                                                        mktId =0;
                                                                                                                                    }
                                                                                                                                }
                                                                                                                                @Override
                                                                                                                                public void onNothingSelected(AdapterView<?> parent) {
                                                                                                                                }
                                                                                                                            });
                                                                                                                            if (tList.size() == 2) {
                                                                                                                                spMktTerritory.setSelection(1);
                                                                                                                                spMktTerritory.setEnabled(false);
                                                                                                                            }else {
                                                                                                                                spMktTerritory.setEnabled(true);
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            } else {
                                                                                                                mkList.clear();
                                                                                                                spMktSet.setAdapter(null);
                                                                                                                spMktSet.setEnabled(false);

                                                                                                                mList.clear();
                                                                                                                spMktMio.setAdapter(null);
                                                                                                                spMktMio.setEnabled(false);

                                                                                                                tList.clear();
                                                                                                                spMktTerritory.setAdapter(null);
                                                                                                                spMktTerritory.setEnabled(false);

                                                                                                                areaCd = "";
                                                                                                                territoryId = "";
                                                                                                                mioCd ="";
                                                                                                                mktId =0;
                                                                                                            }
                                                                                                        }
                                                                                                        @Override
                                                                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                                                                        }
                                                                                                    });
                                                                                                    if (aList.size() == 2) {
                                                                                                        spMktArea.setSelection(1);
                                                                                                        spMktArea.setEnabled(false);
                                                                                                    }else {
                                                                                                        spMktArea.setEnabled(true);
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }

                                                                                    } else {

                                                                                        mkList.clear();
                                                                                        spMktSet.setAdapter(null);
                                                                                        spMktSet.setEnabled(false);

                                                                                        mList.clear();
                                                                                        spMktMio.setAdapter(null);
                                                                                        spMktMio.setEnabled(false);

                                                                                        tList.clear();
                                                                                        spMktTerritory.setAdapter(null);
                                                                                        spMktTerritory.setEnabled(false);

                                                                                        aList.clear();
                                                                                        spMktArea.setAdapter(null);
                                                                                        spMktArea.setEnabled(false);

                                                                                        regionCd = "";
                                                                                        areaCd = "";
                                                                                        territoryId = "";
                                                                                        mioCd ="";
                                                                                        mktId =0;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onNothingSelected(AdapterView<?> parent) {
                                                                                }
                                                                            });
                                                                            if (rList.size() == 2) {
                                                                                spMktRegion.setSelection(1);
                                                                                spMktRegion.setEnabled(false);
                                                                            }else {
                                                                                spMktRegion.setEnabled(true);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            } else {

                                                                mkList.clear();
                                                                spMktSet.setAdapter(null);
                                                                spMktSet.setEnabled(false);

                                                                mList.clear();
                                                                spMktMio.setAdapter(null);
                                                                spMktMio.setEnabled(false);

                                                                tList.clear();
                                                                spMktTerritory.setAdapter(null);
                                                                spMktTerritory.setEnabled(false);

                                                                aList.clear();
                                                                spMktArea.setAdapter(null);
                                                                spMktArea.setEnabled(false);

                                                                rList.clear();
                                                                spMktRegion.setAdapter(null);
                                                                spMktRegion.setEnabled(false);

                                                                depotCd= "";
                                                                regionCd = "";
                                                                areaCd = "";
                                                                territoryId = "";
                                                                mioCd ="";
                                                                mktId =0;
                                                            }
                                                        }
                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {
                                                        }
                                                    });
                                                    if (dList.size() == 2) {
                                                        spMktDpo.setSelection(1);
                                                        spMktDpo.setEnabled(false);
                                                    }else {
                                                        spMktDpo.setEnabled(true);
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        mkList.clear();
                                        spMktSet.setAdapter(null);
                                        spMktSet.setEnabled(false);

                                        mList.clear();
                                        spMktMio.setAdapter(null);
                                        spMktMio.setEnabled(false);

                                        tList.clear();
                                        spMktTerritory.setAdapter(null);
                                        spMktTerritory.setEnabled(false);

                                        aList.clear();
                                        spMktArea.setAdapter(null);
                                        spMktArea.setEnabled(false);

                                        rList.clear();
                                        spMktRegion.setAdapter(null);
                                        spMktRegion.setEnabled(false);

                                        dList.clear();
                                        spMktDpo.setAdapter(null);
                                        spMktDpo.setEnabled(false);

                                        zoneCd = "";
                                        depotCd= "";
                                        regionCd = "";
                                        areaCd = "";
                                        territoryId = "";
                                        mioCd ="";
                                        mktId =0;
                                    }
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                            if (zList.size() == 2) {
                                spMktZone.setSelection(1);
                                spMktZone.setEnabled(false);
                            }else {
                                spMktZone.setEnabled(true);
                            }
                        }
                    });
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



