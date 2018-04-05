package com.ownhealth.kineo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.ownhealth.kineo.persistence.Measure;
import com.ownhealth.kineo.persistence.MeasureRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class MeasuresViewModel extends AndroidViewModel {

    private MeasureRepository mMeasureRepository;

    public MeasuresViewModel(@NonNull Application application, MeasureRepository measureRepository) {
        super(application);
        mMeasureRepository = measureRepository;
    }

    public LiveData<List<Measure>> getMeasures() {
        return mMeasureRepository.getAllMeasures();
    }

    //Esto se podria hacer que devuelva un Flowable, Single o Completable y observarlo en la UI para reaccionar ante eventos cuando termine o etc
    public void addMeasure(Measure measure) {
        mMeasureRepository.insertMeasure(measure).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;
        private final MeasureRepository mMeasureRepository;

        public Factory(@NonNull Application application, MeasureRepository measureRepository) {
            this.mApplication = application;
            this.mMeasureRepository = measureRepository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new MeasuresViewModel(mApplication, mMeasureRepository);
        }
    }
}
