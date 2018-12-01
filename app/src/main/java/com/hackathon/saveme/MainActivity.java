package com.hackathon.saveme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
TextView distanceText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        distanceText = (TextView)findViewById(R.id.textView);
        LocalBroadcastManager.getInstance(this).registerReceiver(DistanceReciver,new IntentFilter("DistanceWalked"));
    }
    private BroadcastReceiver DistanceReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
      String message = intent.getStringExtra("distance");
      distanceText.setText("You walked :"+Double.toString(Double.parseDouble(message)/1000)+" km");
            Log.d("Distance",message);
        }
    };
}
