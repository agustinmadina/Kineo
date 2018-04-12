package com.ownhealth.kineo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ownhealth.kineo.R;

import butterknife.ButterKnife;

/**
 * Created by Agustin Madina on 4/12/2018.
 */
public class AddPatientFragment extends Fragment {

    public static final String TAG =  "AddPatientFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_patient, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
