package com.ownhealth.kineo.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.VISIBLE;
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
    @BindView(R.id.text_input_patient_age)
    TextInputLayout mAgeInput;
    @BindView(R.id.input_patient_age)
    EditText mAgeEditText;
    @BindView(R.id.add_patient_button)
    Button mAddPatientButton;
    @BindView(R.id.delete_patient_button)
    Button mDeletePatientButton;
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
            mAgeEditText.setText(mPatientToEdit != null ? String.valueOf(mPatientToEdit.getAge()) : "");
            mDeletePatientButton.setVisibility(VISIBLE);
            mAddPatientButton.setText("Finalizar edicion");
        }
        mDiagnosticEditText.setOnEditorActionListener(diagnosticEnterKeyListener());
        mNameEditText.requestFocus();

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
        final int age = Integer.valueOf(mAgeEditText.getText().toString());
        Patient patient = new Patient();
        if (getArguments() != null) {
            patient.setId(mPatientToEdit.getId());
        }
        patient.setName(username);
        patient.setSurname(surname);
        patient.setEmail(email);
        patient.setDiagnostic(diagnostic);
        patient.setAge(age);

        mPatientsViewModel.addPatient(patient).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mProgressBar.setVisibility(VISIBLE);
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
     * Listener that checks if enter key was pressed while editing field, in order to click add patient button instantly
     */
    EditText.OnEditorActionListener diagnosticEnterKeyListener() {
        return (textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mAddPatientButton.performClick();
                return true;
            }
            return false;
        };
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
        String age = mAgeEditText.getText().toString();

        if (name.isEmpty()) {
            mNameTextInput.setError(getString(R.string.add_patient_required_message));
            valid = false;
        } else {
            mNameTextInput.setError(null);
            mNameEditText.getBackground().clearColorFilter();
        }

        if (surname.isEmpty()) {
            mSurnameTextInput.setError(getString(R.string.add_patient_required_message));
            valid = false;
        } else {
            mSurnameTextInput.setError(null);
            mSurnameEditText.getBackground().clearColorFilter();
        }

        if (age.isEmpty()) {
            mAgeInput.setError(getString(R.string.add_patient_required_message));
            valid = false;
        } else {
            mAgeInput.setError(null);
            mAgeEditText.getBackground().clearColorFilter();
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

    @OnClick(R.id.add_patient_button)
    public void addPatientClick() {
        addPatient();
    }

    @OnClick(R.id.delete_patient_button)
    public void deletePatientClick() {
        deletePatient();
    }

    @OnClick(R.id.secret_button)
    public void secretButtonClick() {
        Patient patient = new Patient();
        patient.setName("Matias");
        patient.setSurname("Caruso");
        patient.setAge(21);
        mPatientsViewModel.addPatient(patient).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient2 = new Patient();
        patient2.setName("Mariana");
        patient2.setSurname("Bianco");
        patient2.setAge(18);
        mPatientsViewModel.addPatient(patient2).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient3 = new Patient();
        patient3.setName("Julia");
        patient3.setSurname("Caceres");
        patient3.setAge(30);
        mPatientsViewModel.addPatient(patient3).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient4 = new Patient();
        patient4.setName("Osvalvo");
        patient4.setSurname("Gomez");
        patient4.setAge(23);
        mPatientsViewModel.addPatient(patient4).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient5 = new Patient();
        patient5.setName("Hernan");
        patient5.setSurname("Abondi");
        patient5.setAge(60);
        mPatientsViewModel.addPatient(patient5).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient6 = new Patient();
        patient6.setName("Brian");
        patient6.setSurname("Macri");
        patient6.setAge(70);
        mPatientsViewModel.addPatient(patient6).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient7 = new Patient();
        patient7.setId(96);
        patient7.setName("Liliana");
        patient7.setSurname("Moroni");
        patient7.setAge(46);
        mPatientsViewModel.addPatient(patient7).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient8 = new Patient();
        patient8.setName("Julia");
        patient8.setSurname("Dispa");
        patient8.setAge(65);
        mPatientsViewModel.addPatient(patient8).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient9 = new Patient();
        patient9.setName("Ronaldo");
        patient9.setSurname("Froni");
        patient9.setAge(40);
        mPatientsViewModel.addPatient(patient9).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient11 = new Patient();
        patient11.setName("Hernan");
        patient11.setSurname("Taitan");
        patient11.setAge(45);
        mPatientsViewModel.addPatient(patient11).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient12 = new Patient();
        patient12.setName("Ugoni");
        patient12.setSurname("Rito");
        patient12.setAge(22);
        mPatientsViewModel.addPatient(patient12).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient13 = new Patient();
        patient13.setName("Sebastian");
        patient13.setSurname("Esteverria");
        patient13.setAge(25);
        mPatientsViewModel.addPatient(patient13).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient24 = new Patient();
        patient24.setName("Tatiana");
        patient24.setSurname("Gomez");
        patient24.setAge(21);
        mPatientsViewModel.addPatient(patient24).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient14 = new Patient();
        patient14.setName("Enrique");
        patient14.setSurname("Penimpedde");
        patient14.setAge(21);
        mPatientsViewModel.addPatient(patient14).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient15 = new Patient();
        patient15.setName("Rodrigo");
        patient15.setSurname("Gatica");
        patient15.setAge(30);
        mPatientsViewModel.addPatient(patient15).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient16 = new Patient();
        patient16.setName("Paloma");
        patient16.setSurname("Pelussa");
        patient16.setAge(23);
        mPatientsViewModel.addPatient(patient16).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient17 = new Patient();
        patient17.setName("Federico");
        patient17.setSurname("Faggiolini");
        patient17.setAge(27);
        mPatientsViewModel.addPatient(patient17).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient18 = new Patient();
        patient18.setName("Martin");
        patient18.setSurname("Hinojal");
        patient18.setAge(21);
        mPatientsViewModel.addPatient(patient18).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient19 = new Patient();
        patient19.setName("Federico");
        patient19.setSurname("Tehaux");
        patient19.setAge(27);
        mPatientsViewModel.addPatient(patient19).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient20 = new Patient();
        patient20.setName("Guido");
        patient20.setSurname("Viggiano");
        patient20.setAge(25);
        mPatientsViewModel.addPatient(patient20).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient21 = new Patient();
        patient21.setName("Ramiro");
        patient21.setSurname("Tejon");
        patient21.setAge(55);
        mPatientsViewModel.addPatient(patient21).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient30 = new Patient();
        patient30.setName("Agustin");
        patient30.setSurname("Ramirez");
        patient30.setAge(18);
        mPatientsViewModel.addPatient(patient30).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient31 = new Patient();
        patient31.setName("Claudio");
        patient31.setSurname("Calvo");
        patient31.setAge(35);
        mPatientsViewModel.addPatient(patient31).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient33 = new Patient();
        patient33.setName("Carlos");
        patient33.setSurname("Tejon");
        patient33.setAge(38);
        mPatientsViewModel.addPatient(patient33).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient45 = new Patient();
        patient45.setName("Carlos");
        patient45.setSurname("Tejon");
        patient45.setAge(38);
        mPatientsViewModel.addPatient(patient45).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient38 = new Patient();
        patient38.setName("Alejandro");
        patient38.setSurname("Madina");
        patient38.setAge(46);
        mPatientsViewModel.addPatient(patient38).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient37 = new Patient();
        patient37.setName("Jorge");
        patient37.setSurname("Lodi");
        patient37.setAge(46);
        mPatientsViewModel.addPatient(patient37).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient39 = new Patient();
        patient39.setName("Cristina");
        patient39.setSurname("Lodi");
        patient39.setAge(46);
        mPatientsViewModel.addPatient(patient39).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Patient patient40 = new Patient();
        patient40.setName("Veronica");
        patient40.setSurname("Freuler");
        patient40.setAge(46);
        mPatientsViewModel.addPatient(patient40).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
        Toast.makeText(getActivity(), "Pacientes mock agregados",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
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
                        mProgressBar.setVisibility(VISIBLE);
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
