package com.ownhealth.kineo.persistence.Medic;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/5/2018.
 * Using the Room database as a data source, if we want backend we should implement in other class
 */

public class LocalMedicRepository implements MedicRepository {

    private final MedicDao mMedicDao;

    public LocalMedicRepository(MedicDao medicDao) {
        mMedicDao = medicDao;
    }

    @Override
    public LiveData<Medic> getMedic() {
        return mMedicDao.getMedic();
    }

    @Override
    public LiveData<List<Medic>> getAllMedics() {
        return mMedicDao.getAllMedics();
    }

    @Override
    public Completable insertMedic(Medic medic) {
        if (medic == null) {
            return Completable.error(new IllegalArgumentException("Medic cannot be null"));
        }
        return Completable.fromAction(() -> mMedicDao.insertPatient(medic));
    }

    @Override
    public Completable deleteAllMedics() {
        return Completable.fromAction(mMedicDao::deleteAllPatients);
    }

    @Override
    public Completable updateMedic(Medic medic) {
        if (medic == null) {
            return Completable.error(new IllegalArgumentException("Medic cannot be null"));
        }
        return Completable.fromAction(() -> mMedicDao.updateMedic(medic));
    }

    @Override
    public Completable deleteMedic(Medic medic) {
        if (medic == null) {
            return Completable.error(new IllegalArgumentException("Medic cannot be null"));
        }
        return Completable.fromAction(() -> mMedicDao.deleteMedic(medic));
    }
}
