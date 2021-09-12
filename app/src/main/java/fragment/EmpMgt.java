package fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.R;
import java.util.ArrayList;

import adapter.EmpAdapter;
import adapter.RosterAdapter;
import dialog.TimePickerFragment;
import helper.BaseFragment;
import helper.DMYPDialog;
import helper.DividerItemDecoration;
import interfac.ApiService;
import model.EmpData;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;

public class EmpMgt extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RecyclerView mRecyclerView;
    private RelativeLayout empNoFound;
    private Spinner spEOrM;
    private TextView datePick,timePicker,nameText,areaCode;
    private ArrayList<TinyUser> list= new ArrayList<TinyUser>();
    private ArrayList<EmpData> mList= new ArrayList<EmpData>();
    private ArrayList<EmpData> ints = new ArrayList<EmpData>();
    private String strRoster="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emp_mgt, container, false);
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
        toolbar.setTitle("Visit schedule");

        Typeface ceRaProBold = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProBold.otf");
        Typeface ceRaProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProRegular.otf");

        empNoFound = (RelativeLayout) getView().findViewById(R.id.empNoFound);
        spEOrM = (Spinner) getView().findViewById(R.id.spEOrM);
        pb = (ProgressBar) getView().findViewById(R.id.pbEmpMgt);
        datePick = (TextView) getView().findViewById(R.id.datePick);
        timePicker = (TextView) getView().findViewById(R.id.timePicker);
        nameText = (TextView) getView().findViewById(R.id.nameText);
        areaCode = (TextView) getView().findViewById(R.id.areaCode);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rcvEmp);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1,RecyclerView.VERTICAL,false);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(context,getResources().getDrawable(R.drawable.recycler_horizontal_divider)));

        mRecyclerView.addItemDecoration(new DividerItemDecoration(context,context.getResources().getDrawable(R.drawable.divider)));

        mRecyclerView.setLayoutManager(gridVertical);
        empNoFound.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(pref.getString("EMP_NAME",""))){
            nameText.setText(pref.getString("EMP_NAME",""));
        }
        if (!TextUtils.isEmpty(pref.getString("AREA_CODE",""))){
            areaCode.setText(Html.fromHtml("<font color='#000000'>Area Code :</font>"+pref.getString("AREA_CODE","")));
        }else {
            areaCode.setText(Html.fromHtml("<font color='#000000'>Area Code :N/A</font>"));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        list.clear();
        list.add(0,new TinyUser(0,"Select roster"));
        list.add(1,new TinyUser(1,"Morning"));
        list.add(2,new TinyUser(2,"Evening"));
        RosterAdapter myAdapter = new RosterAdapter(context,R.layout.spinner_item,list);
        spEOrM.setAdapter(myAdapter);

        spEOrM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if ((position > 0) && (list.size()>1)) {
                    strRoster = list.get(position).getName();
                    Toast.makeText(context,strRoster,Toast.LENGTH_SHORT).show();
                }else {
                    strRoster ="";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //current=Calendar.getInstance();
        //df = new SimpleDateFormat("yyyy-MM-dd");
       // datePick.setText(df.format(current.getTime()));
        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DMYPDialog pickerDialog = new DMYPDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String dd, mm;
                        if (day > 9 && day <= 31) {
                            dd = String.format("%01d", day);
                        } else {
                            dd = String.format("%02d", day);
                        }
                        int m = month + 1;
                        if (m > 9 && m <= 12) {
                            mm = String.format("%01d", m);
                        } else {
                            mm = String.format("%02d", m);
                        }
                        datePick.setText(year + "-" + mm + "-" + dd);
                    }
                });
                pickerDialog.show(getActivity().getSupportFragmentManager(), "DMYPDialog");
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new TimePickerFragment();
                dFragment.show(getFragmentManager(),"Time Picker");
            }
        });



       if (AppConstant.isOnline(context)){
           //getAllEmpList("bearer "+pref.getString("TOKEN",""),"113");
       }else {
           AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
       }
    }

    private void getAllEmpList(String header,String id) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonArray> call = api.getAllEmpList(header,id);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull retrofit2.Response<JsonArray> response) {
                pb.setVisibility(View.GONE);
                JsonArray jsonArray = response.body();
                Log.e("response", new GsonBuilder().serializeNulls().create().toJson(jsonArray));
                mList.clear();
                ints.clear();
                assert jsonArray != null;
                if (response.code() == 200 && jsonArray.size()>0 ) {
                    empNoFound.setVisibility(View.GONE);
                    TypeToken<ArrayList<EmpData>> token = new TypeToken<ArrayList<EmpData>>() {};
                    mList = new Gson().fromJson(jsonArray.getAsJsonArray(), token.getType());
                    EmpAdapter adapter = new EmpAdapter(context, mList, new EmpAdapter.MyAdapterListener() {
                        @Override
                        public void itemCheck(View v, int position) {
                            EmpData item = mList.get(position);
                            if (item.isSelected()) {
                                item.setSelected(false);
                                ints.remove(item);
                            } else{
                                item.setSelected(true);
                                ints.add(item);
                            }

                        }
                    });
                    mRecyclerView.setAdapter(adapter);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    empNoFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}



