package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.MainActivity;
import com.opl.one.oplsales.R;
import java.util.ArrayList;
import adapter.ReportAdapter;
import db.DBHandler;
import helper.BaseFragment;
import interfac.ApiService;
import model.TinyUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;


public class DoctorReport extends BaseFragment{
    private Context context;
    private DBHandler db;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RelativeLayout reportNoFound;
    private RecyclerView mRecyclerView;
    private ArrayList<TinyUser> list= new ArrayList<TinyUser>();
    private TextView btnNewChemist;
    private ReportAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_report, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        db = new DBHandler(context);
        intUit();
    }

    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.search_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("CHEMIST VISIT");
      /*  DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = (displayMetrics.widthPixels) / 4;*/
        Typeface ceRaProBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CeraProBold.otf");
        Typeface ceRaProRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CeraProRegular.otf");
        pb = (ProgressBar) getView().findViewById(R.id.pbDReport);
        reportNoFound = (RelativeLayout) getView().findViewById(R.id.reportNoFound);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recReport);
        btnNewChemist = (TextView) getView().findViewById(R.id.btnNewChemist);
        GridLayoutManager gridVertical = new GridLayoutManager(context,1,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridVertical);
        reportNoFound.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation, R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        btnNewChemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDoctor doctor = new NewDoctor();
                Bundle ars = new Bundle();
                ars.putString("EDIT_TYPE","chemistAdd");
                doctor.setArguments(ars);
                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                myCommunicator.setContentFragment(doctor, true);
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
            getChemistList(pref.getString(AppConstant.TOKEN,""));
        }else {
            AppConstant.openDialog(context,"No Internet",context.getResources().getString(R.string.internet_error));
        }


    }

    private void getChemistList(String header) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);

        Call<JsonObject> call = api.getChemistList(header);
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
                    if (status && serverResponse.has("chemists") && serverResponse.get("chemists").getAsJsonArray().size()>0){
                        reportNoFound.setVisibility(View.GONE);
                        TypeToken<ArrayList<TinyUser>> token = new TypeToken<ArrayList<TinyUser>>() {};
                        list = new Gson().fromJson(serverResponse.get("chemists").getAsJsonArray(), token.getType());
                        adapter= new ReportAdapter(context,"show edit", list, new ReportAdapter.MyAdapterListener() {
                            @Override
                            public void rowClick(View v, int position) {
                                Toast.makeText(context,"Show details of chemist",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void rowEdit(View v, int position) {
                                NewDoctor doctor = new NewDoctor();
                                Bundle ars = new Bundle();
                                ars.putString("EDIT_TYPE","chemist");
                                ars.putParcelable("SEND_OBJECT",list.get(position));
                                doctor.setArguments(ars);
                                getActivity().overridePendingTransition(R.anim.enter_animation, R.anim.exit_animation);
                                myCommunicator.addContentFragment(doctor, true);
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else if(message.equals("Invalid Token.")){
                        AppConstant.logOut(MainActivity.getInstance());
                    } else {
                        reportNoFound.setVisibility(View.VISIBLE);
                    }
                }else {
                    reportNoFound.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pb.setVisibility(View.GONE);
            }
        });
    }


}



