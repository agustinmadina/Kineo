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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ownhealth.kineo.utils.Constants.LOGIN_TOKEN;
import static com.ownhealth.kineo.utils.Constants.PATIENT_TO_EDIT_EXTRA;
import static com.ownhealth.kineo.utils.Constants.SHARED_PREFERENCES;

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

    private MeasuresViewModel mMeasuresViewModel;
    private PatientsViewModel mPatientsViewModel;
    private Patient mActualPatient;
    private List<Measure> measures;

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
    }

    private void setupSpinners() {
        ArrayAdapter<String> spinnerJointAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.joints));
        spinnerJointAdapter.setDropDownViewResource(R.layout.spinner_item);
        jointSpinner.setAdapter(spinnerJointAdapter);
        ArrayAdapter<String> spinnerMovementAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.movements));
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

        } else if (id == R.id.all_measurements) {

        } else if (id == R.id.logout) {
            SharedPreferences prefs = this.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LOGIN_TOKEN, false);
            editor.apply();
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            startActivity(logoutIntent);
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
        startActivity(intent);
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
