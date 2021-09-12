package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.LaunchActivity;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;
import java.util.ArrayList;

import adapter.DoctorAdapter;
import adapter.DrListAdapter;
import db.DBHandler;
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

public class DoctorMgt extends BaseFragment {
    private Context context;
    private Bundle bundle;
    private DBHandler db;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RecyclerView mRecyclerView;
    private RelativeLayout doctorNoFound;
    private ArrayList<TinyUser> list= new ArrayList<TinyUser>();
    private TextView btnNewDoctor;
    private DoctorAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_mgt, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        db = new DBHandler(context);
        bundle = this.getArguments();
        intUit();
    }

    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.search_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("DOCTOR VISIT");
        Typeface ceRaProBold = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProBold.otf");
        Typeface ceRaProRegular = Typeface.createFromAsset(context.getAssets(), "fonts/CeraProRegular.otf");

        doctorNoFound = (RelativeLayout) getView().findViewById(R.id.doctorNoFound);
        btnNewDoctor = (TextView) getView().findViewById(R.id.btnNewDoctor);

        pb = (ProgressBar) getView().findViewById(R.id.pbDMgt);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recDoctor);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1,RecyclerView.VERTICAL,false);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(context,getResources().getDrawable(R.drawable.recycler_horizontal_divider)));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context,context.getResources().getDrawable(R.drawable.divider)));
        mRecyclerView.setLayoutManager(gridVertical);
        doctorNoFound.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        btnNewDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewDoctor doctor = new NewDoctor();
                Bundle ars = new Bundle();
                ars.putString("EDIT_TYPE","drAdd");
                doctor.setArguments(ars);
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(doctor, true);
                //getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                //myCommunicator.setContentFragment(new NewDoctor(),true);
            }
        });

        MenuItem myActionMenuItem =toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        theTextArea.setTextColor(getResources().getColor(android.R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // menu.findItem(R.id.action_search).collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // search goes here !!
                if (adapter!=null)
                    adapter.getFilter().filter(newText);
                return false;
            }
        });


       if (AppConstant.isOnline(context)){
           getDoctorList(pref.getString(AppConstant.TOKEN,""));
       }else {
           AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
       }
    }


    private void getDoctorList(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getDrList(header);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("MyData", new GsonBuilder().serializeNulls().create().toJson(serverResponse));
                if (response.code() ==200 && serverResponse!=null){
                    boolean status = serverResponse.get("status").getAsBoolean();
                    String message = serverResponse.get("message").getAsString().trim();
                    list.clear();
                    if (status && serverResponse.has("doctors") && serverResponse.get("doctors").getAsJsonArray().size()>0){
                        doctorNoFound.setVisibility(View.GONE);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("doctors").getAsJsonArray(), token.getType());
                        adapter = new DoctorAdapter(context, "show edit",list, new DoctorAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                //Toast.makeText(context,"Dr details",Toast.LENGTH_SHORT).show();
                               /* DoctorVisited visited= new DoctorVisited();
                                Bundle ars = new Bundle();
                                ars.putString("dId",list.get(position).getDoctor_code().trim());
                                visited.setArguments(ars);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.setContentFragment(visited,true);*/
                            }
                            @Override
                            public void editClick(View v, int position) {
                                NewDoctor doctor = new NewDoctor();
                                Bundle ars = new Bundle();
                                ars.putString("EDIT_TYPE","dr");
                                ars.putParcelable("SEND_OBJECT",list.get(position));
                                doctor.setArguments(ars);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.setContentFragment(doctor, true);
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else if (message.equals("Invalid Token.")){
                        AppConstant.logOut(MainActivity.getInstance());
                    }else {
                        doctorNoFound.setVisibility(View.VISIBLE);
                    }
                }else {
                    doctorNoFound.setVisibility(View.VISIBLE);
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



