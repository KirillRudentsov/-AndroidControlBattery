package com.toast;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import com.toast.ServiceTools;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*final EditText command_text = findViewById(R.id.tb_Command);

        //btnCheckConnection
        final Button btn_checkConnection = findViewById(R.id.btnCheckConnection);
        btn_checkConnection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    ShowToast( isOnline(command_text.getText().toString()) ? "Has internet" : "Has no internet" );

                }catch (Exception ex){ Log.i("Error", ex.getMessage()); }
            }
        });



        //charging part
        final Button btn_disCh = findViewById(R.id.btn_disCh);
        btn_disCh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    Sudoer.su(BatteryChargingCommands.discharge_command_1);
                    Sudoer.su(BatteryChargingCommands.discharge_command_2);
                }catch (Exception ex){ ShowToast(ex.getMessage()); }
            }
        });
        final Button btn_enCh = findViewById(R.id.btn_EnCh);
        btn_enCh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    Sudoer.su(BatteryChargingCommands.charge_command);
                }catch (Exception ex){ ShowToast(ex.getMessage()); }
            }
        });

        final EditText min_lvl = findViewById(R.id.tb_MinLvl);
        final EditText max_lvl = findViewById(R.id.tb_MaxLvl);
        final Button btn_scb = findViewById(R.id.btn_startService);
        btn_scb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(!ServiceTools.isServiceRunning_2(getApplicationContext(), ControlBatteryService.class.getName()))
                        startService(new Intent(getApplicationContext(), ControlBatteryService.class));
                    else {
                        ShowToast("Service already running!");
                    }
                }catch (Exception ex){
                    ShowToast(ex.getMessage());
                }
            }
        });
        final Button btn_StopControlling = findViewById(R.id.btn_stopService);
        btn_StopControlling.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(ServiceTools.isServiceRunning_2(getApplicationContext(), ControlBatteryService.class.getName()))
                        stopService(new Intent(getApplicationContext(), ControlBatteryService.class));

                }catch (Exception ex){ ShowToast(ex.getMessage()); }
            }
        });

        final Button btn_checkService = findViewById(R.id.btn_checkService);
        btn_checkService.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    boolean isRunning = ServiceTools.isServiceRunning_2(getApplicationContext(), ControlBatteryService.class.getName());
                    ShowToast("service is running : " + String.valueOf(isRunning));
                }catch (Exception ex){ ShowToast(ex.getMessage()); }
            }
        });*/

        final Button btn_ChStat = findViewById(R.id.btn_ChStat);
        btn_ChStat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                    int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    boolean isCharging = bm.isCharging();

                    ShowToast("lvl : " + batLevel + " isCharging : " + isCharging);

                }catch (Exception ex){ ShowToast(ex.getMessage()); }
            }
        });

        final Button btn = findViewById(R.id.btn_regService);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    IntentFilter broadcast = new IntentFilter("com.toast.BootCompleteReceiver");
                    registerReceiver(new BootCompleteReceiver(), broadcast);
                }catch (Exception ex){
                    Log.e(TAG,ex.getMessage());
                }
            }
        });
    }

    public boolean isOnline(String command) throws Exception {
        //Runtime runtime = Runtime.getRuntime();
        try {
            //Process ipProcess = runtime.exec(command);
            //Sudoer.su(command).waitFor();

            int  exitValue = Sudoer.su(command).waitFor();

            return (exitValue == 0);
        }
        catch (IOException e)          { ShowToast(e.getMessage()); }
        catch (InterruptedException e) { ShowToast(e.getMessage()); }

        return false;
    }

    public void ShowToast(final String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


}
