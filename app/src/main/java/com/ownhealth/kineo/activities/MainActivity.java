package com.ownhealth.kineo.activities;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ownhealth.kineo.R;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * Created by Agustin Madina on 3/26/2018.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private static final String Y_AXIS = "y_axis";
    private static final String X_AXIS = "x_axis";

    private SensorManager sensorManager;
    float[] gData = new float[3];
    private TextView yActualTextView;
    private TextView xActualTextView;
    private TextView zActualTextView;
    private TextView actualDegreeTextView;
    private TextView finalDegreeTextView;
    private FloatingActionButton fabStartStop;
    private FloatingActionButton fabChangeAxis;
    private float yaw;
    private float pitch;
    private int y;
    private int x;
    private int z;
    private int axisMeasured;
    private float referenceAxis;
    private String axisBeingMeasured = Y_AXIS;
    private float referenceInitial;
    private int initialDegree = 1;
    private int lastQuarterDegree;
    private float lastQuarterReference;
    private int measuredAngle;
    private int lastFivePointer = 0;
    private boolean isMeasuring = false;
    private boolean clockwise;
    private boolean isClockwiseSet = false;
    private ArrayList<Integer> lastFiveList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbarAndDrawer();
        yActualTextView = findViewById(R.id.y_actual);
        xActualTextView = findViewById(R.id.x_actual);
        zActualTextView = findViewById(R.id.z_actual);
        actualDegreeTextView = findViewById(R.id.measured_actual);
        finalDegreeTextView = findViewById(R.id.measured_final);
        fabStartStop = findViewById(R.id.fab_start_stop);
        fabStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMeasuring) {
                    finishMeasureClick();
                } else {
                    startMeasureClick(view);
                }
                isMeasuring = !isMeasuring;
            }
        });
        fabChangeAxis = findViewById(R.id.fab_change_axis);
        fabChangeAxis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (axisBeingMeasured.equals(Y_AXIS)) {
                    axisBeingMeasured = X_AXIS;
                } else {
                    axisBeingMeasured = Y_AXIS;
                }
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.change_axis_being_measured), axisBeingMeasured), Toast.LENGTH_LONG).show();
            }
        });
        // Get the sensor manager from system services
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    private void startMeasureClick(View view) {
        initialDegree = axisMeasured;
        lastQuarterDegree = axisMeasured;
        referenceInitial = referenceAxis;
        lastQuarterReference = referenceAxis;
        actualDegreeTextView.setText("0°");
        actualDegreeTextView.setVisibility(VISIBLE);
        finalDegreeTextView.setVisibility(View.INVISIBLE);
        fabStartStop.setImageResource(R.drawable.ic_media_pause);
        fabChangeAxis.setVisibility(GONE);
        Snackbar.make(view, "Set " + axisMeasured + " as initial degree", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void finishMeasureClick() {
        fillLastFive(measuredAngle);
        finalDegreeTextView.setVisibility(VISIBLE);
        finalDegreeTextView.setText(format(getResources().getString(R.string.final_degree_measured), measuredAngle));
        actualDegreeTextView.setText(getString(R.string.ready_to_measure));
        referenceInitial = 0;
        initialDegree = 1;
        lastQuarterDegree = 0;
        lastQuarterReference = 0;
        measuredAngle = 0;
        fabStartStop.setImageResource(R.drawable.ic_media_play);
        fabChangeAxis.setVisibility(VISIBLE);
    }

    private void fillLastFive(int measuredAngle) {
        LinearLayout lastFiveLayout = findViewById(R.id.last_5_container);
        TextView lastFiveLastTextView = findViewById(R.id.last_5_1);
        TextView lastFive2TextView = findViewById(R.id.last_5_2);
        TextView lastFive3TextView = findViewById(R.id.last_5_3);
        TextView lastFive4TextView = findViewById(R.id.last_5_4);
        TextView lastFive5TextView = findViewById(R.id.last_5_5);
        lastFiveLayout.setVisibility(VISIBLE);
        lastFiveList.add(measuredAngle);
        lastFiveLastTextView.setText(format(getString(R.string.last_5_1), lastFiveList.get(lastFivePointer)));
        lastFive2TextView.setText(lastFiveList.size() >= 2 ? format(getString(R.string.last_5_2), lastFiveList.get(lastFivePointer - 1)) : "");
        lastFive3TextView.setText(lastFiveList.size() >= 3 ? format(getString(R.string.last_5_3), lastFiveList.get(lastFivePointer - 2)) : "");
        lastFive4TextView.setText(lastFiveList.size() >= 4 ? format(getString(R.string.last_5_4), lastFiveList.get(lastFivePointer - 3)) : "");
        lastFive5TextView.setText(lastFiveList.size() >= 5 ? format(getString(R.string.last_5_5), lastFiveList.get(lastFivePointer - 4)) : "");
        lastFivePointer++;
    }

    private void setUpToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, 5000000);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                yaw = (float) (gData[0] / 9.82);
                pitch = (float) (gData[1] / 9.82);
                float roll = (float) (gData[2] / 9.82);
                x = (int) Math.round(Math.toDegrees(Math.atan((double) yaw / (double) pitch)));
                y = (int) Math.round(Math.toDegrees(Math.atan((double) pitch / (double) roll)));
                z = (int) Math.round(Math.toDegrees(Math.atan((double) roll / (double) yaw)));

                yActualTextView.setText(valueOf((y)));
                xActualTextView.setText(valueOf((x)));
                zActualTextView.setText(valueOf((z)));

                if (axisBeingMeasured.equals(Y_AXIS)) {
                    axisMeasured = y;
                    referenceAxis = pitch;
                } else {
                    axisMeasured = x;
                    referenceAxis = yaw;
                }

                if (isMeasuring && axisMeasured != 0 && abs(axisMeasured) != 90) {
                    evaluateIfClockwise();
                    //problema si initial 0, no entra en este al no tener signo
                    if ((Math.signum(axisMeasured) == Math.signum(initialDegree) && (Math.signum(referenceAxis) == (Math.signum(referenceInitial)))) && measuredAngle < 90) {
                        //Less than 90° turn
                        measuredAngle = abs(axisMeasured - initialDegree);
                        isClockwiseSet = false;
                    } else if (Math.signum(axisMeasured) == Math.signum(initialDegree) && (Math.signum(referenceAxis) == (Math.signum(referenceInitial)))) {
                        //Has already done an entire turn as is on the same quarter as the initial degree
                        actualDegreeTextView.setText("Maximum reached");
                        break;
                    } else if (Math.signum(axisMeasured) != Math.signum(initialDegree) && (Math.signum(referenceAxis) == (Math.signum(referenceInitial)) && measuredAngle < 180)) {
                        //Between 90° and 180° turn, Quarter next to it, both up or down
                        measuredAngle = 180 - abs(axisMeasured) - abs(initialDegree);
                    } else if (Math.signum(axisMeasured) == Math.signum(initialDegree)) {
                        if ((Math.signum(axisMeasured) == 1 && clockwise) || (Math.signum(axisMeasured) == -1 && !clockwise)) {
                            //Between 180° and 270° turn
                            measuredAngle = 180 - abs(axisMeasured) + abs(initialDegree);
                        } else {
                            //Between 180° and 270° turn
                            measuredAngle = 180 + abs(axisMeasured) - abs(initialDegree);
                        }
                    } else if ((Math.signum(referenceAxis) != (Math.signum(referenceInitial)) && measuredAngle < 180)) {
                        //Between 90° and 180° turn, both left or right
                        measuredAngle = abs(initialDegree) + abs(axisMeasured);
                    } else if ((Math.signum(referenceAxis) == Math.signum(referenceInitial)) && measuredAngle < 270) {
                        //Between 270° and 360° turn, up or down
                        measuredAngle = 180 + abs(axisMeasured) + abs(initialDegree);
                    } else {
                        //Between 270° and 360° turn, right or left
                        measuredAngle = 360 - abs(axisMeasured) - abs(initialDegree);
                    }
                    actualDegreeTextView.setText(format(getResources().getString(R.string.actual_degree_measuring), measuredAngle));
                }
        }
    }

    private void evaluateIfClockwise() {
        if (Math.signum(axisMeasured) != Math.signum(lastQuarterDegree) && measuredAngle < 90) {
            //cambio cuadrante y chequeo para que lado segun los signos de Y y pitch anteriores y actuales, solo setea en primero cuadrante
            if (!isClockwiseSet) {
                clockwise = ((Math.signum(lastQuarterDegree) == -1) && (Math.signum(referenceAxis) == Math.signum(lastQuarterReference))) || ((Math.signum(lastQuarterDegree) == 1) && (Math.signum(referenceAxis) != Math.signum(lastQuarterReference)));
                isClockwiseSet = true;
                lastQuarterDegree = axisMeasured;
                lastQuarterReference = referenceAxis;
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
