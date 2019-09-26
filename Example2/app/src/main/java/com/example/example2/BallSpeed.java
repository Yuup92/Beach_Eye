package com.example.example2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static java.lang.Math.abs;

public class BallSpeed extends Activity {
    /**
     * The sensor manager object.
     */
    private SensorManager sensorManager;
    /**
     * The accelerometer.
     */
    private Sensor accelerometer;
    private int sensorTypeAcc = Sensor.TYPE_ACCELEROMETER;

    /**
     * The gyroscope.
     */
    private Sensor gyroscope;
    /**
     * Accelerometer values
     */
    private float aX = 0;
    private float aY = 0;
    private float aZ = 0;
    private float prevAX = 0;
    private float prevAY = 0;
    private float prevAZ = 0;
    private float impactAX = 0;
    private float impactAY = 0;
    private float impactAZ = 0;
    private boolean foundImpactAcc = false;
    private long impactTimeA = 0;
    /**
     * Gyro values
     */
    private float gX = 0;
    private float gY = 0;
    private float gZ = 0;
    private float prevGX = 0;
    private float prevGY = 0;
    private float prevGZ = 0;
    private float impactGX = 0;
    private float impactGY = 0;
    private float impactGZ = 0;
    private boolean foundImpactG = false;
    private boolean swingDetectedG = false;
    private long impactTimeG = 0;
    private int impactDetected = 0;

    private int swingThreshold = 30; // 6*g
    private int swingThresholdGyro = 10;
    /**
     * Ball hit detection
     */
    private int swingDetected = 0;
    private long swingDelay = 400;
    private long timeSwingDetected = 0;

    private Button buttonMainMenu, buttonReset;
    private EditText editD, editK;
    private TextView textHighScore;

    private double speed = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball_speed);

        buttonMainMenu = (Button)  findViewById(R.id.buttonMainMenu);
        buttonReset = (Button) findViewById(R.id.buttonReset);

        editD = (EditText) findViewById(R.id.editD);
        editK = (EditText) findViewById(R.id.editK);

        textHighScore = (TextView) findViewById(R.id.textHighScore);

        initiate_sensors();

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();

                Intent mainActivity = new Intent(context, MainActivity.class);
                startActivity(mainActivity);
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = String.format("Detected Speed: %.2f [km/h]", speed);
                textHighScore.setText(text);
            }
        });

    }

    public void initiate_sensors() {
        // Set the sensor manager
        Context context = getApplicationContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // Check if event is of type accelerometer or gyroscope
                if(event.sensor.getType() == sensorTypeAcc) {
                    accSensor(event);
                } else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    gyroSensor(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor event, int i) {

            }
        };

        // if the default accelerometer exists
        if (sensorManager.getDefaultSensor(sensorTypeAcc) != null) {
            // set accelerometer
            accelerometer = sensorManager
                    .getDefaultSensor(sensorTypeAcc);
            // register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            // No accelerometer!
        }

        // if the default gyroscope exists
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // set gyroscope
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            // Register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorManager.registerListener(sensorEventListener, gyroscope, sensorManager.SENSOR_DELAY_FASTEST);
        } else {
            // No Gyroscope found
        }
    }

    public void accSensor(SensorEvent event) {
        // get the the x,y,z values of the accelerometer
        aX = event.values[0];
        aY = event.values[1];
        aZ = event.values[2];

        long curTime = System.currentTimeMillis();

        long timDiff = System.currentTimeMillis() - timeSwingDetected;
        if(timDiff > (swingDelay)) {
            foundImpactAcc = false;
        }

//      Stroke detection
        if(abs(aY) >= swingThreshold && !foundImpactAcc) {
            if(timDiff > (swingDelay)) {

                timeSwingDetected = System.currentTimeMillis();
                impactDetected++;
                foundImpactAcc = true;

            }
        }

        prevAX = abs(aX);
        prevAY = abs(aY);
        prevAZ = abs(aZ);
    }

    public void gyroSensor(SensorEvent event) {
        gX = event.values[0];
        gY = event.values[1];
        gZ = event.values[2];

        long timDiff = System.currentTimeMillis() - timeSwingDetected;
        if(timDiff > (swingDelay)) {
            foundImpactG = false;
            swingDetectedG = false;
        }

//      Stroke detection
        if(abs(gX) >= swingThresholdGyro || swingDetectedG) {
            swingDetectedG = true;
            if(prevGX >= abs(gX) && !foundImpactG) {
                impactGX = prevGX;
                impactGY = prevGY;
                impactGZ = prevGZ;
                foundImpactG = true;
                speed = abs(gX*Float.parseFloat(editD.getText().toString())/10 + Float.parseFloat(editK.getText().toString()));
                String text = String.format("Detected Speed: %.2f [km/h]", speed);
                textHighScore.setText(text);
            }
        }

        prevGX = abs(gX);
        prevGY = abs(gY);
        prevGZ = abs(gZ);
    }


}
