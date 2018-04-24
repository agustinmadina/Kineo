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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import static com.ownhealth.kineo.persistence.Medic.Medic.TABLE_NAME;

/**
 * Immutable model class for a Medic
 */
@Entity(tableName = TABLE_NAME)
public class Medic implements Parcelable {
    public static final String TABLE_NAME = "medics";

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "email")
    private String email;

    public Medic() {
    }

    protected Medic(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.password = in.readString();
        this.email = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String surname) {
        this.password = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public static final Creator<Medic> CREATOR = new Creator<Medic>() {
        @Override
        public Medic createFromParcel(Parcel in) {
            return new Medic(in);
        }

        @Override
        public Medic[] newArray(int size) {
            return new Medic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(email);
    }
}
