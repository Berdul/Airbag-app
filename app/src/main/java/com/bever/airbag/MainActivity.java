package com.bever.airbag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    TextView gyroX, gyroY, gyroZ, acceleroX, acceleroY, acceleroZ;
    SensorManager sensorManager;
    Sensor gyroscope;
    Sensor accelerometer;

    GraphView gyroGraph;
    LineGraphSeries<DataPoint> gyroSeries;
    int gyroPointCount = 0;

    GraphView acceleroGraph;
    LineGraphSeries<DataPoint> acceleroSeries;
    int acceleroPointCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SENSORS
        gyroX = findViewById(R.id.gyroX);
        gyroY = findViewById(R.id.gyroY);
        gyroZ = findViewById(R.id.gyroZ);

        acceleroX = findViewById(R.id.acceleroX);
        acceleroY = findViewById(R.id.acceleroY);
        acceleroZ = findViewById(R.id.acceleroZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // GRAPH
        gyroSeries = new LineGraphSeries<>();
        acceleroSeries = new LineGraphSeries<>();
        gyroGraph = setupGraph((GraphView) findViewById(R.id.gyroGraph), gyroSeries, 10);
        acceleroGraph = setupGraph((GraphView) findViewById(R.id.acceleroGraph), acceleroSeries, 30);
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            BigDecimal x = new BigDecimal(event.values[0]).setScale(3, RoundingMode.HALF_UP);
            BigDecimal y = new BigDecimal(event.values[1]).setScale(3, RoundingMode.HALF_UP);
            BigDecimal z = new BigDecimal(event.values[2]).setScale(3, RoundingMode.HALF_UP);

            gyroX.setText(x.toString() + " rad/s²");
            gyroY.setText(y.toString() + " rad/s²");
            gyroZ.setText(z.toString() + " rad/s²");

            gyroSeries.appendData(
                    new DataPoint(gyroPointCount, Math.sqrt(x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).doubleValue())),
                    true,
                    100);
            gyroPointCount++;
        }
    };

    public SensorEventListener acceleroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            BigDecimal x = new BigDecimal(event.values[0]).setScale(3, RoundingMode.HALF_UP);
            BigDecimal y = new BigDecimal(event.values[1]).setScale(3, RoundingMode.HALF_UP);
            BigDecimal z = new BigDecimal(event.values[2]).setScale(3, RoundingMode.HALF_UP);

            acceleroX.setText(x.toString() + " m/s²");
            acceleroY.setText(y.toString() + " m/s²");
            acceleroZ.setText(y.toString() + " m/s²");

            acceleroSeries.appendData(
                    new DataPoint(acceleroPointCount, Math.sqrt(x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).doubleValue())),
                    true,
                    100);
            acceleroPointCount++;
        }
    };

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(acceleroListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(gyroListener);
        sensorManager.unregisterListener(acceleroListener);
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
}
