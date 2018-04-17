package com.ownhealth.kineo.persistence;

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
    public LiveData<Patient> getPatient() {
        return mPatientDao.getPatient();
    }

    @Override
    public LiveData<List<Patient>> getAllPatients() {
        return mPatientDao.getAllPatients();
    }

    @Override
    public Completable insertPatient(Patient patient) {
        if (patient == null) {
            return Completable.error(new IllegalArgumentException("Patient cannot be null"));
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
            return Completable.error(new IllegalArgumentException("Patient cannot be null"));
        }
        return Completable.fromAction(() -> mPatientDao.updatePatient(patient));
    }

    @Override
    public Completable deletePatient(Patient patient) {
        if (patient == null) {
            return Completable.error(new IllegalArgumentException("Patient cannot be null"));
        }
        return Completable.fromAction(() -> mPatientDao.deletePatient(patient));
    }
}
