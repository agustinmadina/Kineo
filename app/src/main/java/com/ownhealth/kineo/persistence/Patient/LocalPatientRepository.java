package com.ownhealth.kineo.persistence.Patient;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/5/2018.
 * Using the Room database as a data source, if we want backend we should implement in other class
 */

public class LocalPatientRepository implements PatientRepository {

    private final PatientDao mPatientDao;

    public LocalPatientRepository(PatientDao patientDao) {
        mPatientDao = patientDao;
    }

    @Override
    public LiveData<Patient> getPatient(int patientId) {
        return mPatientDao.getPatient(patientId);
    }

    @Override
    public LiveData<List<Patient>> getPatientsForMedic(int medicId) {
        return mPatientDao.getPatientsForMedic(medicId);
    }

    @Override
    public Completable insertPatient(Patient patient) {
        if (patient == null) {
            return Completable.error(new IllegalArgumentException("Medic cannot be null"));
        }
        return Completable.fromAction(() -> mPatientDao.insertPatient(patient));
    }

    @Override
    public Completable deleteAllPatients() {
        return Completable.fromAction(mPatientDao::deleteAllPatients);
    }

    @Override
    public Completable updatePatient(Patient patient) {
        if (patient == null) {
            return Completable.error(new IllegalArgumentException("Medic cannot be null"));
        }
        return Completable.fromAction(() -> mPatientDao.updatePatient(patient));
    }

    @Override
    public Completable deletePatient(Patient patient) {
        if (patient == null) {
            return Completable.error(new IllegalArgumentException("Medic cannot be null"));
        }
        return Completable.fromAction(() -> mPatientDao.deletePatient(patient));
    }
}
