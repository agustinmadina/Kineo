package com.ownhealth.kineo.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evrencoskun.tableview.TableView;
import com.levitnudi.legacytableview.LegacyTableView;
import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Measure.LocalMeasureRepository;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.utils.Constants;
import com.ownhealth.kineo.viewmodel.MeasuresViewModel;

import static com.levitnudi.legacytableview.LegacyTableView.GOLDALINE;
import static com.levitnudi.legacytableview.LegacyTableView.getRowSeperator;


/**
 * Created by Agustin Madina on 23/11/18.
 */
public class PatientTableFragment extends Fragment {

    private Patient patient;
    private String jointMeasured;
    private String movementMeasured;
    private LegacyTableView legacyTableView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PatientTableFragment newInstance(Patient patient, String jointMeasured, String movementMeasured) {
        PatientTableFragment fragment = new PatientTableFragment();
        Bundle args = new Bundle();
        args.putString(Constants.JOINT_EXTRA, jointMeasured);
        args.putParcelable(Constants.PATIENT_EXTRA, patient);
        args.putString(Constants.MOVEMENT_EXTRA, movementMeasured);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_history, container, false);
        jointMeasured = getArguments().getString(Constants.JOINT_EXTRA);
        patient = getArguments().getParcelable(Constants.PATIENT_EXTRA);
        movementMeasured = getArguments().getString(Constants.MOVEMENT_EXTRA);


        legacyTableView = (LegacyTableView)rootView.findViewById(R.id.legacy_table_view);
        legacyTableView.setTablePadding(7);

        //to enable users to zoom in and out:
//        legacyTableView.setZoomEnabled(true);
//        legacyTableView.setShowZoomControls(true);
        legacyTableView.setTheme(GOLDALINE);
        getFromDatabase();
        return rootView;
    }

    public void getFromDatabase() {//execute this method to fetch from database
        MeasuresViewModel.Factory factory = new MeasuresViewModel.Factory(getActivity().getApplication(), new LocalMeasureRepository(JointDatabase.getInstance(getActivity().getApplication()).measureDao()));
        MeasuresViewModel mMeasuresViewModel = ViewModelProviders.of(this, factory).get(MeasuresViewModel.class);
        mMeasuresViewModel.getMeasuresForPatientForJointForMovement(patient.getId(), jointMeasured, movementMeasured).observe(this, measures -> {
            if (measures != null && !measures.isEmpty()) {
                for (int i = 0; i < measures.size(); i++) {
                    LegacyTableView.insertLegacyContent(measures.get(i).getDate(), getString(R.string.actual_degree_measuring, measures.get(i).getMeasuredAngle()));
                }
                //simple table content insert method for table contents
                LegacyTableView.insertLegacyTitle("Dia y hora", "Angulo medido");
                legacyTableView.setTitle(LegacyTableView.readLegacyTitle());
                legacyTableView.setContent(LegacyTableView.readLegacyContent());
                legacyTableView.build();
            }
        });
    }
}
