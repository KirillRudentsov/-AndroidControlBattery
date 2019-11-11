package com.toast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import static android.content.Context.BATTERY_SERVICE;

public class BootCompleteReceiver extends BroadcastReceiver {

    private String TAG = BootCompleteReceiver.class.getName();

    @Override
    public void onReceive(final Context context, Intent intent) {

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!ServiceTools.isServiceRunning_2(context, ControlBatteryService.class.getName()))
                context.startForegroundService(new Intent(context, ControlBatteryService.class));
        }else {
            context.startService(new Intent(context, ControlBatteryService.class));
        }*/

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
                        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = context.getApplicationContext().registerReceiver(null, ifilter);

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


    }

}
