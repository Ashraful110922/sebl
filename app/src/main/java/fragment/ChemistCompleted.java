package fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import adapter.DrExecuteAdapter;
import adapter.RosterAdapter;
import helper.BaseFragment;
import helper.DMYPDialog;
import interfac.ApiService;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;

public class ChemistCompleted extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private TextView btnToDate;
    private ImageView btnDcSearch;
    private Calendar current;
    private SimpleDateFormat df;
    private RecyclerView mRecyclerView;
    private List<TinyUser> list = new ArrayList<TinyUser>();
    private Spinner spDCExecute;
    private ArrayList<TinyUser> rsList= new ArrayList<TinyUser>();
    private int intRosterId=0;
    private RelativeLayout drNotFound;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_common, container, false);
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
        pb = (ProgressBar) getView().findViewById(R.id.pbDCReport);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recDC);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridVertical);
        btnToDate = (TextView) getView().findViewById(R.id.btnToDate);
        spDCExecute = (Spinner) getView().findViewById(R.id.spDCExecute);
        btnDcSearch = (ImageView) getView().findViewById(R.id.btnDcSearch);
        drNotFound = (RelativeLayout) getView().findViewById(R.id.drNotFound);
        current=Calendar.getInstance();
        //df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        btnToDate.setText(df.format(current.getTime()));

        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DMYPDialog pickerDialog = new DMYPDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String dd, mm;
                        if (day > 9 && day <= 31) {
                            dd = String.format(Locale.getDefault(),"%01d", day);
                        } else {
                            dd = String.format(Locale.getDefault(),"%02d", day);
                        }
                        int m = month + 1;
                        if (m > 9 && m <= 12) {
                            mm = String.format(Locale.getDefault(),"%01d", m);
                        } else {
                            mm = String.format(Locale.getDefault(),"%02d", m);
                        }
                       // btnToDate.setText(year + "-" + mm + "-" + dd);
                        btnToDate.setText( mm+ "/" + dd + "/" + year);
                    }
                });
                pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });
        drNotFound.setVisibility(View.GONE);
        rsList.clear();
        rsList.add(0,new TinyUser(0,"Select roster"));
        rsList.add(1,new TinyUser(1,"Morning"));
        rsList.add(2,new TinyUser(2,"Evening"));
        RosterAdapter rosterAdapter = new RosterAdapter(context,R.layout.spinner_item,rsList);
        spDCExecute.setAdapter(rosterAdapter);
        spDCExecute.post(new Runnable() {
            @Override
            public void run() {
                spDCExecute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ((position > 0) && (rsList.size()>1)) {
                            intRosterId = rsList.get(position).getId();
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

        btnDcSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstant.isOnline(context)){
                    String dd = btnToDate.getText().toString().trim();
                    getChListAfterSetPlan(pref.getString(AppConstant.TOKEN,""),dd,intRosterId);
                    Log.e("Response",dd);
                    Log.e("Response",""+intRosterId);
                }else {
                    AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
                }
            }
        });

        if (AppConstant.isOnline(context)){
            String dd = btnToDate.getText().toString().trim();
            getChListAfterSetPlan(pref.getString(AppConstant.TOKEN,""),dd,intRosterId);
        }else {
            AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
        }

       // Log.e("Response","Token: "+pref.getString(AppConstant.TOKEN,""));

    }

    private void getChListAfterSetPlan(String header,String date,int id) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getChListAfterSetPlan(header,date,id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() ==200 && serverResponse!=null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    list.clear();
                    if (status && serverResponse.has("chemists") && serverResponse.get("chemists").getAsJsonArray().size()>0){
                        drNotFound.setVisibility(View.GONE);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("chemists").getAsJsonArray(), token.getType());
                        DrExecuteAdapter drExecuteAdapter = new DrExecuteAdapter(context, list, new DrExecuteAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                DoctorVisited visited= new DoctorVisited();
                                Bundle ars = new Bundle();
                                ars.putString("dId",String.valueOf(list.get(position).getId()));
                                ars.putString("EXECUTE_TYPE","chemist");
                                visited.setArguments(ars);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.setContentFragment(visited,true);
                            }
                        });
                        mRecyclerView.setAdapter(drExecuteAdapter);
                        drExecuteAdapter.notifyDataSetChanged();
                    }else {
                        drNotFound.setVisibility(View.VISIBLE);
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



