package com.ara.advent.models;

import com.ara.advent.utils.AppConstants;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static com.ara.advent.utils.AppConstants.PARAM_PASSWORD;
import static com.ara.advent.utils.AppConstants.PARAM_TYPE;
import static com.ara.advent.utils.AppConstants.PARAM_USER_NAME;


public class User {
    private String userName;
    private int id;
    private String password;
    private String checkinR,checkOutR,dateR,shiftR;

    public String getCheckinR() {
        return checkinR;
    }

    public void setCheckinR(String checkinR) {
        this.checkinR = checkinR;
    }

    public String getCheckOutR() {
        return checkOutR;
    }

    public void setCheckOutR(String checkOutR) {
        this.checkOutR = checkOutR;
    }

    public String getDateR() {
        return dateR;
    }

    public void setDateR(String dateR) {
        this.dateR = dateR;
    }

    public String getShiftR() {
        return shiftR;
    }

    public void setShiftR(String shiftR) {
        this.shiftR = shiftR;
    }

    public User(){

    }
    public User( int id,String userName) {
        this.userName = userName;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public RequestBody toRequestBody(int type){
        RequestBody requestBody=new FormBody.Builder()
                .add(PARAM_USER_NAME,userName)
                .add(PARAM_PASSWORD,password)
                .add(PARAM_TYPE, String.valueOf(type))
                .build();
        return requestBody;
    }
}
