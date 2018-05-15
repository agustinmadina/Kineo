package com.ownhealth.kineo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.persistence.Patient.PatientRepository;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class PatientsViewModel extends AndroidViewModel {

    private PatientRepository mPatientRepository;

    public PatientsViewModel(@NonNull Application application, PatientRepository mPatientRepository) {
        super(application);
        this.mPatientRepository = mPatientRepository;
    }

    public LiveData<List<Patient>> getPatientsForMedic(int medicId) {
        return mPatientRepository.getPatientsForMedic(medicId);
    }

    public LiveData<Patient> getPatient(int patientId) {
        return mPatientRepository.getPatient(patientId);
    }

    public Completable addPatient(Patient patient) {
        return mPatientRepository.insertPatient(patient);
    }
    public Completable deletePatient(Patient patient) {
        return mPatientRepository.deletePatient(patient);
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
