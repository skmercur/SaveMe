package com.hackathon.saveme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class emergencyActivity extends AppCompatActivity {
    android.hardware.Camera cam;
    android.hardware.Camera.Parameters p;
    String message,message1;
    TextView statusText;
    ArrayList<String> userDataCollected = new ArrayList<String>();
    private  String android_id;
    int k = 0;
    private Vibrator v;

    private void readUserData() {
        File path = getFilesDir();
        File file = new File(path, "userdata");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                userDataCollected.add(line);

            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readUserData();
        setContentView(R.layout.activity_emergency);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
statusText = (TextView)findViewById(R.id.textView18);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("status", "1");
                    jsonObject.put("id", android_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String dataF = jsonObject.toString();

                byte[] encodedJson = Base64.encode(dataF.getBytes(), Base64.DEFAULT);
                String url = null;
                try {
                    url = "http://192.168.1.61/help?json=" + URLEncoder.encode(dataF, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("sending to ", url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response : ", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            statusText.setText(obj.getString("status"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                    }
                });

                queue.add(stringRequest);

            }

        }).start();

        /*if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
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
        }*/
    }

}
