package com.hackathon.saveme;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class fallDetection extends AppCompatActivity implements SensorEventListener {
private SensorManager sensorManager;
private Sensor senAcc;
private TextView vzSpeedText;
private Vibrator v;
double ax,ay,az,a;
double max = 0;
double previousAcceleration = 0;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detection);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,senAcc,sensorManager.SENSOR_DELAY_NORMAL);
        vzSpeedText = (TextView)findViewById(R.id.speedVzText);
        v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        builder = new AlertDialog.Builder(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        az = event.values[2];
        ax = event.values[0];
        az = event.values[1];
        a = Math.sqrt(Math.pow(ax,2)+Math.pow(ay,2)+Math.pow(az,2));

        max = (a > max) ? a:max;
        vzSpeedText.setText("Your acceleraton is : " + Double.toString(a) + "and the max  is : "+Double.toString(max));
        if(Math.abs(previousAcceleration-a) > 10){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }else {
                v.vibrate(500);
            }
builder.setTitle("Fall Detector").setMessage("Are you OK ?").setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}).setNegativeButton("No", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}).show();

        }
        previousAcceleration = a;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }
}
