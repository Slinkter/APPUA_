package com.cudpast.appminas.Model;

public class AllPersonalMetricas {

    private String dni, name, last, age, address, born, date, phone1, phone2;

    private String tempurature;
    private String so2;
    private String pulse;
    private String symptoms;
    private String dateRegister;
    private String who_user_register;
    private Boolean testpruebarapida;
    private Boolean horario;
    private Boolean s1, s2, s3, s4, s5, s6, s7;

    public AllPersonalMetricas() {
    }

    public String getDni() {
        return dni;
    }

    public String getName() {

        return name;
    }

    public String getLast() {

        return last;
    }

    public String getAge() {

        return age;
    }

    public String getAddress() {

        return address;
    }

    public String getBorn() {

        return born;
    }

    public String getDate() {

        return date;
    }

    public String getPhone1() {

        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getTempurature() {
        return tempurature;
    }

    public String getSo2() {
        return so2;
    }

    public String getPulse() {
        return pulse;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public String getWho_user_register() {
        return who_user_register;
    }

    public Boolean getTestpruebarapida() {
        return testpruebarapida;
    }

    public Boolean getHorario() {
        if (horario == null) {
            horario = false;
        }
        return horario;
    }

    public Boolean getS1() {
        if (s1 == null) {
            s1 = false;
        }
        return s1;
    }

    public Boolean getS2() {
        if (s2 == null) {
            s2 = false;
        }
        return s2;
    }

    public Boolean getS3() {
        if (s3 == null) {
            s3 = false;
        }
        return s3;
    }

    public Boolean getS4() {
        if (s4 == null) {
            s4 = false;
        }
        return s4;
    }

    public Boolean getS5() {
        if (s5 == null) {
            s5 = false;
        }
        return s5;
    }

    public Boolean getS6() {
        if (s6 == null) {
            s6 = false;
        }
        return s6;
    }

    public Boolean getS7() {
        if (s7 == null) {
            s7 = false;
        }
        return s7;
    }

    //

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLast(String last) {
        if (last == null) {
            this.last = "";
        }
        this.last = last;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public void setTempurature(String tempurature) {
        this.tempurature = tempurature;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public void setWho_user_register(String who_user_register) {
        this.who_user_register = who_user_register;
    }

    public void setTestpruebarapida(Boolean testpruebarapida) {
        this.testpruebarapida = testpruebarapida;
    }

    public void setHorario(Boolean horario) {
        this.horario = horario;
    }

    public void setS1(Boolean s1) {

        if (s1 == null) {
            this.s1 = false;
        } else {
            this.s1 = s1;
        }
    }

    public void setS2(Boolean s2) {
        if (s2 == null) {
            this.s2 = false;
        } else {
            this.s2 = s2;
        }
    }

    public void setS3(Boolean s3) {
        if (s3 == null) {
            this.s3 = false;
        } else {
            this.s3 = s3;
        }

    }

    public void setS4(Boolean s4) {
        if (s4 == null) {
            this.s4 = false;
        } else {
            this.s4 = s4;
        }

    }

    public void setS5(Boolean s5) {
        if (s5 == null) {
            this.s5 = false;
        } else {
            this.s5 = s5;
        }

    }

    public void setS6(Boolean s6) {
        if (s6 == null) {
            this.s6 = false;
        } else {
            this.s6 = s6;
        }

    }

    public void setS7(Boolean s7) {
        if (s7 == null) {
            this.s7 = false;
        } else {
            this.s7 = s7;
        }


    }

    public String getAllInfoWorker() {
        String cad = "\n" + "------------------ " + "\n";
        cad = cad +
                "dni = " + dni + "\n" +
                "name = " + name + "\n" +
                "last = " + last + "\n" +
                "age = " + age + "\n" +
                "born = " + born + "\n" +
                "date = " + date + "\n" +
                "phone1 = " + phone1 + "\n" +
                "phone2 = " + phone2 + "\n" +
                "-----" + "------" + "\n" +
                "tempurature = " + tempurature + "\n" +
                "so2 = " + so2 + "\n" +
                "pulse = " + pulse + "\n" +
                "symptoms = " + symptoms + "\n" +
                "dateRegister = " + dateRegister + "\n" +
                "who_user_register = " + who_user_register + "\n" +
                "testpruebarapida = " + testpruebarapida + "\n" +
                "horario = " + horario + "\n" +
                "-----" + "------" + "\n" +
                "s1 = " + s1 + "\n" +
                "s2 = " + s2 + "\n" +
                "s3 = " + s3 + "\n" +
                "s4 = " + s4 + "\n" +
                "s5 = " + s5 + "\n" +
                "s6 = " + s6 + "\n" +
                "s7 = " + s7 + "\n";
        return cad;
    }

    public String getAllInfoWorker2() {
        String cad = "\n" + "------------------ ";
        cad = cad + "\n" +
                " dni = " + dni + "\n" +
                " last = " + last + " , " + name + "\n" +
                " tempurature = " + tempurature + "\n" +
                " so2 = " + so2 + "\n" +
                " pulse = " + pulse + "\n";
        return cad;
    }


}
