package com.hackathon.saveme;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BPMSimulator extends Service {
    int BPM;

    public BPMSimulator() {
    }

    private void BMSGEN() {
        Random rand = new Random();
        BPM = 80 + rand.nextInt(5);
        Intent intent = new Intent("BPM");
        intent.putExtra("BPMvalue", Double.toString(BPM));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                BMSGEN();
            }
        }, 1000, 10000);


        return super.onStartCommand(intent, flags, startId);
    }
}
