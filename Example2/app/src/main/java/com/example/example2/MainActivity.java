package com.example.example2;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Math.abs;

/**
 * Built from the Smart Phone Sensing Example 2. Working with sensors.
 */
public class MainActivity extends Activity implements SensorEventListener {

    /**
     * The sensor manager object.
     */
    private SensorManager sensorManager;
    /**
     * The accelerometer.
     */
    private Sensor accelerometer;
    /**
     * The gyroscope.
     */
    private Sensor gyroscope;
    /**
     * The wifi manager.
     */
    private WifiManager wifiManager;
    /**
     * The wifi info.
     */
    private WifiInfo wifiInfo;
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

    private float mX = 0;
    private float mY = 0;
    private float mZ = 0;
    private int sensorTypeAcc = Sensor.TYPE_ACCELEROMETER;
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

    /**
     * Data collection bool
     */
    private boolean dataCollection = false;
    private boolean dataSaved = false;
    private long startTime = 0;
    private int recordAmount = 0;
    private static int RECORDTIME = 2000; //ms
    String dataAcc = "";
    String dataGyro = "";
    /**
     * Ball hit detection
     */
    private int MAX_DATA_POINTS = 1200;
    private int swingThreshold = 50; // 6*g
    private int swingThresholdGyro = 10;
    private double aXArray[] = new double[MAX_DATA_POINTS];
    private double aYArray[] = new double[MAX_DATA_POINTS];
    private double aZArray[] = new double[MAX_DATA_POINTS];

    private double gXArray[] = new double[MAX_DATA_POINTS];
    private double gYArray[] = new double[MAX_DATA_POINTS];
    private double gZArray[] = new double[MAX_DATA_POINTS];
    private int swingDetected = 0;
    private long swingDelay = 400;
    private long timeSwingDetected = 0;
     /**
     * Text fields to show the sensor values.
     */
    private TextView currentX, currentY, currentZ,
            maxX, maxY, maxZ,
            gyroX, gyroY, gyroZ,
            titleAcc, textRssi;
    /**
     * EditText Fields
     */
    private EditText  ptsv2;
    /**
     * Button fields
     */
    private Button buttonRssi, buttonRest, buttonRecord,
                    buttonSwitchToCounter, buttonnSwitchToBallSpeed;
    /**
     * For save File
     */
    private static final String FILE_NAME = "example.txt";

    /**
     * To make a post online
     */
    OkHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the text views.
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        gyroX = (TextView) findViewById(R.id.gyroX);
        gyroY = (TextView) findViewById(R.id.gyroY);
        gyroZ = (TextView) findViewById(R.id.gyroZ);

        titleAcc = (TextView) findViewById(R.id.titleAcc);
        textRssi = (TextView) findViewById(R.id.textRSSI);

        buttonRest = (Button) findViewById(R.id.buttonResetMax);
        buttonRecord = (Button) findViewById(R.id.buttonCollectData);

        buttonnSwitchToBallSpeed  = (Button) findViewById(R.id.buttonSwitchToBallSpeed);
        buttonSwitchToCounter = (Button) findViewById(R.id.buttonSwitchToCounter);

        // Create editText
        ptsv2 = (EditText) findViewById(R.id.editWebLink);

        // Set the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);



        // if the default accelerometer exists
        if (sensorManager.getDefaultSensor(sensorTypeAcc) != null) {
            // set accelerometer
            accelerometer = sensorManager
                    .getDefaultSensor(sensorTypeAcc);
            // register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            // No accelerometer!
        }

        // if the default gyroscope exists
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // set gyroscope
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            // Register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorManager.registerListener(this, gyroscope, sensorManager.SENSOR_DELAY_FASTEST);
        } else {
            // No Gyroscope found
        }

//        maxX.setText(String.format("%.2f", accelerometer.getMaximumRange()));
//        gyroX.setText(String.format("%.2f", gyroscope.getMaximumRange()));

        buttonSwitchToCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();

                Intent ballCounter = new Intent(context, BallCounter.class);
                startActivity(ballCounter);

            }
        });

        buttonnSwitchToBallSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();

                Intent ballCounter = new Intent(context, BallSpeed.class);
                startActivity(ballCounter);

            }
        });

