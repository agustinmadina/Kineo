package com.ownhealth.kineo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ownhealth.kineo.persistence.Patient;
import com.ownhealth.kineo.persistence.PatientRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsViewModel extends AndroidViewModel {

    private PatientRepository mPatientRepository;
    private final MediatorLiveData<List<Patient>> mObservablePatients;

    public PatientsViewModel(@NonNull Application application, PatientRepository mPatientRepository) {
        super(application);
        this.mPatientRepository = mPatientRepository;

        mObservablePatients = new MediatorLiveData<>();
        mObservablePatients.setValue(null);

        LiveData<List<Patient>> patients = mPatientRepository.getAllPatients();
    }

    public LiveData<List<Patient>> getPatients() {
        return mObservablePatients;
    }

    public void addPatient(Patient patient) {
        mPatientRepository.insertPatient(patient).observeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io()).subscribe();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final PatientRepository mPatientRepository;

        public Factory(@NonNull Application application, PatientRepository patientRepository) {
            this.mApplication = application;
            this.mPatientRepository = patientRepository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new PatientsViewModel(mApplication, mPatientRepository);
        }
    }
}
