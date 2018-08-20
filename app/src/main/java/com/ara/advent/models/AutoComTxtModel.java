package com.ara.advent.models;

/**
 * Created by User on 11-May-18.
 */

public class AutoComTxtModel {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVeh_no() {
        return Veh_no;
    }

    public void setVeh_no(String veh_no) {
        Veh_no = veh_no;
    }


    String id;
    String Veh_no;

    public AutoComTxtModel(String id, String veh_no) {
        this.id = id;
        Veh_no = veh_no;
    }

    public AutoComTxtModel(String veh_no) {
        Veh_no = veh_no;
    }
}
