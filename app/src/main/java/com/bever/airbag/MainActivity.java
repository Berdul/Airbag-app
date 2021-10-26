package com.bever.airbag;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bever.airbag.sensors.Accelerometer;
import com.bever.airbag.sensors.Gyroscope;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private static final int CUSTOM_PERMISSION_FINE_LOCATION = 999; // Custom int to track permission request
    private static final String TAG = "YOYOYYOYO";

    TextView gyroX, gyroY, gyroZ, acceleroX, acceleroY, acceleroZ, latitude, longitude, accuracy, speed, normalLinearAcceleration, normalAngularAcceleration;
    Switch switchGps;
    SensorManager sensorManager;
    Accelerometer accelerometer;
    Gyroscope gyroscope;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gyroX = findViewById(R.id.gyroX);
        gyroY = findViewById(R.id.gyroY);
        gyroZ = findViewById(R.id.gyroZ);
        acceleroX = findViewById(R.id.acceleroX);
        acceleroY = findViewById(R.id.acceleroY);
        acceleroZ = findViewById(R.id.acceleroZ);
        latitude = findViewById(R.id.latitudeValue);
        longitude = findViewById(R.id.longitudeValue);
        accuracy = findViewById(R.id.gpsAccuracyValue);
        switchGps = findViewById(R.id.switchGps);
        speed = findViewById(R.id.speed);
        normalLinearAcceleration = findViewById(R.id.normalLinearAccelValue);
        normalAngularAcceleration = findViewById(R.id.normalAngularAccelValue);

        // SENSORS
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        gyroscope = new Gyroscope(sensorManager);
        gyroscope.setGyroListener(buildGyroEventListener());
        gyroscope.setGyroGraph(setupGraph((GraphView) findViewById(R.id.gyroGraph), gyroscope.getGyroSeries(), 10));

        accelerometer = new Accelerometer(sensorManager);
        accelerometer.setAcceleroListener(buildAcceleroEventListener());
        accelerometer.setAcceleroGraph(setupGraph((GraphView) findViewById(R.id.acceleroGraph), accelerometer.getAcceleroSeries(), 30));

        // Location
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        updateGps();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateGpsUI(locationResult.getLastLocation());
            }
        };

        switchGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchGps.isChecked()) {
                    startLocationUpdate();
                } else {
                    stopLocationUpdate();
                }
            }
        });
    }

    private void stopLocationUpdate() {
        updateGpsUI(null);
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGps();
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscope.getGyroListener(), gyroscope.getSensor(), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accelerometer.getAcceleroListener(), accelerometer.getSensor(), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(gyroscope.getGyroListener());
        sensorManager.unregisterListener(accelerometer.getAcceleroListener());
    }

    private GraphView setupGraph(GraphView viewGraph, LineGraphSeries series, int maxY) {
        viewGraph.addSeries(series);
        viewGraph.getViewport().setXAxisBoundsManual(true);
        viewGraph.getViewport().setYAxisBoundsManual(true);
        viewGraph.getViewport().setMinX(0);
        viewGraph.getViewport().setMaxX(100);
        viewGraph.getViewport().setMinY(0);
        viewGraph.getViewport().setMaxY(maxY);

        return viewGraph;
    }

    private SensorEventListener buildGyroEventListener() {
        return new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int acc) {
            }

            public void onSensorChanged(SensorEvent event) {
                BigDecimal x = new BigDecimal(event.values[0]).setScale(3, RoundingMode.HALF_UP);
                BigDecimal y = new BigDecimal(event.values[1]).setScale(3, RoundingMode.HALF_UP);
                BigDecimal z = new BigDecimal(event.values[2]).setScale(3, RoundingMode.HALF_UP);

                gyroX.setText(" " + x.toString() + " rad/s²");
                gyroY.setText(" " + y.toString() + " rad/s²");
                gyroZ.setText(" " + z.toString() + " rad/s²");

                BigDecimal normal = new BigDecimal(Math.sqrt(x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).doubleValue()))
                                            .setScale(3, RoundingMode.HALF_UP);

                normalAngularAcceleration.setText(" " + normal + " rad/s²");

                gyroscope.getGyroSeries().appendData(
                        new DataPoint(gyroscope.getGyroPointCount(), normal.doubleValue()),
                        true,
                        100);
                gyroscope.setGyroPointCount(gyroscope.getGyroPointCount() + 1);
            }
        };
    }

    private SensorEventListener buildAcceleroEventListener() {
        return new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int acc) {
            }

            public void onSensorChanged(SensorEvent event) {
                BigDecimal x = new BigDecimal(event.values[0]).setScale(3, RoundingMode.HALF_UP);
                BigDecimal y = new BigDecimal(event.values[1]).setScale(3, RoundingMode.HALF_UP);
                BigDecimal z = new BigDecimal(event.values[2]).setScale(3, RoundingMode.HALF_UP);

                acceleroX.setText(" " + x.toString() + " m/s²");
                acceleroY.setText(" " + y.toString() + " m/s²");
                acceleroZ.setText(" " + y.toString() + " m/s²");

                BigDecimal normal = new BigDecimal(Math.sqrt(x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).doubleValue()) - 9.81)
                                            .setScale(3, RoundingMode.HALF_UP);
                normalLinearAcceleration.setText(" " + normal + " m/s²");

                accelerometer.getAcceleroSeries().appendData(
                        new DataPoint(accelerometer.getAcceleroPointCount(), normal.doubleValue()),
                        true,
                        100);
                accelerometer.setAcceleroPointCount(accelerometer.getAcceleroPointCount() + 1);
            }
        };
    }

    private void updateGps() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (switchGps.isChecked()) {
                        updateGpsUI(location);
                    } else {
                        updateGpsUI(null);
                    }
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        CUSTOM_PERMISSION_FINE_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CUSTOM_PERMISSION_FINE_LOCATION) {
            updateGps();
        } else {
            Toast.makeText(this, "Permission for location not granted but required.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGpsUI(Location location) {
        if (location != null) {
            latitude.setText(String.valueOf(location.getLatitude()));
            longitude.setText(String.valueOf(location.getLongitude()));
            speed.setText(String.valueOf(location.getSpeed()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                speed.append(" +/- " + location.getSpeedAccuracyMetersPerSecond() + "m/s²");
            }
            accuracy.setText(String.valueOf(location.getAccuracy()));
        } else {
            latitude.setText("-");
            longitude.setText("-");
            speed.setText("-");
            accuracy.setText("-");
        }
    }
}
