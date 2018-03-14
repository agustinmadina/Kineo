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
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    SensorManager sensorManager;
    float[] gData = new float[3];
    private TextView xTextView;
    private TextView yTextView;
    private TextView zTextView;
    private int yInitialDegree = 0;
    private int yActualDegree;
    float yaw;
    float pitch;
    float roll;
    int x;
    int y;
    int z;

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
                Snackbar.make(view, "Set" + yActualDegree + "as initial degree (0)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                yInitialDegree = yActualDegree;
                yTextView.setText("0");
            }
        });
        Button stopMeasuringButton = findViewById(R.id.btn_stop_measuring);
        stopMeasuringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                zTextView.setText(roundUp((int) Math.round(Math.toDegrees(Math.atan((double) pitch / (double) roll))););
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
        // Register listener
        Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, asensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                yaw = (float) (gData[0] / 9.82);
                pitch = (float) (gData[1] / 9.82);
                roll = (float) (gData[2] / 9.82);

                x = (int) Math.round(Math.toDegrees(Math.atan((double) yaw / (double) pitch)));
                y = (int) Math.round(Math.toDegrees(Math.atan((double) pitch / (double) roll)));
                z = (int) Math.round(Math.toDegrees(Math.atan((double) roll / (double) yaw)));

                yActualDegree = y;
//                xTextView.setText("x: " + String.valueOf(roundUp(x)));
                yTextView.setText("y: " + String.valueOf(roundUp(y + yInitialDegree)));
//                zTextView.setText("z:" + String.valueOf(roundUp(z)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    int roundUp(int n) {
        return (n + 4) / 5 * 5;
    }
}
