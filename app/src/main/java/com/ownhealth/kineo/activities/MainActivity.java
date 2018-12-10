package com.ownhealth.kineo.activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Measure.LocalMeasureRepository;
import com.ownhealth.kineo.persistence.Measure.Measure;
import com.ownhealth.kineo.persistence.Patient.LocalPatientRepository;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.utils.AngleView;
import com.ownhealth.kineo.utils.Constants;
import com.ownhealth.kineo.viewmodel.MeasuresViewModel;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.ownhealth.kineo.utils.Constants.LOGIN_TOKEN;
import static com.ownhealth.kineo.utils.Constants.PATIENT_TO_EDIT_EXTRA;
import static com.ownhealth.kineo.utils.Constants.SHARED_PREFERENCES;
import static java.lang.String.format;

/**
 * Created by Agustin Madina on 3/26/2018.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private SensorManager sensorManager;

    @BindView(R.id.tag_edit_text)
    TextView tagEditText;
    @BindView(R.id.joint_actual)
    TextView jointActual;
    @BindView(R.id.movement_actual)
    TextView measurmentActual;
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
    @BindView(R.id.last_5_title)
    TextView lastFiveTitle;
    @BindView(R.id.ready_to_measure)
    TextView readyToMeasureTextView;
    @BindView(R.id.measurments_container)
    RelativeLayout measurmentsContainer;
    @BindView(R.id.fab_start_stop)
    ImageButton fabStartStop;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.angle_view)
    AngleView angleView;
    @BindView(R.id.delete_5_1)
    ImageView deleteFive1;
    @BindView(R.id.delete_5_2)
    ImageView deleteFive2;
    @BindView(R.id.delete_5_3)
    ImageView deleteFive3;
    @BindView(R.id.delete_5_4)
    ImageView deleteFive4;
    @BindView(R.id.delete_5_5)
    ImageView deleteFive5;

    private MeasuresViewModel mMeasuresViewModel;
    private PatientsViewModel mPatientsViewModel;
    private Patient mActualPatient;
    private List<Measure> measures;

    private String jointMeasured;
    private String movementMeasured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbarAndDrawer();
        movementMeasured = getIntent().getExtras().getString(Constants.MOVEMENT_EXTRA);
        jointMeasured = getIntent().getExtras().getString(Constants.JOINT_EXTRA);


        MeasuresViewModel.Factory factory = new MeasuresViewModel.Factory(getApplication(), new LocalMeasureRepository(JointDatabase.getInstance(getApplication()).measureDao()));
        mMeasuresViewModel = ViewModelProviders.of(this, factory).get(MeasuresViewModel.class);
        PatientsViewModel.Factory factoryPatients = new PatientsViewModel.Factory(getApplication(), new LocalPatientRepository(JointDatabase.getInstance(getApplication()).patientDao()));
        mPatientsViewModel = ViewModelProviders.of(this, factoryPatients).get(PatientsViewModel.class);
        subscribeUi();

        // Get the sensor manager from system services
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @OnClick(R.id.fab_start_stop)
    public void fabStartStopClick() {
        updateMeasuringUi(mMeasuresViewModel.isMeasuring());
        readyToMeasureTextView.setVisibility(GONE);
        mMeasuresViewModel.fabStartStopClick();
    }

    @OnClick(R.id.delete_5_1)
    public void deleteFive1() {
        mMeasuresViewModel.deleteMeasure(measures.get(measures.size() - 1));
    }

    @OnClick(R.id.delete_5_2)
    public void deleteFive2() {
        mMeasuresViewModel.deleteMeasure(measures.get(measures.size() - 2));
    }

    @OnClick(R.id.delete_5_3)
    public void deleteFive3() {
        mMeasuresViewModel.deleteMeasure(measures.get(measures.size() - 3));
    }

    @OnClick(R.id.delete_5_4)
    public void deleteFive4() {
        mMeasuresViewModel.deleteMeasure(measures.get(measures.size() - 4));
    }

    @OnClick(R.id.delete_5_5)
    public void deleteFive5() {
        mMeasuresViewModel.deleteMeasure(measures.get(measures.size() - 5));
    }

    private void updateMeasuringUi(boolean measuring) {
        actualDegreeTextView.setText(measuring ? "" : "0°");
        finalDegreeTextView.setVisibility(measuring ? VISIBLE : INVISIBLE);
        fabStartStop.setImageResource(measuring ? R.drawable.ic_media_play : R.drawable.ic_media_pause);
        finalDegreeTextView.setText(measuring ? format(getResources().getString(R.string.final_degree_measured), mMeasuresViewModel.getMeasuredAngle()) : "");
        if (measuring) {
            getTodayDate();
            Measure measureToAdd = new Measure(0, jointMeasured, movementMeasured, mMeasuresViewModel.getMeasuredAngle(), mActualPatient.getId(), getTodayDate(), mActualPatient.getAge(), tagEditText.getText().toString().isEmpty() ? " " : tagEditText.getText().toString());
            mMeasuresViewModel.addMeasure(measureToAdd);
        } else {
            Snackbar.make(findViewById(R.id.coordinator_main),  mMeasuresViewModel.getMeasuredAngle() + "° es el grado inicial", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }

    private String getTodayDate() {
        Date today = new Date();
        String format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(today);
        return format;
    }

    private void subscribeUi() {
        // Update the list when the data changes
        mMeasuresViewModel.getMeasuresForPatientForJointForMovement(mActualPatient.getId(), jointMeasured, movementMeasured).observe(this, measures -> {
            if (measures != null && !measures.isEmpty()) {
                this.measures = measures;
                fillLastFive(measures);
            } else {
                lastFiveTitle.setVisibility(GONE);
                measurmentsContainer.setVisibility(GONE);
            }
        });
        mMeasuresViewModel.getObservedAngle().observe(this, angle -> {
            if (mMeasuresViewModel.isMeasuring() && angle != null) {
                actualDegreeTextView.setText(String.format(getString(R.string.actual_degree_measuring), angle));
                angleView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                angleView.setAngle(angle);
            }
        });
    }

    private void fillLastFive(List<Measure> measures) {
        lastFiveTitle.setVisibility(VISIBLE);
        measurmentsContainer.setVisibility(VISIBLE);
        int lastElementPointer = measures.size();
        lastFiveLastTextView.setText(format(getString(R.string.last_5_1), measures.get(lastElementPointer - 1).getMeasuredAngle(), measures.get(lastElementPointer - 1).getTag()));
        lastFive2TextView.setText(lastElementPointer >= 2 ? format(getString(R.string.last_5_2), measures.get(lastElementPointer - 2).getMeasuredAngle(), measures.get(lastElementPointer - 2).getTag()) : "");
        lastFive3TextView.setText(lastElementPointer >= 3 ? format(getString(R.string.last_5_3), measures.get(lastElementPointer - 3).getMeasuredAngle(), measures.get(lastElementPointer - 3).getTag()) : "");
        lastFive4TextView.setText(lastElementPointer >= 4 ? format(getString(R.string.last_5_4), measures.get(lastElementPointer - 4).getMeasuredAngle(), measures.get(lastElementPointer - 4).getTag()) : "");
        lastFive5TextView.setText(lastElementPointer >= 5 ? format(getString(R.string.last_5_5), measures.get(lastElementPointer - 5).getMeasuredAngle(), measures.get(lastElementPointer - 5).getTag()) : "");
        deleteFive1.setVisibility(lastElementPointer >= 1 ? VISIBLE : GONE);
        deleteFive2.setVisibility(lastElementPointer >= 2 ? VISIBLE : GONE);
        deleteFive3.setVisibility(lastElementPointer >= 3 ? VISIBLE : GONE);
        deleteFive4.setVisibility(lastElementPointer >= 4 ? VISIBLE : GONE);
        deleteFive5.setVisibility(lastElementPointer >= 5 ? VISIBLE : GONE);
    }

    private void setUpToolbarAndDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.header_username);
        TextView navEmail = headerView.findViewById(R.id.header_mail);
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, 0);
        navUserName.setText(settings.getString(Constants.MEDIC_NAME_TOKEN, "Joint"));
        navEmail.setText(settings.getString(Constants.MEDIC_EMAIL_TOKEN, ""));

        if (getIntent().getParcelableExtra(Constants.PATIENT_EXTRA) != null) {
            mActualPatient = getIntent().getParcelableExtra(Constants.PATIENT_EXTRA);
        } else {
            finish();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.choose_other_patient) {
            Intent patientsScreenIntent = new Intent(this, PatientsActivity.class);
            startActivity(patientsScreenIntent);
            finish();
        } else if (id == R.id.edit_patient) {
            Intent patientsScreenIntent = new Intent(this, PatientsActivity.class);
            patientsScreenIntent.putExtra(PATIENT_TO_EDIT_EXTRA, mActualPatient);
            startActivity(patientsScreenIntent);
            finish();
        } else if (id == R.id.patient_progress) {
            Intent patientHistoryIntent = new Intent(this, PatientHistoryActivity.class);
            patientHistoryIntent.putExtra(Constants.PATIENT_EXTRA, mActualPatient);
            patientHistoryIntent.putExtra(Constants.JOINT_EXTRA, jointMeasured);
            patientHistoryIntent.putExtra(Constants.MOVEMENT_EXTRA, movementMeasured);
            startActivity(patientHistoryIntent);
            finish();
        } else if (id == R.id.logout) {
            SharedPreferences prefs = this.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LOGIN_TOKEN, false);
            editor.apply();
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
        }
        item.setChecked(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register listener
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SENSOR_DELAY_GAME);
        mPatientsViewModel.getPatient(mActualPatient.getId()).observe(this, patient -> {
            if (patient != null) {
                mActualPatient = patient;
                setTitle(String.format(getString(R.string.patient_item_name), mActualPatient.getName(), mActualPatient.getSurname()));
                jointActual.setText("Articulacion: " + jointMeasured);
                measurmentActual.setText("Movimiento: " + movementMeasured);

            } else {
                finish();
            }
        });
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
