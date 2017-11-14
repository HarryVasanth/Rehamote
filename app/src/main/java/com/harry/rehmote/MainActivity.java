/*
 * Written by Harry Vasanth (harry.vasanth@m-iti.org)
 * Copyright (c) 2017.
 * Please acknowledge the creator by giving him/her credit for the work
 */

package com.harry.rehmote;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener {
    private static final int SERVERPORT = 4444;
    //filename
    String recDate = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    // TextView
    private TextView txtOri, txtAcc, txtVerb;
    //the Sensor Manager
    private SensorManager sManager;
    private Sensor mSensor;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the TextView from the layout file
        txtOri = (TextView) findViewById(R.id.txtGyroData);
        txtAcc = (TextView) findViewById(R.id.txtAccData);
        txtVerb = (TextView) findViewById(R.id.txtVerbose);
        //get a hook to the sensor service
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        //sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        //runTcpServer();
    }

    //when this Activity starts
    @Override
    protected void onResume() {
        super.onResume();
        /*register the sensor listener to listen to the gyroscope sensor, use the
         * callbacks defined in this class, and gather the sensor information as
		 * quick as possible*/
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);

        //
        // runTcpServer();

    }

    //When this Activity isn't visible anymore
    @Override
    protected void onStop() {
        //unregister the sensor listener
        sManager.unregisterListener(this);
        super.onStop();
    }

    // connect wifi
    private void runTcpServer() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(SERVERPORT);
            Log.d("TcpServer", ss.getInetAddress() + "");
            //ss.setSoTimeout(10000);
            //accept connections
            Socket s = ss.accept();
            Log.i("TcpServer", "Receiving");
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            //receive a message
            Log.i("TcpServer", in.readLine());
            final String incomingMsg = in.readLine() + System.getProperty("line.separator");
            Log.i("TcpServer", "received: " + incomingMsg);
            runOnUiThread(new Runnable() {
                public void run() {
                    txtVerb.append("received: " + incomingMsg);
                }
            });

            s.close();
        } catch (InterruptedIOException e) {
            //if timeout occurs
            e.printStackTrace();
            Log.e("TcpServer", "" + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TcpServer", "" + e);
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("TcpServer", "" + e);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //Do nothing
    }

    public void writeToCsvGy(String x, String y, String z) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + "/rehamote");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success

            String csv = folder + "/gyro_" + recDate + ".csv";
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss.SSS").format(new Date());
            FileWriter file_writer = new FileWriter(csv, true);


            // file_writer.append("date,time,gyro_x,gyro_y,gyro_z");
            String s = timeStamp + "," + x + "," + y + "," + z + "\n";

            file_writer.append(s);
            file_writer.close();
        }
    }

    public void writeToCsv(String x, String y, String z) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + "/rehamote");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success
            String csv = folder + "/acc_" + recDate + ".csv";
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss.SSS").format(new Date());
            FileWriter file_writer = new FileWriter(csv, true);

            String s = timeStamp + "," + x + "," + y + "," + z + "\n";

            file_writer.append(s);
            file_writer.close();

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //runTcpServer();// test
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //TODO: get values

            //else it will output the Roll, Pitch and Yawn values
            txtAcc.setText("Acceleration X  : " + Float.toString(event.values[0]) + "\n" +
                    "Acceleration Y  : " + Float.toString(event.values[1]) + "\n" +
                    "Acceleration Z : " + Float.toString(event.values[2]));
            try {
                writeToCsv(Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //TODO: get values
            //else it will output the Roll, Pitch and Yawn values
            txtOri.setText("Orientation X (Roll) : " + Float.toString(event.values[0]) + "\n" +
                    "Orientation Y (Pitch) : " + Float.toString(event.values[1]) + "\n" +
                    "Orientation Z (Yaw) : " + Float.toString(event.values[2]));
            try {
                writeToCsvGy(Float.toString(event.values[0]), Float.toString(event.values[1]), Float.toString(event.values[2]));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void btnStop(View view) {
        onStop();
        this.finish();
        System.exit(0);
    }
}
