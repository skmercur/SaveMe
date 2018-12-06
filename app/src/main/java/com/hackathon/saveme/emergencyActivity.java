package com.hackathon.saveme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
                BufferedOutputStream os = null;
                InputStream is = null;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lat", "3333");
                    jsonObject.put("lon", "8888");
                    byte[] enco = jsonObject.toString().getBytes("UTF-8");
                    String encoded = Base64.encodeToString(enco,Base64.DEFAULT);
                    URLConnection url = new URL("http://192.168.1.61/emer?json="+encoded).openConnection();
                    url.setRequestProperty("Accept-Charset","utf-8");
                    is = new BufferedInputStream(url.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }

                    Log.d("Results : ",result.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    try {
                        if(os != null){
                            os.close();

                        }
                        if(is != null){
                            is.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();


      /*  new Thread(new Runnable() {
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
*/
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
