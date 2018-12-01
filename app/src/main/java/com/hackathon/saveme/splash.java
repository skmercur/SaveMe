package com.hackathon.saveme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(splash.this,MainActivity.class);
                Intent FallDectector = new Intent(splash.this,FallDetectorService.class);
                Intent PositionSer = new Intent(splash.this,PositionService.class);
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
