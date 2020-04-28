package com.cudpast.appminas.Model;

import com.google.android.material.textfield.TextInputEditText;

public class User {

    private String uid;
    private String reg_email;
    private String reg_password;
    private String reg_name;
    private String reg_dni;
    private String reg_phone;

    public User() {
    }

    public User(String uid, String reg_email, String reg_password, String reg_name, String reg_dni, String reg_phone) {
        this.uid = uid;
        this.reg_email = reg_email;
        this.reg_password = reg_password;
        this.reg_name = reg_name;
        this.reg_dni = reg_dni;
        this.reg_phone = reg_phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReg_email() {
        return reg_email;
    }

    public void setReg_email(String reg_email) {
        this.reg_email = reg_email;
    }

    public String getReg_password() {
        return reg_password;
    }

    public void setReg_password(String reg_password) {
        this.reg_password = reg_password;
    }

    public String getReg_name() {
        return reg_name;
    }

    public void setReg_name(String reg_name) {
        this.reg_name = reg_name;
    }

    public String getReg_dni() {
        return reg_dni;
    }

    public void setReg_dni(String reg_dni) {
        this.reg_dni = reg_dni;
    }

    public String getReg_phone() {
        return reg_phone;
    }

    public void setReg_phone(String reg_phone) {
        this.reg_phone = reg_phone;
    }
}
