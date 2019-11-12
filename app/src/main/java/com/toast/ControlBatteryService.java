package com.toast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

public class ControlBatteryService extends Service {

    private String TAG = ControlBatteryService.class.getName();
    final int min = 80;
    final int max = 95;
    private boolean isRunning = false;

    public ControlBatteryService() {
        Log.i(TAG, "constructor here!");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate event handled");
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(11011, notification);

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "1233321123";
        String channelName = "toast channel name";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        isRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Log.i(TAG, "work!!!");
                        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

                        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                        int chargingType = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                        if (batLevel <= min)
                            Sudoer.su(BatteryChargingCommands.charge_command);
                        if (batLevel >= max) {
                            Sudoer.su(BatteryChargingCommands.discharge_command_1);
                            Sudoer.su(BatteryChargingCommands.discharge_command_2);
                        }

                        Thread.sleep(15000);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }
            }
        }).start();

        return START_STICKY;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();

        isRunning = false;

        Log.e(TAG,"onDestroy event handled");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void ShowToast(final String text){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });

    }
}
