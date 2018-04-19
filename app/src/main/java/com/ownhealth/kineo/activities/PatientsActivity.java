package com.ownhealth.kineo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.fragments.PatientsFragment;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        // Add product list fragment if this is first creation
        if (savedInstanceState == null) {
            PatientsFragment fragment = new PatientsFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, PatientsFragment.TAG).commit();
        }
    }
}
