package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.opl.one.oplsales.R;
import helper.BaseFragment;
import static android.content.Context.MODE_PRIVATE;


public class ReportMain extends BaseFragment {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    private int fragmentPos=0;
    private String tabTitles[] = new String[] { "REPORT IN LIST VIEW", "REPORT IN MAP VIEW"};
    protected MyAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        intUit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.target_main, container, false);
    }


    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitle("TARGET SET");

        pb = (ProgressBar) getView().findViewById(R.id.pbTarget);
        tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation,R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getInt("pos") ==0) {
            fragmentPos = 0;
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // pb.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(int position) {
                //setAdapter(position);
                Log.e("onPageSelected",""+position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //pb.setVisibility(View.VISIBLE);
            }
        });
        adapter=new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        setPageItem(fragmentPos);
    }



    void setPageItem(int i)
    {
        viewPager.setCurrentItem(i);
        tabLayout.setupWithViewPager(viewPager);
    }

    private class MyAdapter extends FragmentPagerAdapter {

        private MyAdapter(FragmentManager fm) {
            super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position)
        {
            switch (position){

                case 0 :
                    ReportMio reportInList= new ReportMio();
                    Bundle bundle = new Bundle();
                    reportInList.setArguments(bundle);
                    return reportInList;
                case 1 :
                    ReportInMap reportInMap= new ReportInMap();
                    Bundle args = new Bundle();
                    reportInMap.setArguments(args);
                    return reportInMap;
            }
            return null;
        }
        @Override
        public int getCount() {
            return tabTitles.length;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
           // notifyDataSetChanged();
            return fragmentPos;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}



