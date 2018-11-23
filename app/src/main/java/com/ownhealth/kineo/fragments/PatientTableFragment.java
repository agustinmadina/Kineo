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

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Patient patient;
    private String jointMeasured;
    private LegacyTableView legacyTableView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PatientTableFragment newInstance(Patient patient, String jointMeasured) {
        PatientTableFragment fragment = new PatientTableFragment();
        Bundle args = new Bundle();
        args.putString(Constants.JOINT_EXTRA, jointMeasured);
        args.putParcelable(Constants.PATIENT_EXTRA, patient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_history, container, false);
        jointMeasured = getArguments().getString(Constants.JOINT_EXTRA);
        patient = getArguments().getParcelable(Constants.PATIENT_EXTRA);

        //set table title labels
        //set table contents as string arrays
//        LegacyTableView.insertLegacyContent("2999010", "John Deer", "50", "john@example.com",
//                "332312", "Kennedy F", "33", "ken@example.com"
//                ,"42343243", "Java Lover", "28", "Jlover@example.com"
//                ,"4288383", "Mike Tee", "22", "miket@example.com");

        legacyTableView = (LegacyTableView)rootView.findViewById(R.id.legacy_table_view);
//        legacyTableView.setTitle(LegacyTableView.readLegacyTitle());
//        legacyTableView.setContent(LegacyTableView.readLegacyContent());

        //depending on the phone screen size default table scale is 100
        //you can change it using this method
        legacyTableView.setInitialScale(100);//default initialScale is zero (0)

        //if you want a smaller table, change the padding setting
        legacyTableView.setTablePadding(7);

        //to enable users to zoom in and out:
//        legacyTableView.setZoomEnabled(true);
//        legacyTableView.setShowZoomControls(true);
        legacyTableView.setTheme(GOLDALINE);
        //once you have inserted contents and titles, you can retrieve them
        //using readLegacyTitle() and readLegacyContent() methods
        getFromDatabase();
//        legacyTableView.setContent(LegacyTableView.readLegacyContent());
        //remember to build your table as the last step
//        legacyTableView.build();
        return rootView;
    }

    public void getFromDatabase() {//execute this method to fetch from database
        MeasuresViewModel.Factory factory = new MeasuresViewModel.Factory(getActivity().getApplication(), new LocalMeasureRepository(JointDatabase.getInstance(getActivity().getApplication()).measureDao()));
        MeasuresViewModel mMeasuresViewModel = ViewModelProviders.of(this, factory).get(MeasuresViewModel.class);
        mMeasuresViewModel.getMeasuresForPatient(patient.getId()).observe(this, measures -> {
            if (measures != null && !measures.isEmpty()) {
                /* insert your column titles using legacy insertLegacyTitle() function*/
//            LegacyTableView.insertLegacyTitle(measures., cursor[0].getColumnName(2),
//                    cursor[0].getColumnName(3), cursor[0].getColumnName(4));

                for (int i = 0; i < measures.size(); i++) {
                    LegacyTableView.insertLegacyContent(measures.get(i).getDate(), measures.get(i).getMovement(), getString(R.string.actual_degree_measuring, measures.get(i).getMeasuredAngle()));
                }
                //simple table content insert method for table contents
                LegacyTableView.insertLegacyTitle("Date and Time", "Movement", "Angle measured");
                legacyTableView.setTitle(LegacyTableView.readLegacyTitle());
                legacyTableView.setContent(LegacyTableView.readLegacyContent());
                legacyTableView.build();
            }
        });
    }
}
