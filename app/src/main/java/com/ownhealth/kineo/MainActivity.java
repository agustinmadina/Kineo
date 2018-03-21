package com.ownhealth.kineo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import static android.view.View.VISIBLE;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    SensorManager sensorManager;
    float[] gData = new float[3];
    private Toolbar toolbar;
    private TextView yActualTextView;
    private TextView yMeasuredActualTextView;
    private TextView yMeasuredFinalTextView;
    private float pitch;
    private float pitchInitial;
    private int y;
    private int yInitialDegree = 1;
    private int measuredAngle;
    private boolean isMeasuring = false;
    private boolean clockwise;
    private boolean clockwiseIsSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = setUpToolbar();
        yActualTextView = findViewById(R.id.y_actual);
        yMeasuredActualTextView = findViewById(R.id.y_measured_actual);
        yMeasuredFinalTextView = findViewById(R.id.y_measured_final);
        final Button stopMeasuringButton = findViewById(R.id.btn_stop_measuring);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Set" + y + "as initial degree (0)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                yInitialDegree = y;
                pitchInitial = pitch;
                yMeasuredActualTextView.setText("0");
                yMeasuredActualTextView.setVisibility(VISIBLE);
                stopMeasuringButton.setVisibility(VISIBLE);
                isMeasuring = true;
                clockwiseIsSet = false;
            }
        });
        stopMeasuringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yMeasuredFinalTextView.setText(measuredAngle);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Get the sensor manager from system services
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    private Toolbar setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register listener
        Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, asensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                float yaw = (float) (gData[0] / 9.82);
                pitch = (float) (gData[1] / 9.82);
                float roll = (float) (gData[2] / 9.82);
                int x = (int) Math.round(Math.toDegrees(Math.atan((double) yaw / (double) pitch)));
                y = (int) Math.round(Math.toDegrees(Math.atan((double) pitch / (double) roll)));
                int z = (int) Math.round(Math.toDegrees(Math.atan((double) roll / (double) yaw)));
                yActualTextView.setText(String.valueOf((y)));
                if (isMeasuring) {
                    if (!clockwiseIsSet && (Math.signum(y) != Math.signum(yInitialDegree))) {
                        //cambio cuadrante y chequeo para que lado segun donde este
                        if (((Math.signum(yInitialDegree) == -1) && (Math.signum(pitch) == Math.signum(pitchInitial))) || ((Math.signum(yInitialDegree) == 1) && (Math.signum(pitch) != Math.signum(pitchInitial)))) {
                            clockwise = true;
                            clockwiseIsSet = true;
                        } else {
                            clockwise = false;
                            clockwiseIsSet = true;
                        }
                    }
                }

                if (Math.signum(y) == Math.signum(yInitialDegree) && (Math.signum(pitch) == (Math.signum(pitchInitial)))) {
                    //Less than 90° turn
                    measuredAngle = abs(y - yInitialDegree);
                    yMeasuredActualTextView.setText(String.valueOf(measuredAngle));
                } else if (Math.signum(y) != Math.signum(yInitialDegree) && (Math.signum(pitch) == (Math.signum(pitchInitial)) && measuredAngle < 270)) {
                    //Between 90° and 180° turn, Quarter next to it, both up or down
                    measuredAngle = 180 - abs(y) - abs(yInitialDegree);
                    yMeasuredActualTextView.setText(String.valueOf(measuredAngle));
                } else if (Math.signum(y) == Math.signum(yInitialDegree)) {
                    if ((Math.signum(y) == 1 && clockwise) || (Math.signum(y) == -1 && !clockwise)) { //TODO
                        //Between 180° and 270° turn
                        measuredAngle = 180 - abs(y) + abs(yInitialDegree);
                        yMeasuredActualTextView.setText(String.valueOf(measuredAngle));
                    } else {
                        //Between 180° and 270° turn
                        measuredAngle = 180 + abs(y) - abs(yInitialDegree);
                        yMeasuredActualTextView.setText(String.valueOf(measuredAngle));
                    }
                } else if (Math.signum(pitch) != (Math.signum(pitchInitial))) {
                    //Between 90° and 180° turn, both left or right //TODO
                    measuredAngle = abs(yInitialDegree) + abs(y);
                    yMeasuredActualTextView.setText(String.valueOf(measuredAngle));
                } else {
                    //Between 270° and 360° turn
                    measuredAngle = 360 - abs(y) - abs(yInitialDegree);
                    yMeasuredActualTextView.setText(measuredAngle);
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    int roundUp(int n) {
        return (n + 4) / 5 * 5;
    }
}
