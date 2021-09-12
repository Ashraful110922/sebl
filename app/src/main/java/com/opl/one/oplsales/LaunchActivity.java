package com.opl.one.oplsales;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import fragment.Launch;
import interfac.CommunicatorFragmentInterface;

public class LaunchActivity extends AppCompatActivity implements CommunicatorFragmentInterface{
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        context = this;
        setContentFragment(new Launch(),false);
    }



    @Override
    public void setContentFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null) {
            return;
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment cf = fm.findFragmentById(R.id.flContent);

        if (cf != null && fragment.getClass().isAssignableFrom(cf.getClass())) {
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flContent, fragment, fragment.getClass().getName());

        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    @Override
    public void addContentFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null) {
            return;
        }
        final FragmentManager fm = getSupportFragmentManager();
        Fragment cf = fm.findFragmentById(R.id.flContent);

        if (cf != null && fragment.getClass().isAssignableFrom(cf.getClass())) {
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.flContent, fragment, fragment.getClass().getName());
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getName());
        }
        ft.commit();
        fm.executePendingTransactions();
    }

    @Override
    public void removeAllFragment() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fm.popBackStackImmediate();
        }
    }
}
