package com.ownhealth.kineo.persistence;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/5/2018.
 * <p>
 * Using the Room database as a data source.
 */

public class LocalMeasureRepository implements MeasureRepository {

    private final MeasureDao mMeasureDao;

    public LocalMeasureRepository(MeasureDao measureDao) {
        mMeasureDao = measureDao;
    }

    @Override
    public LiveData<Measure> getMeasure() {
        return mMeasureDao.getMeasure();
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
    public Completable deleteAllMeasures() {
        return Completable.fromAction(() -> mMeasureDao.deleteAllMeasures());
    }
}
