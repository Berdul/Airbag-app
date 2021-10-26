package com.bever.airbag;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bever.airbag.sensors.Accelerometer;
import com.bever.airbag.sensors.Gyroscope;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private static final int CUSTOM_PERMISSION_FINE_LOCATION = 999; // Custom int to track permission request

    TextView gyroX, gyroY, gyroZ, acceleroX, acceleroY, acceleroZ, latitude, longitude;
    SensorManager sensorManager;
    Accelerometer accelerometer;
    Gyroscope gyroscope;

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

        // SENSORS
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        gyroscope = new Gyroscope(sensorManager);
        gyroscope.setGyroListener(buildGyroEventListener());
        gyroscope.setGyroGraph(setupGraph((GraphView) findViewById(R.id.gyroGraph), gyroscope.getGyroSeries(), 10));

        accelerometer = new Accelerometer(sensorManager);
        accelerometer.setAcceleroListener(buildAcceleroEventListener());
        accelerometer.setAcceleroGraph(setupGraph((GraphView) findViewById(R.id.acceleroGraph), accelerometer.getAcceleroSeries(), 30));
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

                gyroX.setText(x.toString() + " rad/s²");
                gyroY.setText(y.toString() + " rad/s²");
                gyroZ.setText(z.toString() + " rad/s²");

                gyroscope.getGyroSeries().appendData(
                        new DataPoint(gyroscope.getGyroPointCount(), Math.sqrt(x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).doubleValue())),
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

                acceleroX.setText(x.toString() + " m/s²");
                acceleroY.setText(y.toString() + " m/s²");
                acceleroZ.setText(y.toString() + " m/s²");

                accelerometer.getAcceleroSeries().appendData(
                        new DataPoint(accelerometer.getAcceleroPointCount(), Math.sqrt(x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).doubleValue())),
                        true,
                        100);
                accelerometer.setAcceleroPointCount(accelerometer.getAcceleroPointCount() + 1);
            }
        };
    }
}
