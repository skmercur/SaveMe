package com.hackathon.saveme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
    ArrayList<String> userDataCollected = new ArrayList<String>();
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


        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", userDataCollected.get(0));
                    jsonObject.put("email", userDataCollected.get(1));
                    jsonObject.put("allergy", userDataCollected.get(2));
                    jsonObject.put("image", new String(Base64.encode(userDataCollected.get(3).getBytes(), Base64.DEFAULT)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String dataF = jsonObject.toString();

                byte[] encodedJson = Base64.encode(dataF.getBytes(), Base64.DEFAULT);
                String url = null;
                try {
                    url = "http://192.168.1.61/emer?json=" + URLEncoder.encode(dataF, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("sending to ", url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response : ", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                    }
                });

             /*  OutputStreamWriter os = null;
                InputStreamReader is = null;
                try {
                    String message1 = "3.4888888444";
                    String message = "44.5555555;55";
                 String name = "sofiane";
                    Log.d("hzheh",name);
                    String parameters = "lat="+message1+"&lon="+message;
                    URL url = new URL("http://192.168.1.61/emer");
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                    connection.setRequestMethod("GET");

os = new OutputStreamWriter(connection.getOutputStream());
os.write(parameters);
os.flush();
os.close();
connection.connect();
if(connection.getResponseCode() >=200 && connection.getResponseCode() < 400) {
    is = new InputStreamReader(connection.getInputStream());
}else{
    is = new InputStreamReader(connection.getErrorStream());
}
                    Log.d("response",Integer.toString(connection.getResponseCode()));
//TODO fix send name


                    BufferedReader reader = new BufferedReader(is);
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }

                    Log.d("Results : ",result.toString());
is.close();
reader.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (EOFException r){
                    r.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();


                }*/
                queue.add(stringRequest);

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

    private BroadcastReceiver positionReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("lon");
            final String message1 = intent.getStringExtra("lat");
            Log.d("Lat", message);
            Log.d("lon", message1);
/*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedOutputStream os = null;
                    InputStream is = null;
                    try {
                        URLConnection url = new URL("http://192.168.43.215:8080/emer?lat="+message1+"&lon="+message).openConnection();
                        Log.d("State :","Starting");


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
                    }
                }
            }).start();*/
        }
    };
}
