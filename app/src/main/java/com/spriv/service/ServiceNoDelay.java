package com.spriv.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.spriv.activity.AccountsActivity;
import com.spriv.model.ServerAPI;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

public class ServiceNoDelay extends Service {
    public int counter = 0;
    Context context;

    public ServiceNoDelay(Context applicationContext) {
        super();
        context = applicationContext;
        Log.i("HERE", "here service created!");
    }

    public ServiceNoDelay() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("EXIT_make_log", "ondestroy!");

        Intent broadcastIntent = new Intent("ac.in.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 20000, 1000 * 60 * 60); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.d("in timer_make_log", "in timer ++++  " + (counter++));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            writeLogToFile(ServiceNoDelay.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
    }

    public void writeLogToFile(Context context) throws IOException {
        String fileName = "logcat.txt";
        final File file= new File(context.getExternalCacheDir(),fileName);

        if (file.exists()){
            Log.d("Check_make_log", file.length() / 1024 + " KB");
            if (file.length()/1024 <= 480){
                String command = "logcat -f " + file.getAbsolutePath();
                Process p = Runtime.getRuntime().exec(command);
                try {
                    Thread.sleep(4000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p.destroy();
            }
        } else {
            file.createNewFile();
            String command = "logcat -f "+file.getAbsolutePath();
            Process p = Runtime.getRuntime().exec(command);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.destroy();
        }
        Log.d("After_make_log", file.length() / 1024 + " KB");

        if(file.length() / 1024 > 490) {
            file.delete();
        }
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}