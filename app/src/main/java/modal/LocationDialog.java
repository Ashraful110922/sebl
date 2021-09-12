package modal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.opl.one.oplsales.R;


public class LocationDialog extends DialogFragment implements OnMapReadyCallback {
    public static final String TAG = "LocationDialog";
    private Context context;
    private Bundle bundle;
    private ProgressBar pb;
    private GoogleMap mMap;
    private TextView btnLoc;
    private OnChooseReasonListener callback;
    private static View view;
    private double latitude = 0.00,longitude = 0.00;
    private LatLng mLatLng =new LatLng(latitude, longitude);


    public static LocationDialog newInstance() {
        return new LocationDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.dialog_lc, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }
        return view;
    }


    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = this.getActivity();
        bundle = this.getArguments();

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapContenView, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);
        callback = (OnChooseReasonListener)  getParentFragment();
      /*  mLatLng = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(mLatLng).title("Select position"));
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
        mMap.animateCamera(location);*/
        intUi();
    }

    private void intUi() {
        pb = (ProgressBar) getView().findViewById(R.id.pbLocation);
        btnLoc = (TextView) getView().findViewById(R.id.btnLoc);
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback!=null){
                    assert getActivity() != null;
                    callback.onChooseReason(mLatLng);
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

  @Override
    public void onDetach() {
        super.onDetach();
      callback = null;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
        mMap.animateCamera(location);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
                if (bundle.getDouble("LATITUDE")>0.00 && bundle.getDouble("LONGITUDE")>0.00){
                    mLatLng = new LatLng(bundle.getDouble("LATITUDE"),bundle.getDouble("LONGITUDE"));
                    mMap.addMarker(new MarkerOptions().position(mLatLng).title("Select position"));
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
                    mMap.animateCamera(location);
                }else {
                    if(locationResult!=null && locationResult.getLocations().size()>0){
                        int lastLocationIndex = locationResult.getLocations().size()-1;
                        latitude = locationResult.getLocations().get(lastLocationIndex).getLatitude();
                        longitude = locationResult.getLocations().get(lastLocationIndex).getLongitude();
                        mLatLng = new LatLng(latitude,longitude);
                        mMap.addMarker(new MarkerOptions().position(mLatLng).title("Select position"));
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
                        mMap.animateCamera(location);
                    }
                }
            }
        }, Looper.getMainLooper());

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mLatLng =latLng;
                mMap.addMarker(new MarkerOptions().position(mLatLng).title("Select position"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
                mMap.animateCamera(location);
            }
        });
    }

    public interface OnChooseReasonListener {
        void onChooseReason(LatLng latLng);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
}
