package com.hackathon.saveme;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

public class FallDetectorService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor senAcc;
    private Vibrator v;
    double ax,ay,az,a;
    double max = 0;
    double previousAcceleration = 0;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,senAcc,sensorManager.SENSOR_DELAY_NORMAL);
        v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        return super.onStartCommand(intent, flags, startId);
    }

    public FallDetectorService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        az = event.values[2];
        ax = event.values[0];
        az = event.values[1];
        a = Math.sqrt(Math.pow(ax,2)+Math.pow(ay,2)+Math.pow(az,2));

        max = (a > max) ? a:max;

        if(Math.abs(previousAcceleration-a) > 10){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }else {
                v.vibrate(500);
            }
           AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.FallDetectorTheme)).setTitle("Fall Detector").setMessage("Are you ok ?")
                   .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                       }
                   })
                   .setNegativeButton("no", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                       }
                   })
                   .create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();

        }
        previousAcceleration = a;


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
