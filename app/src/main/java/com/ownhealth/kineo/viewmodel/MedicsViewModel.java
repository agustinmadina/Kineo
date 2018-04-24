package com.ownhealth.kineo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ownhealth.kineo.persistence.Medic.Medic;
import com.ownhealth.kineo.persistence.Medic.MedicRepository;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by Agustin Madina on 4/24/2018.
 */
public class MedicsViewModel extends AndroidViewModel {

    private MedicRepository mMedicRepository;

    public MedicsViewModel(@NonNull Application application, MedicRepository medicRepository) {
        super(application);
        this.mMedicRepository = medicRepository;
    }

    public LiveData<List<Medic>> getMedics() {
        return mMedicRepository.getAllMedics();
    }

    public LiveData<Medic> getMedicByEmailAndPassword(String email, String password) {
        return mMedicRepository.getMedicByEmailAndPassword(email, password);
    }

    public Completable addMedic(Medic medic) {
        return mMedicRepository.insertMedic(medic);
    }
    public Completable deleteMedic(Medic medic) {
        return mMedicRepository.deleteMedic(medic);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final MedicRepository mMedicRepository;

        public Factory(@NonNull Application application, MedicRepository medicRepository) {
            this.mApplication = application;
            this.mMedicRepository = medicRepository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new MedicsViewModel(mApplication, mMedicRepository);
        }
    }
}

