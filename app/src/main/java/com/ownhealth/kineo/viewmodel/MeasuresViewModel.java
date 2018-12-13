package com.ownhealth.kineo.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;

import com.ownhealth.kineo.persistence.Measure.Measure;
import com.ownhealth.kineo.persistence.Measure.MeasureRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static java.lang.StrictMath.abs;

/**
 * Created by Agustin Madina on 4/3/2018.
 */

public class MeasuresViewModel extends AndroidViewModel {

    public static final String Y_AXIS = "Y";
    public static final String X_AXIS = "X";
    public static final String Z_AXIS = "Z";

    private MeasureRepository mMeasureRepository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
//    private final MediatorLiveData<List<Measure>> mObservableMeasures;
    private final MediatorLiveData<Integer> mObservableAngle;

    float[] gData = new float[3];
    private int axisMeasured;
    private float referenceAxis;
    private String axisBeingMeasured;
    private float referenceInitial;
    private int initialDegree = 1;
    private int lastQuarterDegree;
    private float lastQuarterReference;
    private int measuredAngle;
    private boolean mIsMeasuring = false;
    private boolean clockwise;
    private boolean isClockwiseSet = false;

    public MeasuresViewModel(@NonNull Application application, MeasureRepository measureRepository) {
        super(application);
        mMeasureRepository = measureRepository;

//        mObservableMeasures = new MediatorLiveData<>();
//        // set by default null, until we get data from the database.
//        mObservableMeasures.setValue(null);
//        LiveData<List<Measure>> products = mMeasureRepository.getAllMeasures();
//        // observe the changes of the products from the database and forward them
//        mObservableMeasures.addSource(products, mObservableMeasures::setValue);

        mObservableAngle = new MediatorLiveData<>();
        mObservableAngle.setValue(0);
        mObservableAngle.addSource(LiveDataReactiveStreams.fromPublisher(observeCurrentAngle()), mObservableAngle::setValue);
    }

    public LiveData<List<Measure>> getMeasuresForPatient(int patientId) {
        return mMeasureRepository.getMeasuresForPatient(patientId);
    }

    public LiveData<List<Measure>> getAllMeasures() {
        return mMeasureRepository.getAllMeasures();
    }

    public LiveData<List<Measure>> getMeasuresForPatientForJointForMovement(int patientId, String joint, String movement) {
        return mMeasureRepository.getMeasuresForPatientForJointForMovement(patientId, joint, movement);
    }

    public LiveData<List<Measure>> getMeasuresForJointForMovement( String joint, String movement) {
        return mMeasureRepository.getMeasuresForJointForMovement(joint, movement);
    }

    public LiveData<List<Measure>> getMeasuresBetweenAges(int startAge, int endAge) {
        return mMeasureRepository.getMeasuresBetweenAges(startAge, endAge);
    }

    public void setAxisMeasured(String axisBeingMeasured) {
        this.axisBeingMeasured = axisBeingMeasured;
    }

    //Esto se podria hacer que devuelva un Flowable, Single o Completable y observarlo en la UI para reaccionar ante eventos cuando termine o  (lo que hace el de abajo)
    public void addMeasure(Measure measure) {
        mMeasureRepository.insertMeasure(measure).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
    }

    //Esto se podria hacer que devuelva un Flowable, Single o Completable y observarlo en la UI para reaccionar ante eventos cuando termine o  (lo que hace el de abajo)
    public void deleteMeasure(Measure measure) {
        mMeasureRepository.deleteMeasure(measure).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();
    }

    public boolean isMeasuring() {
        return mIsMeasuring;
    }

    public int getMeasuredAngle() {
        return mObservableAngle.getValue();
    }

    public LiveData<Integer> getObservedAngle() {
        return mObservableAngle;
    }

    public String getAxisBeingMeasured() {
        return axisBeingMeasured;
    }

    public void fabStartStopClick() {
        if (mIsMeasuring) {
            referenceInitial = 0;
            initialDegree = 1;
            lastQuarterDegree = 0;
            lastQuarterReference = 0;
            measuredAngle = 0;
        } else {
            initialDegree = axisMeasured;
            lastQuarterDegree = axisMeasured;
            referenceInitial = referenceAxis;
            lastQuarterReference = referenceAxis;
        }
        mIsMeasuring = !isMeasuring();
    }


    private double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
            return null;

        double[] output = new double[input.length];

        for (int i = 0; i < input.length; i++)
            output[i] = input[i];

