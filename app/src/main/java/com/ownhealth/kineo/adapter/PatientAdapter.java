package com.ownhealth.kineo.adapter;

import android.content.Context;
import android.graphics.PathEffect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.Patient;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

/**
 * Created by Agustin Madina on 4/12/2018.
 */
public class PatientAdapter  extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    private final Context mContext;
    private List<Patient> mPatientList;

    public PatientAdapter(List<Patient> patientList, Context context) {
        mPatientList = patientList;
        mContext = context;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_patient, parent, false);
        return new PatientViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mPatientList.size();
    }

    public void setPatientList(@Nullable List<Patient> patients) {
        if (patients != null && !patients.isEmpty()) {
            mPatientList = patients;
            notifyDataSetChanged();
        }
    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_patient_name) TextView patientName;

        public PatientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
