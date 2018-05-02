package com.ownhealth.kineo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.fragments.AddPatientFragment;
import com.ownhealth.kineo.fragments.PatientsFragment;
import com.ownhealth.kineo.persistence.Patient.Patient;

import static com.ownhealth.kineo.utils.Constants.PATIENT_TO_EDIT_EXTRA;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        // Add patient list fragment if this is first creation
        if ((savedInstanceState == null && getIntent().getParcelableExtra(PATIENT_TO_EDIT_EXTRA) == null)) {
            PatientsFragment fragment = new PatientsFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, PatientsFragment.TAG).commit();
        } else if (getIntent().getParcelableExtra(PATIENT_TO_EDIT_EXTRA) != null) {
            Patient patientToEdit = getIntent().getParcelableExtra(PATIENT_TO_EDIT_EXTRA);
            AddPatientFragment addPatientFragment = AddPatientFragment.newInstance(patientToEdit);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, addPatientFragment, PatientsFragment.TAG).commit();
        }
    }
}
