package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.opl.one.oplsales.MainActivity;
import java.util.concurrent.TimeUnit;
import service.MyWorker;


public class MyStartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresStorageNotLow(false)
                    .setRequiresCharging(false)
                    .build();

            PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES);
            builder.setConstraints(constraints);
            builder.setInitialDelay(30, TimeUnit.SECONDS);
            builder.addTag("checkLocation");
            PeriodicWorkRequest workRequest = builder.build();
            WorkManager.getInstance(MainActivity.getInstance()).enqueue(workRequest);
        }
    }
}