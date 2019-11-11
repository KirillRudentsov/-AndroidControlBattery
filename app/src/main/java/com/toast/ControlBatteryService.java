package com.toast;

import android.app.Service;
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

    public ControlBatteryService() {
        Log.i(TAG, "constructor here!");
        //ShowToast("constructor here!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Log.i(TAG, "Control battery service running in background");
        //ShowToast("Control battery service running in background");
        /*ControlBatteryWorkerThread = new Thread(new Runnable() {
            public void run() {*/
                Log.i(TAG, "run new thread...");
                //StartControlBatteryCharging(80, 95);
            /*}
        });
        ControlBatteryWorkerThread.start();*/

        //try { ControlBatteryWorkerThread.join(); }catch (Exception ex){}
        final int min = 80;
        final int max = 95;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "StartControlBatteryCharging...");
                //ShowToast("StartControlBatteryCharging...");

                while (true) {

                    try {
                        Log.i(TAG, "work!");
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
                        //SystemClock.sleep(15000);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());

                        //Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).start();

        return START_STICKY;
    }


    @Override
    public void onDestroy(){

        /*if(!ControlBatteryWorkerThread.isInterrupted())
            ControlBatteryWorkerThread.interrupt();

        isRunning = false;*/
        Log.e(TAG,"Control battery service has been Destroyed");
        //ShowToast("Control battery service stoped");

        //Toast.makeText(getApplicationContext(),"Control battery service stoped", Toast.LENGTH_LONG).show();
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
