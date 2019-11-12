package com.toast;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class ControlBatteryService extends Service {

    private String TAG = ControlBatteryService.class.getName();
    final int min = 80;
    final int max = 95;

    public ControlBatteryService() {
        Log.i(TAG, "constructor here!");
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate event handled");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i(TAG, "onStartCommand event handled");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
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

                        Thread.sleep(2000);
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
