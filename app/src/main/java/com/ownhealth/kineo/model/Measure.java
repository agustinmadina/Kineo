package com.ownhealth.kineo.model;

/**
 * Created by Agustin Madina on 3/28/2018.
 */

public class Measure {

    //esto podria ser un enum
    private String joint;
    //diferentes movimientos, por ahora ponemos x e y, podria ser otro enum
    private String movement;
    private int measuredAngle;
    private int patientId;
    private String patientName;

    public Measure(String joint, String movement, int measuredAngle, int patientId, String patientName) {
        this.joint = joint;
        this.movement = movement;
        this.measuredAngle = measuredAngle;
        this.patientId = patientId;
        this.patientName = patientName;
    }

    public String getJoint() {
        return joint;
    }

    public void setJoint(String joint) {
        this.joint = joint;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public int getMeasuredAngle() {
        return measuredAngle;
    }

    public void setMeasuredAngle(int measuredAngle) {
        this.measuredAngle = measuredAngle;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
