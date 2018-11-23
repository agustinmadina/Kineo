package com.ownhealth.kineo.persistence.Measure;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static com.ownhealth.kineo.persistence.Measure.Measure.TABLE_NAME;

/**
 * Immutable model class for a Medic
 */
@Entity(tableName = TABLE_NAME)
public class Measure {
    public static final String TABLE_NAME = "measures";

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    //esto podria ser un enum
    @ColumnInfo(name = "joint")
    private String joint;
    //diferentes movimientos, por ahora ponemos x e y, podria ser otro enum
    @ColumnInfo(name = "movement")
    private String movement;
    @ColumnInfo(name = "measured_angle")
    private int measuredAngle;
    @ColumnInfo(name = "patientId")
    private int patientId;
    @ColumnInfo(name = "date")
    private String date;

    public Measure() {
    }

    public Measure(int id, String joint, String movement, int measuredAngle, int patientId, String date) {
        this.id = id;
        this.joint = joint;
        this.movement = movement;
        this.measuredAngle = measuredAngle;
        this.patientId = patientId;
        this.date = date;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getJoint() {
        return joint;
    }

    public String getMovement() {
        return movement;
    }

    public int getMeasuredAngle() {
        return measuredAngle;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public void setJoint(String joint) {
        this.joint = joint;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public void setMeasuredAngle(int measuredAngle) {
        this.measuredAngle = measuredAngle;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
