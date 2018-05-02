package com.ownhealth.kineo.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Patient.LocalPatientRepository;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.utils.ToolbarHelper;
import com.ownhealth.kineo.viewmodel.PatientsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.ownhealth.kineo.utils.Constants.PATIENT_TO_EDIT_EXTRA;

/**
 * Created by Agustin Madina on 4/12/2018.
 */
public class AddPatientFragment extends Fragment {

    public static final String TAG = "AddPatientFragment";

    private PatientsViewModel mPatientsViewModel;
    private Patient mPatientToEdit;

    @BindView(R.id.text_input_patient_name)
    TextInputLayout mNameTextInput;
    @BindView(R.id.text_input_patient_surname)
    TextInputLayout mSurnameTextInput;
    @BindView(R.id.input_patientname)
    EditText mNameEditText;
    @BindView(R.id.input_patient_surname)
    EditText mSurnameEditText;
    @BindView(R.id.input_patient_email)
    EditText mEmailEditText;
    @BindView(R.id.input_patient_diagnostic)
    EditText mDiagnosticEditText;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    public static AddPatientFragment newInstance(Patient patient) {
        AddPatientFragment editPatientFragment = new AddPatientFragment();
        Bundle args = new Bundle();
        args.putParcelable(PATIENT_TO_EDIT_EXTRA, patient);
        editPatientFragment.setArguments(args);

        return editPatientFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PatientsViewModel.Factory factory = new PatientsViewModel.Factory(getActivity().getApplication(), new LocalPatientRepository(JointDatabase.getInstance(getActivity().getApplication()).patientDao()));
        mPatientsViewModel = ViewModelProviders.of(getActivity(), factory).get(PatientsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_patient, container, false);
        ButterKnife.bind(this, view);
        initToolbar(view);
        if (getArguments() != null) {
            mPatientToEdit = getArguments().getParcelable(PATIENT_TO_EDIT_EXTRA);
            mNameEditText.setText(mPatientToEdit != null ? mPatientToEdit.getName() : "");
            mSurnameEditText.setText(mPatientToEdit != null ? mPatientToEdit.getSurname() : "");
            mEmailEditText.setText(mPatientToEdit != null ? mPatientToEdit.getEmail() : "");
            mDiagnosticEditText.setText(mPatientToEdit != null ? mPatientToEdit.getDiagnostic() : "");
        }
        return view;
    }

    private void addPatient() {
        Log.d(TAG, getString(R.string.login_tag));

        if (!requiredFieldsAreCompleted()) {
            Toast.makeText(getContext(), R.string.add_patient_required_message, Toast.LENGTH_SHORT).show();
            return;
        }

        final String username = mNameEditText.getText().toString();
        final String surname = mSurnameEditText.getText().toString();
        final String email = mEmailEditText.getText().toString();
        final String diagnostic = mDiagnosticEditText.getText().toString();
        Patient patient = new Patient();
        if (getArguments() != null) {
            patient.setId(mPatientToEdit.getId());
        }
        patient.setName(username);
        patient.setSurname(surname);
        patient.setEmail(email);
        patient.setDiagnostic(diagnostic);

        mPatientsViewModel.addPatient(patient).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    /**
     * Checks if TextFields are filled, and sets corresponding error in TextInputLayout.
     *
     * @return whether fields are filled or not.
     */
    public boolean requiredFieldsAreCompleted() {
        boolean valid = true;

        String name = mNameEditText.getText().toString();
        String surname = mSurnameEditText.getText().toString();

        if (name.isEmpty()) {
            mNameTextInput.setError(getString(R.string.add_patient_required_message));
            valid = false;
        } else {
            mNameTextInput.setError(null);
            mNameEditText.getBackground().clearColorFilter();
        }

        if (surname.isEmpty()) {
            mSurnameTextInput.setError(getString(R.string.add_patient_surname_required));
            valid = false;
        } else {
            mSurnameTextInput.setError(null);
            mSurnameEditText.getBackground().clearColorFilter();
        }

        return valid;
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ToolbarHelper.setToolbar(getActivity(), toolbar);
        ToolbarHelper.show(getActivity(), true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_patient_menu, menu);
        if (getArguments() != null) {
            MenuItem item = menu.findItem(R.id.action_delete_patient);
            item.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                return true;
            }
            case R.id.action_add_patient: {
                addPatient();
                return true;
            }
            case R.id.action_delete_patient: {
                deletePatient();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePatient() {
        mPatientsViewModel.deletePatient(mPatientToEdit).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
