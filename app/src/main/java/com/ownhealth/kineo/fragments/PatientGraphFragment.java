package com.ownhealth.kineo.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.levitnudi.legacytableview.LegacyTableView;
import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Measure.LocalMeasureRepository;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.utils.Constants;
import com.ownhealth.kineo.viewmodel.MeasuresViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agustin Madina on 23/11/18.
 */
public class PatientGraphFragment extends Fragment {

    private Patient patient;
    private String jointMeasured;
    private String movementMeasured;
    private LineChart chart;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PatientGraphFragment newInstance(Patient patient, String jointMeasured, String movementMeasured) {
        PatientGraphFragment fragment = new PatientGraphFragment();
        Bundle args = new Bundle();
        args.putString(Constants.JOINT_EXTRA, jointMeasured);
        args.putParcelable(Constants.PATIENT_EXTRA, patient);
        args.putString(Constants.MOVEMENT_EXTRA, movementMeasured);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_graph, container, false);
        jointMeasured = getArguments().getString(Constants.JOINT_EXTRA);
        patient = getArguments().getParcelable(Constants.PATIENT_EXTRA);
        movementMeasured = getArguments().getString(Constants.MOVEMENT_EXTRA);
        chart = (LineChart) rootView.findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();
        MeasuresViewModel.Factory factory = new MeasuresViewModel.Factory(getActivity().getApplication(), new LocalMeasureRepository(JointDatabase.getInstance(getActivity().getApplication()).measureDao()));
        MeasuresViewModel mMeasuresViewModel = ViewModelProviders.of(this, factory).get(MeasuresViewModel.class);
        mMeasuresViewModel.getMeasuresForPatientForJointForMovement(patient.getId(), jointMeasured, movementMeasured).observe(this, measures -> {
            if (measures != null && !measures.isEmpty()) {
                for (int i = 0; i < measures.size(); i++) {
                    entries.add(new Entry(i, measures.get(i).getMeasuredAngle()));

                }
                LineDataSet dataSet = new LineDataSet(entries, "Angulo medido"); // add entries to dataset
                dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.green_details));
                dataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.green_details));
                LineData lineData = new LineData(dataSet);
                chart.setNoDataText("Por favor, cargue mediciones para esta aritculacion y movimiento para poder visualizar");
                chart.setData(lineData);
                Description description = new Description();
                description.setTextSize(10);
                description.setTextColor(R.color.colorPrimaryDark);
                description.setText("Progreso a traves del tiempo");
                chart.setDescription(description);
                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawLabels(false);
                YAxis yAxis = chart.getAxisLeft();
                chart.getAxisRight().setEnabled(false);
                yAxis.setValueFormatter(new MyYAxisValueFormatter());
                yAxis.setDrawGridLines(false);
                chart.invalidate();
            }
        });
        return rootView;
    }

    public class MyYAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {

            // format values to 1 decimal digit
            mFormat = new DecimalFormat("");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mFormat.format(value) + "°";
        }

    }
}
