package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import adapter.ReportMgtAdapter;
import dialog.ReportDetails;
import helper.BaseFragment;
import helper.DividerItemDecoration;
import interfac.ApiService;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;
import static android.content.Context.MODE_PRIVATE;

public class ReportMgt extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RecyclerView mRecyclerView;
    private RelativeLayout notFound;
    private ArrayList<TinyUser> list= new ArrayList<TinyUser>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_mgt, container, false);
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

        notFound = (RelativeLayout) getView().findViewById(R.id.rptNoFound);
        pb = (ProgressBar) getView().findViewById(R.id.pbRptMgt);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recRptMgt);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1,RecyclerView.VERTICAL,false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context,getResources().getDrawable(R.drawable.divider)));

        mRecyclerView.setLayoutManager(gridVertical);
        notFound.setVisibility(View.GONE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        JsonParser parser =  new JsonParser();
        if (bundle!=null && bundle.getString(AppConstant.TYPE_DR_CHEMIST).equals("typeDoctorReport")){
            toolbar.setTitle("DOCTOR VISIT REPORT");
            JsonObject object = (JsonObject) parser.parse(bundle.getString("SET_PARAMS"));
            getMIODoctorVisitReport(pref.getString(AppConstant.TOKEN,""),object.get("ZoneCode").getAsString(),object.get("DepotCode").getAsString(),object.get("RegionCode").getAsString(), object.get("AreaCode").getAsString(),object.get("TerritoryCode").getAsString(),object.get("EmpCode").getAsString(),object.get("FromDate").getAsString(),object.get("ToDate").getAsString());
//            Log.e("MyData","After zoneId: "+object.get("ZoneCode").getAsString());
//            Log.e("MyData","After depotId: "+object.get("DepotCode").getAsString());
//            Log.e("MyData","After regionId: "+object.get("RegionCode").getAsString());
//            Log.e("MyData","After areaId: "+object.get("AreaCode").getAsString());
//            Log.e("MyData","After territoryId: "+object.get("TerritoryCode").getAsString());
//            Log.e("MyData","After mioId: "+ object.get("EmpCode").getAsString());
//            Log.e("MyData","After dateFrom: "+ object.get("FromDate").getAsString());
//            Log.e("MyData","After dateTo: "+ object.get("ToDate").getAsString());
//            Log.e("MyData",pref.getString(AppConstant.TOKEN,""));
        }else if(bundle!=null && bundle.getString(AppConstant.TYPE_DR_CHEMIST).equals("typeDr")){
            toolbar.setTitle("DOCTOR");
            JsonObject object = (JsonObject) parser.parse(bundle.getString("SET_PARAMS"));

            String zone="",depot="",region="",area="",territory="",market="",fDate="",tDate="";
            int dId=0;

            if(!TextUtils.isEmpty(object.get("ZoneCode").getAsString())){
                zone = object.get("ZoneCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("DepotCode").getAsString())){
                depot = object.get("DepotCode").getAsString();
            }
            if (!TextUtils.isEmpty(object.get("RegionCode").getAsString())){
                region = object.get("RegionCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("AreaCode").getAsString())){
                area = object.get("AreaCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("TerritoryCode").getAsString())){
                territory = object.get("TerritoryCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("MarketCode").getAsString())){
                market = object.get("MarketCode").getAsString();
            }
            if (object.get("Id").getAsInt()>0){
                dId = object.get("Id").getAsInt();
            }

            if (!TextUtils.isEmpty(object.get("FromDate").getAsString())){
                fDate = object.get("FromDate").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("ToDate").getAsString())){
                tDate = object.get("ToDate").getAsString();
            }
            getDoctorWiseVisitReport(pref.getString(AppConstant.TOKEN,""),zone,depot,region,area,territory,market,dId,fDate,tDate);
            /*Log.e("MyData","ZoneCode: "+object.get("ZoneCode").getAsString());
            Log.e("MyData","DepotCode: "+object.get("DepotCode").getAsString());
            Log.e("MyData","RegionCode: "+object.get("RegionCode").getAsString());
            Log.e("MyData","AreaCode: "+object.get("AreaCode").getAsString());
            Log.e("MyData","TerritoryCode: "+object.get("TerritoryCode").getAsString());
            Log.e("MyData","MarketCode: "+object.get("MarketCode").getAsString());
            Log.e("MyData","Id: "+object.get("Id").getAsInt());
            Log.e("MyData","FromDate: "+object.get("FromDate").getAsString());
            Log.e("MyData","ToDate: "+object.get("ToDate").getAsString());
            Log.e("MyData","time: "+object.get("time").getAsString());*/

        }else if(bundle!=null && bundle.getString(AppConstant.TYPE_DR_CHEMIST).equals("typeChemist")){
            toolbar.setTitle("CHEMIST");
            JsonObject object = (JsonObject) parser.parse(bundle.getString("SET_PARAMS"));

            String zone="",depot="",region="",area="",territory="",market="",fDate="",tDate="";
            int cId=0;

            if(!TextUtils.isEmpty(object.get("ZoneCode").getAsString())){
                zone = object.get("ZoneCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("DepotCode").getAsString())){
                depot = object.get("DepotCode").getAsString();
            }
            if (!TextUtils.isEmpty(object.get("RegionCode").getAsString())){
                region = object.get("RegionCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("AreaCode").getAsString())){
                area = object.get("AreaCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("TerritoryCode").getAsString())){
                territory = object.get("TerritoryCode").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("MarketCode").getAsString())){
                market = object.get("MarketCode").getAsString();
            }
            if (object.get("Id").getAsInt()>0){
                cId = object.get("Id").getAsInt();
            }

            if (!TextUtils.isEmpty(object.get("FromDate").getAsString())){
                fDate = object.get("FromDate").getAsString();
            }

            if (!TextUtils.isEmpty(object.get("ToDate").getAsString())){
                tDate = object.get("ToDate").getAsString();
            }
            getChemistWiseVisitReport(pref.getString(AppConstant.TOKEN,""),zone,depot,region,area,territory,market,cId,fDate,tDate);
        }else if(bundle!=null && bundle.getString(AppConstant.TYPE_DR_CHEMIST).equals("typeChemistReport")){
            toolbar.setTitle("CHEMIST VISIT REPORT");
            JsonObject object = (JsonObject) parser.parse(bundle.getString("SET_PARAMS"));
            getMIOChemistVisitReport(pref.getString(AppConstant.TOKEN,""),object.get("ZoneCode").getAsString(),object.get("DepotCode").getAsString(),object.get("RegionCode").getAsString(),
                    object.get("AreaCode").getAsString(),object.get("TerritoryCode").getAsString(),object.get("EmpCode").getAsString(),object.get("FromDate").getAsString(),object.get("ToDate").getAsString());

        }else if(bundle!=null && bundle.getString(AppConstant.TYPE_DR_CHEMIST).equals("typeEmployeeReport")){
            toolbar.setTitle("EMPLOYEE VISIT REPORT");
            JsonObject object = (JsonObject) parser.parse(bundle.getString("SET_PARAMS"));
            getEmpVisitReport(pref.getString(AppConstant.TOKEN,""),object.get("ZoneCode").getAsString(),object.get("DepotCode").getAsString(),object.get("RegionCode").getAsString(), object.get("AreaCode").getAsString(),object.get("TerritoryCode").getAsString(),object.get("EmpCode").getAsString(),object.get("FromDate").getAsString(),object.get("ToDate").getAsString());
        }


    }



    private void getMIODoctorVisitReport(String header,String zone,String depot,String region,String area,String territory,String mio,String fDate,String tDate) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getMIODoctorVisitReport(header,zone,depot,region,area,territory,mio,fDate,tDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ){

                    if (serverResponse.get("data").getAsJsonArray().size()>0){
                        notFound.setVisibility(View.GONE);
                        list.clear();
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("data").getAsJsonArray(), token.getType());
                        ReportMgtAdapter adapter = new ReportMgtAdapter(context,bundle.getString(AppConstant.TYPE_DR_CHEMIST), list, new ReportMgtAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                Bundle args = new Bundle();
                                ReportDetails details = new ReportDetails();
                                args.putString(AppConstant.TYPE_DR_CHEMIST,"typeDoctorReport");
                                args.putParcelable("OBJECT",list.get(position-1));
                                details.setArguments(args);
                                if (getFragmentManager() != null) {
                                    details.show(getFragmentManager(), "TAG");
                                }
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        notFound.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void getMIOChemistVisitReport(String header,String zone,String depot,String region,String area,String territory,String mio,String fDate,String tDate) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getMIOChemistVisitReport(header,zone,depot,region,area,territory,mio,fDate,tDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ){

                    if (serverResponse.get("data").getAsJsonArray().size()>0){
                        notFound.setVisibility(View.GONE);
                        list.clear();
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("data").getAsJsonArray(), token.getType());
                        ReportMgtAdapter adapter = new ReportMgtAdapter(context,bundle.getString(AppConstant.TYPE_DR_CHEMIST), list, new ReportMgtAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                Bundle args = new Bundle();
                                ReportDetails details = new ReportDetails();
                                args.putString(AppConstant.TYPE_DR_CHEMIST,"typeChemistReport");
                                args.putParcelable("OBJECT",list.get(position-1));
                                details.setArguments(args);
                                if (getFragmentManager() != null) {
                                    details.show(getFragmentManager(), "TAG");
                                }
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        notFound.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void getDoctorWiseVisitReport(String header,String zone,String depot,String region,String area,String territory,String market,int dId,String fDate,String tDate) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getDoctorWiseVisitReport(header,zone,depot,region,area,territory,market,dId,fDate,tDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ){

                    if (serverResponse.get("data").getAsJsonArray().size()>0){
                        notFound.setVisibility(View.GONE);
                        list.clear();
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("data").getAsJsonArray(), token.getType());
                        ReportMgtAdapter adapter = new ReportMgtAdapter(context,bundle.getString(AppConstant.TYPE_DR_CHEMIST), list, new ReportMgtAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                Bundle args = new Bundle();
                                ReportDetails details = new ReportDetails();
                                args.putString(AppConstant.TYPE_DR_CHEMIST,"typeDr");
                                args.putParcelable("OBJECT",list.get(position-1));
                                details.setArguments(args);
                                if (getFragmentManager() != null) {
                                    details.show(getFragmentManager(), "TAG");
                                }
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        notFound.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void getChemistWiseVisitReport(String header,String zone,String depot,String region,String area,String territory,String market,int dId,String fDate,String tDate) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getChemistWiseVisitReport(header,zone,depot,region,area,territory,market,dId,fDate,tDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ){

                    if (serverResponse.get("data").getAsJsonArray().size()>0){
                        notFound.setVisibility(View.GONE);
                        list.clear();
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("data").getAsJsonArray(), token.getType());
                        ReportMgtAdapter adapter = new ReportMgtAdapter(context,bundle.getString(AppConstant.TYPE_DR_CHEMIST), list, new ReportMgtAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                Bundle args = new Bundle();
                                ReportDetails details = new ReportDetails();
                                args.putString(AppConstant.TYPE_DR_CHEMIST,"typeChemist");
                                args.putParcelable("OBJECT",list.get(position-1));
                                details.setArguments(args);
                                if (getFragmentManager() != null) {
                                    details.show(getFragmentManager(), "TAG");
                                }
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        notFound.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void getEmpVisitReport(String header,String zone,String depot,String region,String area,String territory,String mio,String fDate,String tDate) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getEmpVisitReport(header,zone,depot,region,area,territory,mio,fDate,tDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ){
                    if (serverResponse.get("data").getAsJsonArray().size()>0){
                        notFound.setVisibility(View.GONE);
                        list.clear();
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("data").getAsJsonArray(), token.getType());
                        ReportMgtAdapter adapter = new ReportMgtAdapter(context,bundle.getString(AppConstant.TYPE_DR_CHEMIST), list, new ReportMgtAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                Bundle args = new Bundle();
                                ReportDetails details = new ReportDetails();
                                args.putString(AppConstant.TYPE_DR_CHEMIST,"typeEmployeeReport");
                                args.putParcelable("OBJECT",list.get(position-1));
                                details.setArguments(args);
                                if (getFragmentManager() != null) {
                                    details.show(getFragmentManager(), "TAG");
                                }
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        notFound.setVisibility(View.VISIBLE);
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



