package com.hackathon.saveme;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Calendar;
import java.util.Date;

public class PositionService extends Service {
    LocationManager locationManager;
    LocationListener locationListener;
    HttpURLConnection httpURLConnection;
    FileOutputStream stream;
    double lon,lat;
    double olon,olat;
    double oldDist;
    public PositionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }
private void distanceCalc() throws IOException {
        double r = 6371e3;
        double theta1 = Math.toRadians(olat);
        double theta2 = Math.toRadians(lat);
        double delTheta = Math.toRadians(lat-olat);
        double delLambda = Math.toRadians(lon- olon);
        double a = Math.sin(delTheta/2)*Math.sin(delTheta/2)+Math.cos(theta1)*Math.cos(theta2)*Math.sin(delLambda/2)*Math.sin(delLambda/2);
        double c = 2*Math.atan2(Math.sqrt(a),Math.sqrt((1-a)));
        Date ca = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    String filename = df.format(ca);

    File path = this.getApplicationContext().getFilesDir();
    File file = new File(path,filename);
if(file.exists()) {
int length = (int)file.length();
byte[] bytes = new byte[length];
    FileInputStream in = new FileInputStream(file);
    try {
        in.read(bytes);
    }finally {
        in.close();
    }
    String content = new String(bytes);
    oldDist = Double.parseDouble(content);

}else {
    try {
        stream = new FileOutputStream(file);
        stream.write(Double.toString(Math.abs(r * c)+oldDist).getBytes());
        Intent intent = new Intent("DistanceWalked");
        intent.putExtra("distance",Double.toString(Math.abs(r * c)+oldDist));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        stream.close();
    }

}
Log.d("File Status","File saved Successfully");
oldDist = 0;
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

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(((location.getSpeed()*3600/1000) > 0) && ((location.getSpeed()*3600/1000)<40)) {
                    try {
                        distanceCalc();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                lon = location.getLongitude();
                lat = location.getLatitude();


                Log.d("LOCATION:448",Double.toString(location.getLatitude())+Double.toString(location.getLongitude()));
                //Here we will send the Location to the server
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BufferedOutputStream os = null;
                        InputStream is = null;
                        try {
                            URLConnection url = new URL("http://192.168.1.61/emer?lat="+Double.toString(lat)+"&lon="+Double.toString(lon)).openConnection();
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
olat = lat;
olon = lon;

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
        locationManager.requestLocationUpdates(bestProvider,2*60*1000,10,locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
