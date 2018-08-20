package com.ara.advent.models;

/**
 * Created by User on 21-Apr-18.
 */

public class Customer {
    public Customer(String cus_id, String cus_name) {
        this.cus_id = cus_id;
        this.cus_name = cus_name;
    }

    public String getCus_id() {
        return cus_id;
    }

    public String getCus_name() {
        return cus_name;
    }

    @Override
    public String toString() {
        return cus_name;
    }

    String cus_id,cus_name;
}
