package com.ownhealth.kineo.persistence.Measure;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/5/2018.
 * Similar to DataSource, Access point for managing user data.
 */

public interface MeasureRepository {

    LiveData<List<Measure>> getMeasuresForPatient(int patientId);

    LiveData<List<Measure>> getAllMeasures();

    Completable insertMeasure(Measure measure);

    Completable deleteMeasure(Measure measure);

    Completable deleteAllMeasures();
}
