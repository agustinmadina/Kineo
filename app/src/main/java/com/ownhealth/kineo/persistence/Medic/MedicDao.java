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

package com.ownhealth.kineo.persistence.Medic;

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
public interface MedicDao {

    /**
     * Get the user from the table. Since for simplicity we only have one user in the database,
     * this query gets all users from the table, but limits the result to just the 1st user.
     *
     * @return the user from the table
     */
    @Query("SELECT * FROM " + Medic.TABLE_NAME + " WHERE email=:email AND password =:password")
    LiveData<Medic> getMedicByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM " + Medic.TABLE_NAME)
    LiveData<List<Medic>> getAllMedics();

    /**
     * Insert a medic in the database. If the medic already exists, replace it.
     *
     * @param medic the medic to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPatient(Medic medic);

    /**
     * Delete all users.
     */
    @Query("DELETE FROM " + Medic.TABLE_NAME)
    void deleteAllMedics();

    @Update
    void updateMedic(Medic medic);

    @Delete
    void deleteMedic(Medic medic);
}
