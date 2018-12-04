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

package com.ownhealth.kineo.persistence.Measure;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Data Access Object for the users table.
 */
@Dao
public interface MeasureDao {

    /**
     * Get the measure from the table. Since for simplicity we only have one user in the database,
     * this query gets all users from the table, but limits the result to just the 1st user.
     *
     * @return the user from the table
     */
    @Query("SELECT * FROM " + Measure.TABLE_NAME + " WHERE patientId=:patientId")
    LiveData<List<Measure>> getMeasuresFromPatient(int patientId);

    @Query("SELECT * FROM " + Measure.TABLE_NAME + " WHERE patientId=:patientId AND joint=:joint AND movement=:movement")
    LiveData<List<Measure>> getMeasuresForPatientForJointForMovement(int patientId, String joint, String movement);

    @Query("SELECT * FROM " + Measure.TABLE_NAME + " WHERE patientAge >=:startAge AND patientAge <=:endAge")
    LiveData<List<Measure>> getMeasuresBetweenAges(int startAge, int endAge);

    @Query("SELECT * FROM " + Measure.TABLE_NAME)
    LiveData<List<Measure>> getAllMeasures();

//    // Select all from Task table and order by "complete by" date
//    @Query("SELECT * FROM " + Measure.TABLE_NAME + " ORDER By " + Task.COMPLETE_BY_DATE)
//    LiveData<List<Measure>> getAllTasks();
//
//    // Select one task from Task table by id
//    @Query("SELECT * FROM " + Task.TABLE_NAME + " WHERE id=:id")
//    LiveData<Measure> getTaskById(String id);

    /**
     * Insert a measure in the database
     *
     * @param measure the measure to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeasure(Measure measure);

    @Delete
    void deleteMeasure(Measure measure);

    /**
     * Delete all users.
     */
    @Query("DELETE FROM " + Measure.TABLE_NAME)
    void deleteAllMeasures();
}
