package com.bever.airbag.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Gyroscope {

    private SensorManager sensorManager;
    private Sensor sensor;
    private GraphView gyroGraph;
    private LineGraphSeries<DataPoint> gyroSeries;
    private int gyroPointCount = 0;
    private SensorEventListener gyroListener;

    public Gyroscope(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.gyroSeries = new LineGraphSeries<>();
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public GraphView getGyroGraph() {
        return gyroGraph;
    }

    public void setGyroGraph(GraphView gyroGraph) {
        this.gyroGraph = gyroGraph;
    }

    public LineGraphSeries<DataPoint> getGyroSeries() {
        return gyroSeries;
    }

    public void setGyroSeries(LineGraphSeries<DataPoint> gyroSeries) {
        this.gyroSeries = gyroSeries;
    }

    public int getGyroPointCount() {
        return gyroPointCount;
    }

    public void setGyroPointCount(int gyroPointCount) {
        this.gyroPointCount = gyroPointCount;
    }

    public SensorEventListener getGyroListener() {
        return gyroListener;
    }

    public void setGyroListener(SensorEventListener gyroListener) {
        this.gyroListener = gyroListener;
    }
}
