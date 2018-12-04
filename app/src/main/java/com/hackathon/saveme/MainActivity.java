package com.hackathon.saveme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cunoraz.gifview.library.GifView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
TextView distanceText;
    TextView BpmText;
    TextView waterText;
    TextView walkingTargetText;
    ProgressBar waterProgress;
    ProgressBar walkingProgress;
    String message, message1, message2, message3, message4;
    List<Double> gg = new ArrayList<Double>();
    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        distanceText = (TextView)findViewById(R.id.textView);
        BpmText = (TextView) findViewById(R.id.textView2);
        waterText = (TextView) findViewById(R.id.textTargetWater);
        walkingTargetText = (TextView) findViewById(R.id.textTargetWalking);
        graphView = (GraphView) findViewById(R.id.graphBar);
        waterProgress = (ProgressBar) findViewById(R.id.progressBar2);
        walkingProgress = (ProgressBar) findViewById(R.id.progressBar3);
        GifView gifView = (GifView) findViewById(R.id.gif1);
        gifView.setVisibility(View.VISIBLE);
        gifView.setGifResource(R.drawable.ekg);
        gifView.play();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    distanceDataReader();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, gg.get(0)),
                        new DataPoint(1, gg.get(1)),
                        new DataPoint(2, gg.get(2)),
                        new DataPoint(3, gg.get(3)),
                        new DataPoint(4, gg.get(4)),
                        new DataPoint(5, gg.get(5)),
                        new DataPoint(6, gg.get(6))
                });
                graphView.addSeries(series);
                series.setSpacing(50);
                series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                    @Override
                    public int get(DataPoint data) {
                        return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
                    }
                });
            }
        });


        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(6);

        LocalBroadcastManager.getInstance(this).registerReceiver(DistanceReciver,new IntentFilter("DistanceWalked"));
        LocalBroadcastManager.getInstance(this).registerReceiver(BPMrecviver, new IntentFilter("BPM"));


    }

    private void distanceDataReader() throws IOException {


        for (int i = -6; i <= 0; i++) {
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DATE, i);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            String filename = df.format(ca.getTime());
            File path = this.getApplicationContext().getFilesDir();
            File file = new File(path, filename);

            if (file.exists()) {
                int length = (int) file.length();
                byte[] bytes = new byte[length];
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    in.read(bytes);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                }


                String content = new String(bytes);
                double val = Double.parseDouble(content);
                gg.add(val);
                Log.d("File state:", "Existes file name : " + filename);
            } else {
                gg.add(0.0);
                Log.d("File state:", "Doesn't Existes file name : " + filename);
            }


        }


    }
    private BroadcastReceiver BPMrecviver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("BPMvalue");
            BpmText.setText(message);
        }
    };
    private BroadcastReceiver DistanceReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("distance");
            message2 = intent.getStringExtra("water");
            message4 = intent.getStringExtra("stime");
            message3 = intent.getStringExtra("speed");
            distanceText.setText("You walked :" + Double.toString(Math.round(Double.parseDouble(message))) + " m");
            double timeToDrink = (Double.parseDouble(message2) / Double.parseDouble(message3)) / 60;
            if (Double.parseDouble(message3) > 0) {
                waterText.setText("You will need to drink water in : " + Double.toString(Math.round(timeToDrink)) + " minutes");
            } else {
                waterText.setText("Remember to stay hydrated");
            }
            if (Double.parseDouble(message) - 2500 < 0) {
                walkingTargetText.setText("There are " + Double.toString(Math.round(Math.abs(Double.parseDouble(message) - 2500) / 1000)) + " Km left");
            } else {
                walkingTargetText.setText("You passed the goal by " + Double.toString(Math.round(Math.abs(Double.parseDouble(message) - 2500) / 1000)) + " Km");

            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if ((long) Double.parseDouble(message3) - Long.parseLong(message4) > 0) {
                        int progress = (int) ((long) Double.parseDouble(message3) / Long.parseLong(message4)) * 100;
                        waterProgress.setProgress(progress);
                    } else {
                        waterProgress.setProgress(50);
                    }
                }
            });
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (Double.parseDouble(message) - 2500 < 0) {
                        int progress = (int) (Double.parseDouble(message) / 2500) * 100;
                        walkingProgress.setProgress(progress);
                    } else {
                        walkingProgress.setProgress(100);
                    }
                }
            });
        }
    };
}
