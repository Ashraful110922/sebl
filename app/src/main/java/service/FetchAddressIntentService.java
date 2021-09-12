package service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import utils.AppConstant;

public class FetchAddressIntentService extends IntentService {
    private ResultReceiver resultReceiver;
    public FetchAddressIntentService() {
        super("providerNA");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent!=null){
            String errorMessage ="";
            resultReceiver = intent.getParcelableExtra(AppConstant.RECEIVER);
            Location location = intent.getParcelableExtra(AppConstant.LOCATION_DATA_EXTRA);
            if (location ==null){
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
              addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (addresses==null || addresses.isEmpty()){
                deliveryResultToReceiver(AppConstant.FAIL_RESULT,errorMessage);
            }else {
                  Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();
                for (int i = 0;i<=address.getMaxAddressLineIndex();i++){
                    addressFragments.add(address.getAddressLine(i));
                }
                deliveryResultToReceiver(AppConstant.SUCCESS_RESULT, TextUtils.join(
                        Objects.requireNonNull(System.getProperty("line.separator")),
                        addressFragments
                ));
            }
        }
    }

    private void deliveryResultToReceiver(int resultCode,String addressMessage){
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.RESULT_DATA_KEY,addressMessage);
        resultReceiver.send(resultCode,bundle);
    }
}
