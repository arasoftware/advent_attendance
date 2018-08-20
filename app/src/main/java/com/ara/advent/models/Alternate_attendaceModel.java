package com.ara.advent.models;

import com.ara.advent.utils.AppConstants;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ara.advent.utils.AppConstants.PARAM_ID;
import static com.ara.advent.utils.AppConstants.PARAM_IMAGE;
import static com.ara.advent.utils.AppConstants.PARAM_LATTITUDE;
import static com.ara.advent.utils.AppConstants.PARAM_LOCATION;
import static com.ara.advent.utils.AppConstants.PARAM_LONGITUDE;
import static com.ara.advent.utils.AppConstants.PARAM_TYPE;
import static com.ara.advent.utils.AppConstants.SHIFT_TYPE;

public class Alternate_attendaceModel {

    String cameraImagepath;
    String id ;
    String locationAddress;
    String lattitude;
    String longitude;
    boolean checkedIn;
    String shift;

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getCameraImagepath() {
        return cameraImagepath;
    }

    public void setCameraImagepath(String cameraImagepath) {
        this.cameraImagepath = cameraImagepath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public RequestBody api(int type) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        MediaType mediaType = MediaType.parse("image/jpeg");
        builder.addFormDataPart(PARAM_IMAGE, getCameraImagepath(),
                RequestBody.create(mediaType, new File(getCameraImagepath())));
        builder.addFormDataPart(PARAM_ID,getId());
        builder.addFormDataPart(PARAM_TYPE, String.valueOf(type));
        builder.addFormDataPart(PARAM_LOCATION,getLocationAddress());
        builder.addFormDataPart(PARAM_LATTITUDE,getLattitude());
        builder.addFormDataPart(PARAM_LONGITUDE,getLongitude());
        MultipartBody multipartBody = builder.build();
        return multipartBody;
    }

    @Override
    public String toString() {
        return PARAM_IMAGE+"-"+getCameraImagepath()+"\n"+
                PARAM_ID+"-"+getId()+"\n"+
                PARAM_LOCATION+"-"+getLocationAddress()+"\n"+
                PARAM_LATTITUDE+"-"+getLattitude()+"\n"+
                PARAM_LONGITUDE +"-"+getLongitude();
    }
}