        return output;
    }

    public void sensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gData[0] = event.values[0];
                gData[1] = event.values[1];
                gData[2] = event.values[2];
                float yaw = (float) (gData[0] / 9.82);
                float pitch = (float) (gData[1] / 9.82);
                float roll = (float) (gData[2] / 9.82);
                int x = (int) Math.round(Math.toDegrees(Math.atan((double) yaw / (double) pitch)));
                int y = (int) Math.round(Math.toDegrees(Math.atan((double) pitch / (double) roll)));
                int z = (int) Math.round(Math.toDegrees(Math.atan((double) roll / (double) yaw)));

                if (axisBeingMeasured.equals(Y_AXIS)) {
                    axisMeasured = y;
                    referenceAxis = pitch;
                } else {
                    axisMeasured = x;
                    referenceAxis = yaw;
                }

                //bug con X en el 3er cuadrante, habria que ver mas adelante
                if (mIsMeasuring && axisMeasured != 0 && abs(axisMeasured) != 90) {
                    evaluateIfClockwise();
                    //problema si initial 0, no entra en este al no tener signo
                    if ((Math.signum(axisMeasured) == Math.signum(initialDegree) && (Math.signum(referenceAxis) == (Math.signum(referenceInitial)))) && measuredAngle < 90) {
                        //Less than 90° turn
                        measuredAngle = abs(axisMeasured - initialDegree);
                        isClockwiseSet = false;
                    } else if (Math.signum(axisMeasured) == Math.signum(initialDegree) && (Math.signum(referenceAxis) == (Math.signum(referenceInitial)))) {
                        //Has already done an entire turn as is on the same quarter as the initial degree
//                        actualDegreeTextView.setText("Maximum reached");
//                        break;
                    } else if (Math.signum(axisMeasured) != Math.signum(initialDegree) && (Math.signum(referenceAxis) == (Math.signum(referenceInitial)) && measuredAngle < 180)) {
                        //Between 90° and 180° turn, Quarter next to it, both up or down
                        measuredAngle = 180 - abs(axisMeasured) - abs(initialDegree);
                    } else if (Math.signum(axisMeasured) == Math.signum(initialDegree)) {
                        if ((Math.signum(axisMeasured) == 1 && clockwise) || (Math.signum(axisMeasured) == -1 && !clockwise)) {
                            //Between 180° and 270° turn
                            measuredAngle = 180 - abs(axisMeasured) + abs(initialDegree);
                        } else {
                            //Between 180° and 270° turn
                            measuredAngle = 180 + abs(axisMeasured) - abs(initialDegree);
                        }
                    } else if ((Math.signum(referenceAxis) != (Math.signum(referenceInitial)) && measuredAngle < 180)) {
                        //Between 90° and 180° turn, both left or right
                        measuredAngle = abs(initialDegree) + abs(axisMeasured);
                    } else if ((Math.signum(referenceAxis) == Math.signum(referenceInitial)) && measuredAngle < 270) {
                        //Between 270° and 360° turn, up or down
                        measuredAngle = 180 + abs(axisMeasured) + abs(initialDegree);
                    } else {
                        //Between 270° and 360° turn, right or left
                        measuredAngle = 360 - abs(axisMeasured) - abs(initialDegree);
                    }
                }
        }
    }

    public Flowable<Integer> observeCurrentAngle() {
        return Flowable.interval(450, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .map(unused -> measuredAngle)
                .distinctUntilChanged();
    }

    private void evaluateIfClockwise() {
        if (Math.signum(axisMeasured) != Math.signum(lastQuarterDegree) && measuredAngle < 90) {
            //cambio cuadrante y chequeo para que lado segun los signos de Y y pitch anteriores y actuales, solo setea en primero cuadrante
            if (!isClockwiseSet) {
                clockwise = ((Math.signum(lastQuarterDegree) == -1) && (Math.signum(referenceAxis) == Math.signum(lastQuarterReference))) || ((Math.signum(lastQuarterDegree) == 1) && (Math.signum(referenceAxis) != Math.signum(lastQuarterReference)));
                isClockwiseSet = true;
                lastQuarterDegree = axisMeasured;
                lastQuarterReference = referenceAxis;
            }
        }
    }

    public void changeAxisClick() {
        if (axisBeingMeasured.equals(Y_AXIS)) {
                axisBeingMeasured = X_AXIS;
            } else {
                axisBeingMeasured = Y_AXIS;
            }
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
