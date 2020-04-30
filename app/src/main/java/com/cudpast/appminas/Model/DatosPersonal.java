package com.cudpast.appminas.Model;

public class DatosPersonal {

    private String tempurature;
    private String so2;//saturacion
    private String pulse;
    private String symptoms;
    private String dateRegister;

    public DatosPersonal() {

    }

    public DatosPersonal(String tempurature, String so2, String pulse, String symptoms, String dateRegister) {
        this.tempurature = tempurature;
        this.so2 = so2;
        this.pulse = pulse;
        this.symptoms = symptoms;
        this.dateRegister = dateRegister;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public String getTempurature() {
        return tempurature;
    }

    public void setTempurature(String tempurature) {
        this.tempurature = tempurature;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
}
