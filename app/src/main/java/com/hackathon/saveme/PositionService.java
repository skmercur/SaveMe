package com.hackathon.saveme;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class PositionService extends Service {
    LocationManager locationManager;
    LocationListener locationListener;

    double lon,lat;
    double olon,olat;
    double oldDist;
    double waterDistance = 0;
    double speed = 0;
    public PositionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    private void KeepLocation() {
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("Waiting", "for the Counter to finish");
            }

            @Override
            public void onFinish() {
                olat = lat;
                olon = lon;
                Log.d("Finished ", "counting");

            }
        }.start();
    }
private void distanceCalc() throws IOException {

        double r = 6371e3;
        double theta1 = Math.toRadians(olat);
        double theta2 = Math.toRadians(lat);
        double delTheta = Math.toRadians(lat-olat);
        double delLambda = Math.toRadians(lon- olon);
        double a = Math.sin(delTheta/2)*Math.sin(delTheta/2)+Math.cos(theta1)*Math.cos(theta2)*Math.sin(delLambda/2)*Math.sin(delLambda/2);
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt((1-a)));
    waterDistance += Math.abs(c);
    Calendar ca = Calendar.getInstance();
    ca.add(Calendar.DATE, 0);
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    String filename = df.format(ca.getTime());
    Intent intent = new Intent("DistanceWalked");
    intent.putExtra("distance", Double.toString(Math.abs(r * c) + oldDist));
    intent.putExtra("water", Double.toString(waterDistance));
    intent.putExtra("speed", Double.toString(speed));
    Date d1 = new Date();
    intent.putExtra("stime", Long.toString(d1.getTime()));
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    File path = this.getApplicationContext().getFilesDir();
    File file = new File(path,filename);

int length = (int)file.length();
byte[] bytes = new byte[length];
    if (file.exists()) {
        FileInputStream in = new FileInputStream(file);

        in.read(bytes);
        in.close();
        String content = new String(bytes);
        oldDist = Double.parseDouble(content);
    } else {
        oldDist = 0;
    }

    try {
        OutputStream os = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(os);


        writer.write(Double.toString(Math.abs(r * c) + oldDist));
        writer.close();

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    Log.d("Del Lat", Double.toString(delTheta));
    Log.d("Del lon", Double.toString(delLambda));
    Log.d("File Status", "File saved Successfully");
}


    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final double k = 4;


        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                /*  if(((location.getSpeed()*3600/1000) > 0) && ((location.getSpeed()*3600/1000)<40)) {*/

//            }

                speed = location.getSpeed();
                Intent intent = new Intent("position");
                intent.putExtra("lon", Double.toString(location.getLongitude()));
                intent.putExtra("lat", Double.toString(location.getLatitude()));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                if (waterDistance > 2500) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification n = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        n = new Notification.Builder(getApplicationContext()).setContentTitle("Time to drink water")
                                .setContentText("You walked " + Double.toString(waterDistance) + " meters its time to drink")
                                .setSmallIcon(R.drawable.almonkidh)
                                .setAutoCancel(true)
                                .build();
                    }
                    notificationManager.notify(0, n);
                    waterDistance = 0;
                }

                lon = location.getLongitude();
                lat = location.getLatitude();
                if ((olon == 0) || (olat == 0)) {
                    olon = lon;
                    olat = lat;
                }

                //Here we will send the Location to the server



                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        KeepLocation();
                        Log.d("Keeping", "Keeping");
                    }
                });
                try {
                    distanceCalc();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(bestProvider, 1000, 0, locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}

