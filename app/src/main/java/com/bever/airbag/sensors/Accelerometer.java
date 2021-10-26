package com.bever.airbag.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Accelerometer {

    private SensorManager sensorManager;
    private Sensor sensor;
    private GraphView acceleroGraph;
    private LineGraphSeries<DataPoint> acceleroSeries;
    private int acceleroPointCount = 0;
    private SensorEventListener acceleroListener;

    public Accelerometer(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.acceleroSeries = new LineGraphSeries<>();
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

    public GraphView getAcceleroGraph() {
        return acceleroGraph;
    }

    public void setAcceleroGraph(GraphView acceleroGraph) {
        this.acceleroGraph = acceleroGraph;
    }

    public LineGraphSeries<DataPoint> getAcceleroSeries() {
        return acceleroSeries;
    }

    public void setAcceleroSeries(LineGraphSeries<DataPoint> acceleroSeries) {
        this.acceleroSeries = acceleroSeries;
    }

    public int getAcceleroPointCount() {
        return acceleroPointCount;
    }

    public void setAcceleroPointCount(int acceleroPointCount) {
        this.acceleroPointCount = acceleroPointCount;
    }

    public SensorEventListener getAcceleroListener() {
        return acceleroListener;
    }

    public void setAcceleroListener(SensorEventListener acceleroListener) {
        this.acceleroListener = acceleroListener;
    }
}
