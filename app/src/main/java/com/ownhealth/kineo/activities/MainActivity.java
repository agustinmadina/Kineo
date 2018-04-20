package com.ownhealth.kineo.activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Measure.LocalMeasureRepository;
import com.ownhealth.kineo.persistence.Measure.Measure;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.viewmodel.MeasuresViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.lang.String.format;

/**
 * Created by Agustin Madina on 3/26/2018.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private SensorManager sensorManager;

    @BindView(R.id.measured_final)
    TextView finalDegreeTextView;
    @BindView(R.id.measured_actual)
    TextView actualDegreeTextView;
    @BindView(R.id.last_5_1)
    TextView lastFiveLastTextView;
    @BindView(R.id.last_5_2)
    TextView lastFive2TextView;
    @BindView(R.id.last_5_3)
    TextView lastFive3TextView;
    @BindView(R.id.last_5_4)
    TextView lastFive4TextView;
    @BindView(R.id.last_5_5)
    TextView lastFive5TextView;
    @BindView(R.id.fab_start_stop)
    FloatingActionButton fabStartStop;
    @BindView(R.id.fab_change_axis)
    FloatingActionButton fabChangeAxis;
    @BindView(R.id.sp_joints)
    Spinner jointSpinner;
    @BindView(R.id.sp_movements)
    Spinner movementSpinner;
    @BindView(R.id.last_5_container)
    LinearLayout lastFiveLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private MeasuresViewModel mMeasuresViewModel;
    private Patient mActualPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbarAndDrawer();
        MeasuresViewModel.Factory factory = new MeasuresViewModel.Factory(getApplication(), new LocalMeasureRepository(JointDatabase.getInstance(getApplication()).measureDao()));
        mMeasuresViewModel = ViewModelProviders.of(this, factory).get(MeasuresViewModel.class);
        subscribeUi();
        setupSpinners();

        // Get the sensor manager from system services
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @OnClick(R.id.fab_start_stop)
    public void fabStartStopClick() {
        updateMeasuringUi(mMeasuresViewModel.isMeasuring());
        mMeasuresViewModel.fabStartStopClick();
    }

    @OnClick(R.id.fab_change_axis)
    public void fabChangeAxisClick() {
        mMeasuresViewModel.changeAxisClick();
        Toast.makeText(getApplicationContext(), String.format(getString(R.string.change_axis_being_measured), mMeasuresViewModel.getAxisBeingMeasured()), Toast.LENGTH_LONG).show();
    }

    private void setupSpinners() {
        ArrayAdapter<String> spinnerJointAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.joints));
        spinnerJointAdapter.setDropDownViewResource(R.layout.spinner_item);
        jointSpinner.setAdapter(spinnerJointAdapter);
        ArrayAdapter<String> spinnerMovementAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.movements));
        spinnerMovementAdapter.setDropDownViewResource(R.layout.spinner_item);
        movementSpinner.setAdapter(spinnerMovementAdapter);
    }

    private void updateMeasuringUi(boolean measuring) {
        actualDegreeTextView.setText(measuring ? getString(R.string.ready_to_measure) : "0°");
        finalDegreeTextView.setVisibility(measuring ? VISIBLE : INVISIBLE);
        fabStartStop.setImageResource(measuring ? R.drawable.ic_media_play : R.drawable.ic_media_pause);
        fabChangeAxis.setVisibility(measuring ? VISIBLE : INVISIBLE);
        finalDegreeTextView.setText(measuring ? format(getResources().getString(R.string.final_degree_measured), mMeasuresViewModel.getMeasuredAngle()) : "");
        if (measuring) {
            Measure measureToAdd = new Measure(0, jointSpinner.getSelectedItem().toString(), movementSpinner.getSelectedItem().toString(), mMeasuresViewModel.getMeasuredAngle(), mActualPatient.getId());
            mMeasuresViewModel.addMeasure(measureToAdd);
        } else {
            Snackbar.make(findViewById(R.id.coordinator_main), "Set " + mMeasuresViewModel.getMeasuredAngle() + " as initial degree", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void subscribeUi() {
        // Update the list when the data changes
        mMeasuresViewModel.getMeasuresForPatient(mActualPatient.getId()).observe(this, measures -> {
            if (measures != null && !measures.isEmpty()) {
                fillLastFive(measures);
            }
        });
        mMeasuresViewModel.getObservedAngle().observe(this, angle -> {
            if (mMeasuresViewModel.isMeasuring() && angle != null) {
                actualDegreeTextView.setText(String.format(getString(R.string.actual_degree_measuring), angle));
            }
        });
    }

    private void fillLastFive(List<Measure> measures) {
        lastFiveLayout.setVisibility(VISIBLE);
        int lastElementPointer = measures.size();
        lastFiveLastTextView.setText(format(getString(R.string.last_5_1), measures.get(lastElementPointer - 1).getMeasuredAngle(), measures.get(lastElementPointer - 1).getJoint(), measures.get(lastElementPointer - 1).getMovement()));
        lastFive2TextView.setText(lastElementPointer >= 2 ? format(getString(R.string.last_5_2), measures.get(lastElementPointer - 2).getMeasuredAngle(), measures.get(lastElementPointer - 2).getJoint(), measures.get(lastElementPointer - 2).getMovement()) : "");
        lastFive3TextView.setText(lastElementPointer >= 3 ? format(getString(R.string.last_5_3), measures.get(lastElementPointer - 3).getMeasuredAngle(), measures.get(lastElementPointer - 3).getJoint(), measures.get(lastElementPointer - 3).getMovement()) : "");
        lastFive4TextView.setText(lastElementPointer >= 4 ? format(getString(R.string.last_5_4), measures.get(lastElementPointer - 4).getMeasuredAngle(), measures.get(lastElementPointer - 4).getJoint(), measures.get(lastElementPointer - 4).getMovement()) : "");
        lastFive5TextView.setText(lastElementPointer >= 5 ? format(getString(R.string.last_5_5), measures.get(lastElementPointer - 5).getMeasuredAngle(), measures.get(lastElementPointer - 5).getJoint(), measures.get(lastElementPointer - 5).getMovement()) : "");
    }

    private void setUpToolbarAndDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (getIntent().getParcelableExtra(getString(R.string.patient_extra)) != null) {
            mActualPatient = getIntent().getParcelableExtra(getString(R.string.patient_extra));
            setTitle(String.format(getString(R.string.patient_item_name), mActualPatient.getName(), mActualPatient.getSurname()));
        }
    }

    @Override
    public void onBackPressed() {
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
            Intent loginScreenIntent = new Intent(this, LoginActivity.class);
            startActivity(loginScreenIntent);
        } else if (id == R.id.nav_gallery) {
            Intent patientsScreenIntent = new Intent(this, PatientsActivity.class);
            startActivity(patientsScreenIntent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

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
        mMeasuresViewModel.sensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
