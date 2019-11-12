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

        if(!ServiceTools.isServiceRunning_2(context, ControlBatteryService.class.getName())) {
            Intent s_intent = new Intent(context, ControlBatteryService.class);
            context.startForegroundService(s_intent);
        }

    }

}
