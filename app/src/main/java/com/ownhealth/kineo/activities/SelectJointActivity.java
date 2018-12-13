package com.ownhealth.kineo.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Measure.LocalMeasureRepository;
import com.ownhealth.kineo.persistence.Measure.Measure;
import com.ownhealth.kineo.persistence.Patient.LocalPatientRepository;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.utils.Constants;
import com.ownhealth.kineo.viewmodel.MeasuresViewModel;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;
import com.whygraphics.gifview.gif.GIFView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ownhealth.kineo.utils.Constants.LOGIN_TOKEN;
import static com.ownhealth.kineo.utils.Constants.PATIENT_TO_EDIT_EXTRA;
import static com.ownhealth.kineo.utils.Constants.SHARED_PREFERENCES;
import static com.ownhealth.kineo.viewmodel.MeasuresViewModel.X_AXIS;
import static com.ownhealth.kineo.viewmodel.MeasuresViewModel.Y_AXIS;
import static com.ownhealth.kineo.viewmodel.MeasuresViewModel.Z_AXIS;

/**
 * Created by Agustin Madina on 3/26/2018.
 */
public class SelectJointActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.sp_joints)
    Spinner jointSpinner;
    @BindView(R.id.sp_movements)
    Spinner movementSpinner;
    @BindView(R.id.button_accept_joint)
    Button acceptJointButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.gif_view)
    GIFView gifView;

    private MeasuresViewModel mMeasuresViewModel;
    private PatientsViewModel mPatientsViewModel;
    private Patient mActualPatient;
    private List<Measure> measures;
    private ArrayAdapter<String> spinnerMovementAdapter;
    private String axisBeingMeasured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_joint);
        ButterKnife.bind(this);
        setUpToolbarAndDrawer();
        MeasuresViewModel.Factory factory = new MeasuresViewModel.Factory(getApplication(), new LocalMeasureRepository(JointDatabase.getInstance(getApplication()).measureDao()));
        mMeasuresViewModel = ViewModelProviders.of(this, factory).get(MeasuresViewModel.class);
        PatientsViewModel.Factory factoryPatients = new PatientsViewModel.Factory(getApplication(), new LocalPatientRepository(JointDatabase.getInstance(getApplication()).patientDao()));
        mPatientsViewModel = ViewModelProviders.of(this, factoryPatients).get(PatientsViewModel.class);
        setupSpinners();
        setupMovementSpinnerChange();
        setupGifSpinnerChange();
    }

    private void setupMovementSpinnerChange() {
        jointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                spinnerMovementAdapter.clear();
                switch (parentView.getSelectedItem().toString()) {
                    case "Cadera":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_cadera));
                        break;
                    case "Cervical Inferior":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_cervical_inferior));
                        break;
                    case "Cervical Superior":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_cervical_superior));
                        break;
                    case "Codo":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_codo));
                        break;
                    case "Hombro":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_hombro));
                        break;
                    case "Rodilla":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_rodilla));
                        break;
                    case "Tobillo":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_tobillo));
                        break;
                    case "Torax":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_torax));
                        break;
                    case "Lumbar":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_lumbar));
                        break;
                    case "Muñeca":
                        spinnerMovementAdapter.addAll(getResources().getStringArray(R.array.movements_muñeca));
                        break;
                }
                movementSpinner.setSelection(1, true);
                movementSpinner.setSelection(0, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void setupGifSpinnerChange() {
        movementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (parentView.getSelectedItem().toString()) {
                    case "Abduccion":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Aduccion":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Desviacion Cubita":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Desviacion Radial":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Extension":
                        gifView.setGifResource("asset:giphy");
                        axisBeingMeasured = X_AXIS;
                        break;
                    case "Flexion":
                        gifView.setGifResource("asset:giphy");
                        axisBeingMeasured = X_AXIS;
                        break;
                    case "Flexoextension":
                        gifView.setGifResource("asset:giphy");
                        axisBeingMeasured = X_AXIS;
                        break;
                    case "Inclinacion":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Supinacion":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Pronacion":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Rotacion":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Rotacion Interna":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                    case "Rotacion Externa":
                        gifView.setGifResource("asset:gif1");
                        axisBeingMeasured = Y_AXIS;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> spinnerJointAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.joints));
        spinnerJointAdapter.setDropDownViewResource(R.layout.spinner_item);
        jointSpinner.setAdapter(spinnerJointAdapter);
        List<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, getResources().getStringArray(R.array.movements_cadera));
        spinnerMovementAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, arrayList);
        spinnerMovementAdapter.setDropDownViewResource(R.layout.spinner_item);
        movementSpinner.setAdapter(spinnerMovementAdapter);
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
        } else if (id == R.id.edit_patient) {
            Intent patientsScreenIntent = new Intent(this, PatientsActivity.class);
            patientsScreenIntent.putExtra(PATIENT_TO_EDIT_EXTRA, mActualPatient);
            startActivity(patientsScreenIntent);
        } else if (id == R.id.patient_progress) {
            Intent patientHistoryIntent = new Intent(this, PatientHistoryActivity.class);
            patientHistoryIntent.putExtra(Constants.PATIENT_EXTRA, mActualPatient);
            patientHistoryIntent.putExtra(Constants.JOINT_EXTRA, jointSpinner.getSelectedItem().toString());
            patientHistoryIntent.putExtra(Constants.MOVEMENT_EXTRA, movementSpinner.getSelectedItem().toString());
            startActivity(patientHistoryIntent);

        } else if (id == R.id.logout) {
            SharedPreferences prefs = this.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LOGIN_TOKEN, false);
            editor.apply();
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
            finish();
        } else if (id == R.id.reports) {
            Intent reportsIntent = new Intent(this, ReportsActivity.class);
            startActivity(reportsIntent);
        }
        item.setChecked(false);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.button_accept_joint)
    public void continueToMeasure() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.PATIENT_EXTRA, mActualPatient);
        intent.putExtra(Constants.JOINT_EXTRA, jointSpinner.getSelectedItem().toString());
        intent.putExtra(Constants.MOVEMENT_EXTRA, movementSpinner.getSelectedItem().toString());
        intent.putExtra(Constants.AXIS_EXTRA, axisBeingMeasured);
        startActivity(intent);
    }

    @OnClick(R.id.button_history)
    public void continueToHistory() {
        Intent patientHistoryIntent = new Intent(this, PatientHistoryActivity.class);
        patientHistoryIntent.putExtra(Constants.PATIENT_EXTRA, mActualPatient);
        patientHistoryIntent.putExtra(Constants.JOINT_EXTRA, jointSpinner.getSelectedItem().toString());
        patientHistoryIntent.putExtra(Constants.MOVEMENT_EXTRA, movementSpinner.getSelectedItem().toString());
        startActivity(patientHistoryIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPatientsViewModel.getPatient(mActualPatient.getId()).observe(this, patient -> {
            if (patient != null) {
                mActualPatient = patient;
                setTitle(String.format(getString(R.string.patient_item_name), mActualPatient.getName(), mActualPatient.getSurname()));
            } else {
                finish();
            }
        });
    }
}
