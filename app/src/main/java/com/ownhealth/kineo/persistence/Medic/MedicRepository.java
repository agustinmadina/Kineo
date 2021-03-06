package com.ownhealth.kineo.persistence.Medic;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;

/**
 * Created by Agustin Madina on 4/5/2018.
 * Similar to DataSource, Access point for managing user data.
 */

public interface MedicRepository {

    LiveData<Medic> getMedicByEmailAndPassword(String email, String password);

    LiveData<List<Medic>> getAllMedics();

    Single insertMedic(Medic medic);

    Completable deleteAllMedics();

    Completable updateMedic(Medic medic);

    Completable deleteMedic(Medic medic);
}
