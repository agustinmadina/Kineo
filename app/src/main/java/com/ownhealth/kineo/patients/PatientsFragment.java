package com.ownhealth.kineo.patients;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsFragment extends Fragment {

    PatientsViewModel mPatientsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPatientsViewModel = ViewModelProviders.of(getActivity()).get(PatientsViewModel.class);
    }

}
