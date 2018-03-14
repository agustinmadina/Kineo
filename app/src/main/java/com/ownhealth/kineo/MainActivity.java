package com.ownhealth.kineo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private static final String TAG = "rotation";
    private static final int DEG = 10;

    SensorManager sensorManager;
    float[] gData = new float[3];           // Gravity or accelerometer
    float[] mData = new float[3];           // Magnetometer
    float[] orientation = new float[3];
    float[] Rmat = new float[9];
    float[] R2 = new float[9];
    float[] Imat = new float[9];
    boolean haveGrav = false;
    boolean haveAccel = false;
    boolean haveMag = false;
    private TextView xTextView;
    private TextView yTextView;
    private TextView zTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Get the sensor manager from system services
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        xTextView = findViewById(R.id.x_container);
        yTextView = findViewById(R.id.y_container);
        zTextView = findViewById(R.id.z_container);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register our listeners
//        Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        Sensor msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, asensor, SensorManager.SENSOR_DELAY_GAME);
//        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] data;
        switch (event.sensor.getType()) {
//            case Sensor.TYPE_GRAVITY:
//                gData[0] = event.values[0];
//                gData[1] = event.values[1];
//                gData[2] = event.values[2];
//                haveGrav = true;
//                break;
            case Sensor.TYPE_ACCELEROMETER:
                if (haveGrav) break;    // don't need it, we have better
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                haveAccel = true;
                break;
//            case Sensor.TYPE_MAGNETIC_FIELD:
//                mData[0] = event.values[0];
//                mData[1] = event.values[1];
//                mData[2] = event.values[2];
//                haveMag = true;
//                break;
            default:
                return;
        }

//        if ((haveGrav || haveAccel) && haveMag) {
//            SensorManager.getRotationMatrix(Rmat, Imat, gData, mData);
//            SensorManager.remapCoordinateSystem(Rmat,
//                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);
//            // Orientation isn't as useful as a rotation matrix, but
//            // we'll show it here anyway.
//            SensorManager.getOrientation(R2, orientation);
//            float incl = SensorManager.getInclination(Imat);
        float yaw = (float) (gData[0] / 9.82);
        float pitch = (float) (gData[1] / 9.82);
        float roll = (float) (gData[2] / 9.82);

        double x = Math.round(Math.toDegrees(Math.atan((double) yaw / (double) pitch)));
        double y = Math.round(Math.toDegrees(Math.atan((double) pitch / (double) roll)));
        double z = Math.round(Math.toDegrees(Math.atan((double) roll / (double) yaw)));

        xTextView.setText(String.valueOf(x));
        yTextView.setText(String.valueOf(y));
        zTextView.setText(String.valueOf(z));

        //            Log.d(TAG, "yaw: " + (int)(orientation[0]*DEG));
//            Log.d(TAG, "pitch: " + (int)(orientation[1]*DEG));
//            Log.d(TAG, "roll: " + (int)(orientation[2]*DEG));
//            Log.d(TAG, "yaw: " + (int)(orientation[0]*DEG));
//            Log.d(TAG, "inclination: " + (int)(incl*DEG));
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
