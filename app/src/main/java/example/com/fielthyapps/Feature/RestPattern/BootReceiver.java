package example.com.fielthyapps.Feature.RestPattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        android.util.Log.d(
                "REST_BOOT",
                "BOOT RECEIVED : " + intent.getAction()
        );

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                "android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction())) {

            android.util.Log.d(
                    "REST_BOOT",
                    "STARTING SLEEP SERVICE"
            );

            Intent serviceIntent =
                    new Intent(
                            context,
                            SleepDetectionService.class
                    );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
