package com.ownhealth.kineo.persistence.Patient;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/5/2018.
 * Similar to DataSource, Access point for managing user data.
 */

public interface PatientRepository {

    LiveData<Patient> getPatient(int patientId);

    LiveData<List<Patient>> getAllPatients();

    Completable insertPatient(Patient patient);

    Completable deleteAllPatients();

    Completable updatePatient(Patient patient);

    Completable deletePatient(Patient patient);
}
