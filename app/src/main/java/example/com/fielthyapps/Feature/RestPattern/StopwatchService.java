package example.com.fielthyapps.Feature.RestPattern;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import example.com.fielthyapps.R;

public class StopwatchService extends Service {
    private static final String CHANNEL_ID = "StopwatchChannel";
    private static final int NOTIFICATION_ID = 1;
    private long startTime;
    private boolean isRunning;
    private Handler handler = new Handler();

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                sendElapsedTime(elapsedMillis);
                handler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("StopwatchService", "Service created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = System.currentTimeMillis();
        isRunning = true;
        handler.post(updateTimer);
        Log.d("StopwatchService", "Service started");

        Intent notificationIntent = new Intent(this, RestPatternActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Stopwatch")
                .setContentText("Rest Pattern Running")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimer);
        isRunning = false;
        Log.d("StopwatchService", "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendElapsedTime(long elapsedTime) {
        Intent intent = new Intent("stopwatch_update");
        intent.putExtra("elapsed_time", elapsedTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Stopwatch Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
