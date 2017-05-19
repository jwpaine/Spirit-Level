package marketplace.spiritlevel2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;
import android.graphics.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Level extends Activity implements View.OnTouchListener {
    DrawView drawView;
    SensorManager sensorManager;
    Sensor gyroscopeSensor;
    SensorEventListener gyroscopeSensorListener;
    Sensor rotationVectorSensor;
    SensorEventListener rvListener;
    float[] orientations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.argb(255, 0, 0, 0   ));
        setContentView(drawView);

        orientations = new float[3];

        drawView.setOnTouchListener(this);

        //get a hook to the sensor service and register gyro listener
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


        // Create a listener
        rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // More code goes here
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, sensorEvent.values);

                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,

                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);

                // Convert to orientations
                SensorManager.getOrientation(remappedRotationMatrix, orientations);
                for(int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // Register it
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);


        // schedule reaccuring task to update orientations
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //Schedule a task to run every 5 seconds (or however long you want)
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Do stuff here!

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Do stuff to update UI here!
                        updateOrientations();
                    }
                });

            }
        }, 0, 100, TimeUnit.MILLISECONDS); // or .MINUTES, .HOURS etc.
    }

    public void updateOrientations() {
        drawView.setOrientation(this.orientations);
    }


    /* onTouch event handler to facilitate pausing the graph */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                drawView.pause();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            default:
                break;
        }
        return true;
    }
}