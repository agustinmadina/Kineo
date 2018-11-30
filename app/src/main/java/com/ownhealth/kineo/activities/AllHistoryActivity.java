package com.ownhealth.kineo.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.levitnudi.legacytableview.LegacyTableView;
import com.ownhealth.kineo.R;
import com.ownhealth.kineo.fragments.PatientGraphFragment;
import com.ownhealth.kineo.fragments.PatientTableFragment;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Measure.LocalMeasureRepository;
import com.ownhealth.kineo.persistence.Patient.LocalPatientRepository;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.utils.Constants;
import com.ownhealth.kineo.viewmodel.MeasuresViewModel;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.levitnudi.legacytableview.LegacyTableView.OCEAN;
import static com.ownhealth.kineo.utils.Constants.LOGIN_TOKEN;
import static com.ownhealth.kineo.utils.Constants.PATIENT_TO_EDIT_EXTRA;
import static com.ownhealth.kineo.utils.Constants.SHARED_PREFERENCES;

public class AllHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private LegacyTableView legacyTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_history);

        ButterKnife.bind(this);
        setUpToolbarAndDrawer();
        setTitle("Reportes");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        legacyTableView = (LegacyTableView) findViewById(R.id.legacy_table_view);
        legacyTableView.setTablePadding(7);

        //to enable users to zoom in and out:
//        legacyTableView.setZoomEnabled(true);
//        legacyTableView.setShowZoomControls(true);
        legacyTableView.setTheme(OCEAN);
        getFromDatabase();
    }

    public void getFromDatabase() {//execute this method to fetch from database
        PatientsViewModel.Factory patientFactory = new PatientsViewModel.Factory(getApplication(), new LocalPatientRepository(JointDatabase.getInstance(getApplication()).patientDao()));
        PatientsViewModel mPatientsViewModel = ViewModelProviders.of(this, patientFactory).get(PatientsViewModel.class);
        MeasuresViewModel.Factory factory = new MeasuresViewModel.Factory(getApplication(), new LocalMeasureRepository(JointDatabase.getInstance(getApplication()).measureDao()));
        MeasuresViewModel mMeasuresViewModel = ViewModelProviders.of(this, factory).get(MeasuresViewModel.class);
        mMeasuresViewModel.getAllMeasures().observe(this, measures -> {
            if (measures != null && !measures.isEmpty()) {
                for (int i = 0; i < measures.size(); i++) {
                    int finalI = i;
                    mPatientsViewModel.getPatient(measures.get(i).getPatientId()).observe(this, patient -> {
                        if (patient != null) {
                            LegacyTableView.insertLegacyContent(patient.getSurname() + " " + patient.getName(), measures.get(finalI).getJoint(), measures.get(finalI).getMovement(), measures.get(finalI).getDate(), getString(R.string.actual_degree_measuring, measures.get(finalI).getMeasuredAngle()));
                        }
                        });
                    }
                        //simple table content insert method for table contents
                        LegacyTableView.insertLegacyTitle("Paciente", "Articulacion", "Movimiento", "Dia y hora", "Angulo medido");
                        legacyTableView.setTitle(LegacyTableView.readLegacyTitle());
                        legacyTableView.setContent(LegacyTableView.readLegacyContent());
                        legacyTableView.setContentTextSize(25);
                        legacyTableView.setTitleTextSize(27);
                        legacyTableView.build();
                    }
                });
            }


            private void setUpToolbarAndDrawer () {
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
            }

            @Override
            public void onBackPressed () {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
            }

            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public boolean onNavigationItemSelected (MenuItem item){
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.logout) {
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

        }
