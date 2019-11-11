package com.toast;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static boolean isStopedControlling = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText command_text = findViewById(R.id.tb_Command);
        final EditText result = findViewById(R.id.tb_Result);
        final Button btn_showToast = findViewById(R.id.btnShowToast);
        btn_showToast.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {

                    ShowToast();

                }catch (Exception ex){ Log.i("Error on click", ex.getMessage()); }
            }
        });

        //btnCheckConnection
        final Button btn_checkConnection = findViewById(R.id.btnCheckConnection);
        btn_checkConnection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    ShowToast( isOnline(command_text.getText().toString()) ? "Есть интернет" : "Нету интернета" );

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
        final Button btn_scb = findViewById(R.id.btn_scb);
        btn_scb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    isStopedControlling = false;
                    int min = Integer.parseInt(min_lvl.getText().toString());
                    int max = Integer.parseInt(max_lvl.getText().toString());
                    Run(min, max);

                }catch (Exception ex){
                    ShowToast(ex.getMessage());
                }
            }
        });
        final Button btn_StopControlling = findViewById(R.id.btn_StopControlling);
        btn_StopControlling.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    isStopedControlling = true;

                }catch (Exception ex){ ShowToast(ex.getMessage()); }
            }
        });

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


        btn_scb.performClick();
    }

    private Thread workerThread;

    private void StartControlBatteryCharging(int minLvl, int maxLvl) {

        try {
            while (!isStopedControlling) {
                BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

                int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                int chargingType = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

                if( batLevel <= minLvl)
                    Sudoer.su(BatteryChargingCommands.charge_command);
                if( batLevel >= maxLvl) {
                    Sudoer.su(BatteryChargingCommands.discharge_command_1);
                    Sudoer.su(BatteryChargingCommands.discharge_command_2);
                }
                //ShowToast("current battery lvl : " + String.valueOf(batLevel));

                ShowToast("isCharging : " + bm.isCharging()
                        + " lvl : " + String.valueOf(batLevel) );

                Thread.sleep(300000);
            }
        } catch (Exception ex) {
            ShowToast(ex.getMessage());
        }

    }

    private void Run(final int minLvl,final int maxLvl){

        workerThread = new Thread(new Runnable() {
            public void run() {

                StartControlBatteryCharging(minLvl, maxLvl);

            }
        });

        workerThread.start();

    }

    public boolean isOnline(String command) throws Exception {
        //Runtime runtime = Runtime.getRuntime();
        try {
            //Process ipProcess = runtime.exec(command);
            //Sudoer.su(command).waitFor();

            int  exitValue = Sudoer.su(command).waitFor();

            final EditText result = findViewById(R.id.tb_Result);
            result.setText( Integer.toString(exitValue) );

            return (exitValue == 0);
        }
        catch (IOException e)          { ShowToast(e.getMessage()); }
        catch (InterruptedException e) { ShowToast(e.getMessage()); }

        return false;
    }

    public void ShowToast()
    {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Ошибка получения данных абонентского профиля. Попробуйте позднее.", Toast.LENGTH_SHORT);
        toast.show();
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
