package com.hackathon.saveme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Parameter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

public class emergencyActivity extends AppCompatActivity {
    android.hardware.Camera cam;
    android.hardware.Camera.Parameters p;
    int k = 0;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emergency);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://192.168.1.61:8080/SaveMeWeb/dataReciver/index.php");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoInput(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                    connection.connect();


                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lat", "3333");
                    jsonObject.put("lon", "8888");
                    OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                    outputStream.write(jsonObject.toString().getBytes());
                    outputStream.flush();


                    outputStream.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        LocalBroadcastManager.getInstance(this).registerReceiver(positionReciver, new IntentFilter("position"));
/*

        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(k == 0){
                        cam = android.hardware.Camera.open();
                        p = cam.getParameters();
                        p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                        cam.setParameters(p);
                        cam.startPreview();
                        k = 1;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        }else {
                            v.vibrate(500);
                        }
                    }else{
                        cam.stopPreview();
                        cam.release();
                        cam = null;
                        k = 0;

                    }
                }
            },0,500);


            Toast.makeText(getApplicationContext(),"It has flash",Toast.LENGTH_LONG).show();
        }
        */
    }

    private BroadcastReceiver positionReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("lon");
            final String message1 = intent.getStringExtra("lat");
            Log.d("Lat", message);
            Log.d("lon", message1);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://192.168.1.61:8080/SaveMeWeb/dataReciver/index.php");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoInput(false);
                        connection.setRequestMethod("POST");
                        connection.connect();
                        OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("lat", message1);
                        jsonObject.put("lon", message);
                        outputStream.write(jsonObject.toString().getBytes());
                        outputStream.flush();
                        outputStream.close();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };
}
