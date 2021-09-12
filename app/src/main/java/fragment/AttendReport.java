package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import java.util.Calendar;

import adapter.AttendMothReport;
import helper.BaseFragment;
import helper.DividerItemDecoration;
import interfac.ApiService;
import model.LatLon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;
import static android.content.Context.MODE_PRIVATE;

public class AttendReport extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RecyclerView mRecyclerView;
    private RelativeLayout notFound;
    private LatLon latLon;
    private ArrayList<LatLon> list= new ArrayList<LatLon>();


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
        toolbar.setTitle("ATTENDANCE HISTORY");
        notFound = (RelativeLayout) getView().findViewById(R.id.rptNoFound);
        pb = (ProgressBar) getView().findViewById(R.id.pbRptMgt);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recRptMgt);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1,RecyclerView.VERTICAL,false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, ResourcesCompat.getDrawable(context.getResources(),R.drawable.divider,context.getTheme())));
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

        if(bundle.getParcelable("REPORT_ATTENDANCE")!=null){
            latLon = new LatLon();
            latLon = bundle.getParcelable("REPORT_ATTENDANCE");
            if (AppConstant.isOnline(context)){
                //Log.e("data","Year: "+latLon.getYear());
                //Log.e("data","Month: "+latLon.getMonthNumber());;
                getCheckInOutDetails(pref.getString(AppConstant.TOKEN,""),latLon.getYear(),latLon.getMonthNumber());
            }else {
                AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
            }
        }
    }



    private void getCheckInOutDetails(String header,int year,int month) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getCheckInOutDetails(header,year,month);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse!=null){
                    list.clear();
                    JsonArray history = serverResponse.get("History").getAsJsonArray();
                    TypeToken<ArrayList<LatLon>> token = new TypeToken<ArrayList<LatLon>>() {};
                    list = new Gson().fromJson(history, token.getType());

                    int workingDays =0;
                    int prsDays=0;
                    int absDays=0;

                    if (serverResponse.get("Summary").getAsJsonArray().get(0).getAsJsonObject()!=null){
                        workingDays = serverResponse.get("Summary").getAsJsonArray().get(0).getAsJsonObject().get("workingDays").getAsInt();
                        prsDays = serverResponse.get("Summary").getAsJsonArray().get(0).getAsJsonObject().get("presentDays").getAsInt();
                        absDays = serverResponse.get("Summary").getAsJsonArray().get(0).getAsJsonObject().get("absentDays").getAsInt();
                    }
                    AttendMothReport attendMothReport = new AttendMothReport(context, list,workingDays,prsDays,absDays,latLon.getMonthName(), new AttendMothReport.MyAdapterListener() {
                        @Override
                        public void itemRowClick(View v, int position) {

                        }
                    });
                    mRecyclerView.setAdapter(attendMothReport);
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



