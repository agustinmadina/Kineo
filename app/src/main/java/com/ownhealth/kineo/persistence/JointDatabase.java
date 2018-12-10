/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ownhealth.kineo.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ownhealth.kineo.persistence.Measure.Measure;
import com.ownhealth.kineo.persistence.Measure.MeasureDao;
import com.ownhealth.kineo.persistence.Medic.Medic;
import com.ownhealth.kineo.persistence.Medic.MedicDao;
import com.ownhealth.kineo.persistence.Patient.Patient;
import com.ownhealth.kineo.persistence.Patient.PatientDao;

/**
 * The Room database that contains the Users table
 */
@Database(entities = {Patient.class, Measure.class, Medic.class}, version = 1)
public abstract class JointDatabase extends RoomDatabase {

    private static volatile JointDatabase sInstance;

    public abstract PatientDao patientDao();

    public abstract MeasureDao measureDao();

    public abstract MedicDao medicDao();

    public static JointDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (JointDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            JointDatabase.class, "PhysioAssist.db")
                            .build();
                }
            }
        }
        return sInstance;
    }

}
