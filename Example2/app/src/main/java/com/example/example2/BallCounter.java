package com.example.example2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Math.abs;

public class BallCounter extends Activity  {

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
    private long swingDelay = 2000;
    private long timeSwingDetected = 0;

    private Button buttonMainMenu, buttonStart;
    private EditText editWebLink;
    private TextView textHighScore;

    /**
     * To make a post online
     */
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball_counter);

        buttonMainMenu = (Button)  findViewById(R.id.buttonMainMenu);
        buttonStart = (Button)  findViewById(R.id.buttonStart);
        editWebLink = (EditText) findViewById(R.id.editWebLink);
        textHighScore = (TextView) findViewById(R.id.textHighScore);

        // Instantiate the RequestQueue with the cache and network.
        client = new OkHttpClient();

        initiate_sensors();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                impactDetected = 0;
                String text = String.format("High Score\n%d", impactDetected);
                textHighScore.setText(text);
            }
        });

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();

                Intent mainActivity = new Intent(context, MainActivity.class);
                startActivity(mainActivity);
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
                postOnline();
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
            }
        }

        prevGX = abs(gX);
        prevGY = abs(gY);
        prevGZ = abs(gZ);
    }

    public void postOnline() {
        String url = editWebLink.getText().toString();
        url += "/post";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("somParam", "someValue")
                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();

        // This runs on a side thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String text = String.format("High Score\n%d", impactDetected);
                    textHighScore.setText(text);
                }
            }
        });
    }
}
