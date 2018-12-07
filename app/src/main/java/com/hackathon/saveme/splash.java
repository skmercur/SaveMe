package com.hackathon.saveme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class splash extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 5000;

    private void CheckFirstTime() {
        File path = getFilesDir();
        File file = new File(path, "userdata");
        if (file.exists()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(splash.this, MainActivity.class);
                    Intent FallDectector = new Intent(splash.this, FallDetectorService.class);
                    Intent PositionSer = new Intent(splash.this, PositionService.class);
                    Intent BPMSer = new Intent(splash.this, BPMSimulator.class);
                    startService(FallDectector);
                    startService(PositionSer);
                    startService(BPMSer);
                    splash.this.startActivity(mainIntent);
                    splash.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent mainIntent = new Intent(splash.this, userData.class);
                    Intent FallDectector = new Intent(splash.this, FallDetectorService.class);
                    Intent PositionSer = new Intent(splash.this, PositionService.class);
                    Intent BPMSer = new Intent(splash.this, BPMSimulator.class);
                    startService(FallDectector);
                    startService(PositionSer);
                    startService(BPMSer);
                    splash.this.startActivity(mainIntent);
                    splash.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        CheckFirstTime();

    }
}
