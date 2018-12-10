package com.ownhealth.kineo.persistence.Measure;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/5/2018.
 * <p>
 * Using the Room database as a data source. No se si aca va local o como dice en javadoc de abajo implementas aca
 */

public class LocalMeasureRepository implements MeasureRepository {

    private final MeasureDao mMeasureDao;

    public LocalMeasureRepository(MeasureDao measureDao) {
        mMeasureDao = measureDao;
    }

    @Override
    public LiveData<List<Measure>> getMeasuresForPatient(int patientId) {
        return mMeasureDao.getMeasuresFromPatient(patientId);
    }

    @Override
    public LiveData<List<Measure>> getMeasuresForPatientForJointForMovement(int patientId, String joint, String movement) {
        return mMeasureDao.getMeasuresForPatientForJointForMovement(patientId, joint, movement);
    }

    @Override
    public LiveData<List<Measure>> getMeasuresBetweenAges(int startAge, int endAge) {
        return mMeasureDao.getMeasuresBetweenAges(startAge, endAge);
    }

    @Override
    public LiveData<List<Measure>> getAllMeasures() {
        //Here is where we would do more complex logic, like getting events from a cache
        //then inserting into the database etc. In this example we just go straight to the dao.
        return mMeasureDao.getAllMeasures();
    }

    @Override
    public Completable insertMeasure(Measure measure) {
        if (measure == null) {
            return Completable.error(new IllegalArgumentException("Measure cannot be null"));
        }
        return Completable.fromAction(() -> mMeasureDao.insertMeasure(measure));
    }

    @Override
    public Completable deleteMeasure(Measure measure) {
        if (measure == null) {
            return Completable.error(new IllegalArgumentException("Measure cannot be null"));
        }
        return Completable.fromAction(() -> mMeasureDao.deleteMeasure(measure));
    }

    @Override
    public Completable deleteAllMeasures() {
        return Completable.fromAction(mMeasureDao::deleteAllMeasures);
    }

    @Override
    public LiveData<List<Measure>> getMeasuresForJointForMovement(String joint, String movement) {
        return mMeasureDao.getMeasuresForJointForMovement(joint, movement);
    }
}