//        // Create a click listener for our button.
//        buttonRssi.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!dataCollection) {
//                    startTime = System.currentTimeMillis();
//                    dataCollection = true;
//                    dataSaved = false;
//                    dataAcc = "";
//                    dataGyro = "";
//                }
//            }
//        });

        buttonRest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                maxX.setText("0.0");
                maxY.setText("0.0");
                maxZ.setText("0.0");
                swingDetected = 0;
                String swing = String.format("Swing's Detected: %d", swingDetected);
                textRssi.setText(swing);
            }
        });

        buttonRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                CharSequence text;
                if(isExternalStorageWritable()) {
                    text = "true";
                } else {
                    text = "false";
                }
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });



    }

    // onResume() registers the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // onPause() unregisters the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing.
}

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Check if event is of type accelerometer or gyroscope
        if(event.sensor.getType() == sensorTypeAcc) {
            accSensor(event);
        } else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroSensor(event);
        }
    }

    public void accSensor(SensorEvent event) {
        // get the the x,y,z values of the accelerometer
        aX = event.values[0];
        aY = event.values[1];
        aZ = event.values[2];

        // display the current x,y,z accelerometer values
        currentX.setText(Float.toString(aX));
        currentY.setText(Float.toString(aY));
        currentZ.setText(Float.toString(aZ));

        long curTime = System.currentTimeMillis();
        long time = curTime - startTime;
        if(dataCollection &&
                (time < RECORDTIME)) {
            String res = String.format("%d,%.2f,%.2f,%.2f\n", time, aX, aY, aZ);
            dataAcc += res;
        } else {
            dataCollection = false;
            if(!dataSaved) {
                save();
                dataSaved = true;
            }
        }

        long timDiff = System.currentTimeMillis() - timeSwingDetected;
        if(timDiff > (swingDelay)) {
            foundImpactAcc = false;
        }

//        Stroke detection
        if(abs(aY) >= swingThreshold && !foundImpactAcc) {
            if(prevAY >= abs(aY)) {
                    impactAX = prevAX;
                    impactAY = prevAY;
                    impactAZ = prevAZ;
                    foundImpactAcc = true;
                    impactTimeA = System.currentTimeMillis() - startTime;
            }
            if(timDiff > (swingDelay)) {
                timeSwingDetected = System.currentTimeMillis();
                swingDetected++;
                String swing = String.format("Swing's Detected: %d", swingDetected);
                textRssi.setText(swing);

                //postOnline();
            }
        }

        prevAX = abs(aX);
        prevAY = abs(aY);
        prevAZ = abs(aZ);

        float curMaxX = Float.parseFloat(maxX.getText().toString());
        float curMaxY = Float.parseFloat(maxY.getText().toString());
        float curMaxZ = Float.parseFloat(maxZ.getText().toString());

        if(abs(curMaxX) < abs(aX)) {
            maxX.setText(String.format("%.2f", abs(aX)));
        }

        if(abs(curMaxY) < abs(aY)) {
            maxY.setText(String.format("%.2f", abs(aY)));
        }

        if(abs(curMaxZ) < abs(aZ)) {
            maxZ.setText(String.format("%.2f", abs(aZ)));
        }

        if ((abs(aX) > abs(aY)) && (abs(aX) > abs(aZ))) {
            titleAcc.setTextColor(Color.RED);
        }
        if ((abs(aY) > abs(aX)) && (abs(aY) > abs(aZ))) {
            titleAcc.setTextColor(Color.BLUE);
        }
        if ((abs(aZ) > abs(aY)) && (abs(aZ) > abs(aX))) {
            titleAcc.setTextColor(Color.GREEN);
        }
    }

    public void gyroSensor(SensorEvent event) {
        gyroX.setText("0.0");
        gyroY.setText("0.0");
        gyroZ.setText("0.0");

        gX = event.values[0];
        gY = event.values[1];
        gZ = event.values[2];

        gyroX.setText(String.format("%.2f", gX));
        gyroY.setText(String.format("%.2f", gY));
        gyroZ.setText(String.format("%.2f", gZ));

        long curTime = System.currentTimeMillis();
        long time = curTime - startTime;
        if(dataCollection &&
                (time < RECORDTIME)) {
            String res = String.format("%d,%.2f,%.2f,%.2f\n", time, gX, gY, gZ);
            dataGyro += res;
        } else {
            dataCollection = false;
            if(!dataSaved) {
                save();
                dataSaved = true;
            }
        }

        long timDiff = System.currentTimeMillis() - timeSwingDetected;
        if(timDiff > (swingDelay)) {
            foundImpactG = false;
            swingDetectedG = false;
        }

//        Stroke detection
        if(abs(gX) >= swingThresholdGyro || swingDetectedG) {
            swingDetectedG = true;
            if(prevGX >= abs(gX) && !foundImpactG) {
                impactGX = prevGX;
                impactGY = prevGY;
                impactGZ = prevGZ;
                foundImpactG = true;
                impactTimeG = System.currentTimeMillis() - startTime;
            }
        }

        prevGX = abs(gX);
        prevGY = abs(gY);
        prevGZ = abs(gZ);
    }

    /**
     * Checks to see if I am able to write to external storage
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void save() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        String accFile = "accel" + String.format("_%d.txt", recordAmount);
        File file = new File(dir, accFile);

        //Write to file
        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.append(dataAcc);

        } catch (IOException e) {
            //Handle exception
        }

        String gyroFile = "gyro" + String.format("_%d.txt", recordAmount);
        File fileG = new File(dir, gyroFile);

        //Write to file
        try (FileWriter fileWriter = new FileWriter(fileG)) {
            fileWriter.append(dataGyro);
        } catch (IOException e) {
            //Handle exception
        }

        String impactFile = "impact" + String.format("_%d.txt", recordAmount);
        File fileI = new File(dir, impactFile);

        String data = String.format("%d,%.2f,%.2f,%.2f\n%d,%.2f,%.2f,%.2f\n", impactTimeA, impactAX, impactAY, impactAZ, impactTimeG, impactGX, impactGY, impactGZ);

        try (FileWriter fileWriter = new FileWriter(fileI)) {
            fileWriter.append(data);
        } catch (IOException e) {
            //Handle exception
        }

        recordAmount++;

        int duration = Toast.LENGTH_LONG;

        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "Data Recorded", duration);
        toast.show();

        String swing = String.format("Recording has finished: %d", recordAmount);
        textRssi.setText(swing);
    }

}