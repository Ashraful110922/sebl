package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opl.one.oplsales.R;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import directionHelper.DirectionsJSONParser;
import helper.BaseFragment;
import interfac.ApiService;
import model.ParamMio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;


public class ReportInMap extends BaseFragment implements OnMapReadyCallback {
    private Context context;
    private Bundle bundle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressBar pb;
    private RelativeLayout notFound;
    private GoogleMap mMap;
    private ArrayList<ParamMio> cList= new ArrayList<ParamMio>();
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private PolylineOptions lineOptions = new PolylineOptions();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        bundle =this.getArguments();

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.viewMap, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);


        intUit();
    }

    private void intUit() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP);

        pb = (ProgressBar) getView().findViewById(R.id.pbMapReport);
        notFound = (RelativeLayout) getView().findViewById(R.id.mapReportNoFound);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().overridePendingTransition(R.anim.exit_animation,R.anim.enter_animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        if (bundle!=null){
            if (bundle.get(AppConstant.TYPE_REPORT).equals("CurrentLocation")){
                toolbar.setTitle("CURRENT LOCATION");
            }else if(bundle.get(AppConstant.TYPE_REPORT).equals("RoadLocation")){
                toolbar.setTitle("ROAD PATH");
            }
        }
    }

    private void getMIOWiseCurrentTrackingReport(String header,String zone,String depot,String region,String area,String territory,String eId,GoogleMap googleMap) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getMIOWiseCurrentTrackingReport(header,zone,depot,region,area,territory,eId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ){
                    if (serverResponse.get("data").getAsJsonArray().size()>0){
                        notFound.setVisibility(View.GONE);
                        cList.clear();
                        TypeToken<ArrayList<ParamMio>> token = new TypeToken<ArrayList<ParamMio>>() {};
                        cList = new Gson().fromJson(serverResponse.get("data").getAsJsonArray(), token.getType());
                        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                        for (ParamMio mio : cList) {
                            LatLng marker = new LatLng(Double.parseDouble(mio.getLongitude()),Double.parseDouble(mio.getLatitude()));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(marker)
                                    .title("Exe.Address: "+mio.getLLAddress())
                                    .snippet("Name: "+mio.getMIOName() +"Date: "+convertDateTime(mio.getDateTime()))
                                    .icon(icon));

                          /*  lineOptions.add(marker);
                            lineOptions.width(12);
                            lineOptions.color(Color.RED);*/
                            builder.include(marker);
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 20; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.moveCamera(cu);
                        googleMap.animateCamera(cu);
                        //googleMap.addPolyline(lineOptions);
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


    private void getMIOWiseTrackingReport(String header,String zone,String depot,String region,String area,String territory,String eId,String date,GoogleMap googleMap) {
        pb.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<JsonObject> call = api.getMIOWiseTrackingReport(header,zone,depot,region,area,territory,eId,date);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                pb.setVisibility(View.GONE);
                JsonObject serverResponse = response.body();
                Log.e("response",new Gson().toJson(serverResponse));
                if (response.code() == 200 && serverResponse != null ){
                    if (serverResponse.get("data").getAsJsonArray().size()>0){
                        notFound.setVisibility(View.GONE);

                        ArrayList<LatLng> latLngs = new ArrayList<>();
                        cList.clear();
                        latLngs.clear();
                        TypeToken<ArrayList<ParamMio>> token = new TypeToken<ArrayList<ParamMio>>() {};
                        cList = new Gson().fromJson(serverResponse.get("data").getAsJsonArray(), token.getType());
                        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                        for (ParamMio mio : cList) {
                            LatLng marker = new LatLng(Double.parseDouble(mio.getLongitude()),Double.parseDouble(mio.getLatitude()));
                            googleMap.addMarker(new MarkerOptions()
                                            .position(marker)
                                            .title("Exe.Address: "+mio.getLLAddress())
                                            .snippet("Name: "+mio.getMIOName() +"Date: "+convertDateTime(mio.getDateTime()))
                                            .icon(icon));
                            latLngs.add(marker);
                            lineOptions.add(marker);
                            lineOptions.width(10);
                            lineOptions.color(Color.GRAY);
                            builder.include(marker);
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 20;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.moveCamera(cu);
                        googleMap.animateCamera(cu);
                        googleMap.addPolyline(lineOptions);
                       /* List<String> urls = getDirectionsUrl(latLngs);
                        if (urls.size() > 1) {
                            for (int i = 0; i < urls.size(); i++) {
                                String url = urls.get(i);
                                Log.e("MyData",url);
                                DownloadTask downloadTask = new DownloadTask();
                                downloadTask.execute(url);
                            }
                        }*/
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

    /*private List<String> getDirectionsUrl(ArrayList<LatLng> markerPoints) {
        List<String> mUrls = new ArrayList<>();
        if (markerPoints.size() > 1) {
            String str_origin = markerPoints.get(0).longitude + "," + markerPoints.get(0).latitude;
            String str_dest = markerPoints.get(markerPoints.size()-1).longitude + "," + markerPoints.get(markerPoints.size()-1).latitude;

            String sensor = "sensor=true";
            String mode = "mode=driving";
            String key = "key="+getResources().getString(R.string.google_maps_key);

            String parameters = "origin=" + str_origin + "&destination=" + str_dest + "&" + sensor+"&" + mode + "&" + key;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

            mUrls.add(url);
            for (int i = 2; i < markerPoints.size(); i++)//loop starts from 2 because 0 and 1 are already printed
            {
                str_origin = str_dest;
                str_dest = markerPoints.get(i).longitude + "," + markerPoints.get(i).latitude;
                parameters = "origin=" + str_origin + "&destination=" + str_dest + "&" + sensor+"&" + mode + "&" + key;
                url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
                mUrls.add(url);
            }
        }

        return mUrls;
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(context, "No points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    builder.include(position);
                    points.add(position);
                }
                LatLngBounds bounds = builder.build();
                int padding = 20;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
                mMap.animateCamera(cu);
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(R.color.match_color);
            }

            mMap.addPolyline(lineOptions);
        }
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
          e.printStackTrace();
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (bundle!=null){
            JsonParser parser =  new JsonParser();
            JsonObject object = (JsonObject) parser.parse(bundle.getString("SET_PARAMS"));
            if (bundle.get(AppConstant.TYPE_REPORT).equals("CurrentLocation")){
//                Log.e("MyData","ZoneCode: "+ object.get("ZoneCode").getAsString());
//                Log.e("MyData","DepotCode: "+object.get("DepotCode").getAsString());
//                Log.e("MyData","RegionCode: "+object.get("RegionCode").getAsString());
//                Log.e("MyData","AreaCode: "+object.get("AreaCode").getAsString());
//                Log.e("MyData","TerritoryCode: "+object.get("TerritoryCode"));
//                Log.e("MyData","EmpCode: "+object.get("EmpCode"));
//                Log.e("MyData","Date: "+object.get("Date").getAsString());
//                Log.e("MyData","Token: "+pref.getString(AppConstant.TOKEN,""));
                getMIOWiseCurrentTrackingReport(pref.getString(AppConstant.TOKEN,""),object.get("ZoneCode").getAsString(),object.get("DepotCode").getAsString(),object.get("RegionCode").getAsString(), object.get("AreaCode").getAsString(),object.get("TerritoryCode").getAsString(),object.get("EmpCode").getAsString(),mMap);
            }else if(bundle.get(AppConstant.TYPE_REPORT).equals("RoadLocation")){
                getMIOWiseTrackingReport(pref.getString(AppConstant.TOKEN,""),object.get("ZoneCode").getAsString(),object.get("DepotCode").getAsString(),object.get("RegionCode").getAsString(), object.get("AreaCode").getAsString(),object.get("TerritoryCode").getAsString(),object.get("EmpCode").getAsString(),object.get("Date").getAsString(),mMap);
            }
        }
    }

    protected String convertDateTime(String dateTime){
       String result="";
        SimpleDateFormat originalDt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        Date date =null;
        try {
            date = originalDt.parse(dateTime);
            if (date != null) {
                result = outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}



