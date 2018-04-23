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

package com.ownhealth.kineo.persistence.Patient;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Data Access Object for the users table.
 */
@Dao
public interface PatientDao {

    /**
     * Get the user from the table. Since for simplicity we only have one user in the database,
     * this query gets all users from the table, but limits the result to just the 1st user.
     *
     * @return the user from the table
     */
    @Query("SELECT * FROM " + Patient.TABLE_NAME + " WHERE id=:patientId")
    LiveData<Patient> getPatient(int patientId);

    @Query("SELECT * FROM Patients")
    LiveData<List<Patient>> getAllPatients();

    /**
     * Insert a patient in the database. If the patient already exists, replace it.
     *
     * @param patient the patient to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPatient(Patient patient);

    /**
     * Delete all users.
     */
    @Query("DELETE FROM " + Patient.TABLE_NAME)
    void deleteAllPatients();

    @Update
    void updatePatient(Patient patient);

    @Delete
    void deletePatient(Patient patient);
}
