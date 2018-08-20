package com.ara.advent.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.ara.advent.models.User;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class AppConstants {

    public static final String SHIFT_TYPE = "shift";
    public static String PREFERENCE_NAME = "amaze_advent.ara";
    public static final int MAIN_REQUEST_CODE = 0;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int MY_CAMERA_REQUEST_CODE = 100;

    private static final String URL = "http://amazetravels.in/atnc-app/android_atncfile.php?action=";
    private static final String SAVE_ACTION = "save";
    private static final String LOGIN_ACTION = "login_details";

    public static String getLoginAction() {
        return URL + LOGIN_ACTION;
    }

    public static final String ATTENDANCEOBJ = "atncObj";
    public static String getSaveAction() {
        return URL + SAVE_ACTION;
    }
    public static final String PREF_TYPE = "TYPE";
    public static final String PARAM_USER_ID = "userid";
    public static final String PARAM_USER_NAME = "username";
    public static final String PARAM_LOGIN = "login";
    public static final String PARAM_PASSWORD = "password";
    public static final String LOGIN_RESULT = "login";
    public static String SUCCESS_MESSAGE = "success";
    private static final String PARAMA_CHECK_IN_TIME = "checkin_time";

    public static final int CHECK_IN = 1;
    public static final int CHECK_OUT = 2;

    public static final int USER_TYPE = 1;
    public static final int DRIVER_TYPE = 2;

    public static final String CUSTOMER_SELECTING_SPINNER_URL = URL + "customerstation";
    public static final String CUSTOMER_ID_FOR_SELECTING = "customerId";

    public static final String PARAM_IMAGE = "userimage";
    public static final String PARAM_ID = "id";
    public static final String PARAM_LOCATION = "location";
    public static final String PARAM_LATTITUDE = "lattitude";
    public static final String PARAM_LONGITUDE = "longitude";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_CHECK_IN = "checkin";
    public static final String PARAM_CHECK_OUT = "checkout";

    public static final String LOG_DETTIAL = URL + "log_details";
    public static final String CHECK_IN_DATE = "CheckInDate";

    public static final String CUSTOMER_URL = URL + "customerList";
    public static  final String BOOKING_URL = URL + "tripsheetentry";
    public static final String VEHICLE_NO_URL = URL + "lastvehicleno";
    public static final  String PARAM_CUSTOMERID = "custid";
    public static final  String PARAM_VEHICELNO = "vehicle_no";
    public static final  String PARAM_TARIFF_ID = "tariff_id";
    public static  final String PARAM_STARTDATE = "start_date";
    public static final  String PARAM_CLOSEDATE = "close_date";
    public static final  String PARAM_STARTKM = "startkm";
    public static final  String PARAM_ENDKM = "endkm";
    public static  final String PARAM_TOTALKM = "totalkm";
    public static final  String PARAM_STARTTIME = "starttime";
    public static  final String PARAM_CLOSETIME = "endtime";
    public static  final String PARAM_TOTALTIME = "totaltime";
    public static final String PARAM_CUSTOMER_ID = "customerId";
    public static final String ONCALLTRIPSHEETURL = URL+"oncaltripsheetupdate";
    public static final String PARAM_VEHICLE_ID = "vehicleid";
    public static final String ONCALLTRIPSHEETUPDATE = "oncaltripsheetupdate";
    public static final String PARAM_TRIPSHEET_ID = "tripid";
    public static final String PARAM_CLOSING_KM_ONCALL = "closingkm";
    public static final String PARAM_CLOSIN_TIME_ONCALL = "closingtime";
    public static final String PARAM_DEF_DATE = "";
    public static final String PARAM_TRIPSHEETFRONT = "tripsheetbill";
    public static final String PARAM_PERMITBILL = "permitbill";
    public static final String TOLLGAETEBILL = "tollgatebill";
    public static final String PARKIGNBILL = "parkingbill";
    public static final String PARAM_TRIPSHEETBACK = "tripsheetbillback";
    public static final String PARAMTB = "oncaltripsheetlist";
    public static final String PARAM_PARKAMT = "parkingbillamount";
    public static final String PARAM_PERAMT = "permitbillamount";
    public static final String PARAM_TOLLAMT = "tollgatebillamount";
    public static final String TBURL = URL+PARAMTB;
    public static final String TBID = "trip_booking_id";
    public static final String TBNO = "trip_booking_no";
    public static final String TBDATE ="trip_booking_date" ;
    public static final String TBCNAME ="customer_name" ;
    public static final String TBCMCNAME ="customer_multi_contact_name" ;
    public static final String TBREPORTTO ="trip_booking_report_to" ;
    public static final String TBCSTIME = "trip_closing_customer_starting_time";
    public static final String TBCSSKM = "trip_closing_customer_starting_km";
    public static final String TBCMOBNO = "mobile_no";
    public static final String TBCADDRESS = "address";
    public static final String TBCVEHNAME = "vehicle_type_name";
    public static final String TBVEHID = "vehicle_type_id";
    public static final String PICKUP_TIME="pickup_time";

    public static final String SUB = "oncaltripsheetstartingupdate";
    public static final String STARTINGSUBMITURL = URL +SUB;
    public static final String TRIPID = "tripid";
    public  static final String USERID = "userid";
    public static final String STARTINGKM = "startingkm";
    public static final String STARTINGTIME ="startingtime" ;
    public static final String TOTALKM = "totalkm";
    public static final String TOTALTIME = "totaltime";
    public static final String CLOSINGDATE = "cdate";
    public static final String IP = "ipadd";
    public static  final  String NOTIFYURL = URL + "customernotify";
    public static  final  String HISTORYURL = "";
    public static User user;

    public static void setUser(User tmpUser) {
        user = tmpUser;
    }

    public static User getUser() {
        return user;
    }

    public static User toUser(JSONObject jsonObject) {
        try {

            int id = jsonObject.getInt(PARAM_USER_ID);
            String name = jsonObject.getString(AppConstants.PARAM_USER_NAME);
            user = new User(id, name);
        } catch (Exception e) {
            Log.e("Register Login User", e.getMessage());
        }
        return user;
    }

    public static boolean isToday(String date) {
        return isToday(calendarStringAsCalendar(date));
    }

    public static String timeAsString(Calendar calendar) {
        String am_pm = (calendar.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
        int hour = calendar.get(Calendar.HOUR);
        if (calendar.get(Calendar.HOUR_OF_DAY) == 24) {
            hour = Math.abs(12 - hour);
        }
        int minutes = calendar.get(Calendar.MINUTE);
        String strMinutes = minutes + "";
        if (minutes < 10)
            strMinutes = "0" + strMinutes;
        return hour + ":" + strMinutes
                + " " + am_pm;

    }

    public static Calendar calendarStringAsCalendar(String date) {
        String[] dayMonthYear = date.split("/");
        int day = Integer.parseInt(dayMonthYear[0]);
        int month = Integer.parseInt(dayMonthYear[1]);
        int year = Integer.parseInt(dayMonthYear[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar;
    }

    public static boolean isToday(Calendar attendanceDate) {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.DATE) != attendanceDate.get(Calendar.DATE))
            return false;
        if (today.get(Calendar.MONTH) != attendanceDate.get(Calendar.MONTH))
            return false;
        if (today.get(Calendar.YEAR) != attendanceDate.get(Calendar.YEAR))
            return false;
        return true;

    }

    public static String calendarAsString(Calendar calendar) {

        return calendar.get(Calendar.DATE) + "/"
                + calendar.get(Calendar.MONTH) + "/"
                + calendar.get(Calendar.YEAR);
    }

    public static String getAddressAsString(ArrayList<String> addressFraments) {
        String strAdddress = "";
        for (String address : addressFraments) {
            strAdddress += address + "\n\r";
        }
        return strAdddress;
    }


    public static boolean isNotHalfAnHourDifference(Calendar inTime) {
        Calendar currentTime = Calendar.getInstance();

        if (inTime.get(Calendar.HOUR) != currentTime.get(Calendar.HOUR ))
            return false;
        if ( inTime.get(Calendar.AM_PM) != currentTime.get(Calendar.AM_PM) )
            return false;
        if ((currentTime.get(Calendar.MINUTE) - inTime.get(Calendar.MINUTE)) > 30)
            return false;

        return true;
    }



    public static File createImageFile() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/user.jpg");
//        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/userFile.jpg");
//        try {
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String imageFileName = "JPEG_" + timeStamp + "_";
//            File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
//            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
//            String mCurrentPhotoPath = imageFile.getAbsolutePath();
//            Log.e("Create File", "save a path is :--" + mCurrentPhotoPath);
//            return imageFile;
//        } catch (Exception exception) {
//            Log.e("Create File", exception.getMessage(), exception);
//            return null;
//        }
    }


    public static Calendar timeStringToCalendar(String time) {
        String[] hrsMin = time.split(":");
        int hours = Integer.parseInt(hrsMin[0]);
        String[] minAm_Pm = hrsMin[1].split(" ");
        int minutes = Integer.parseInt(minAm_Pm[0]);
        int am_pm = Calendar.AM;
        if (minAm_Pm[1].compareToIgnoreCase("PM") == 0) {
            am_pm = Calendar.PM;
        }
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) == 24)
            hours += 12;
        calendar.set(Calendar.HOUR, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.AM_PM, am_pm);

        return calendar;

    }

    public static String toAppTimeFormation(String time) {
        if (time.isEmpty() ||
                time.compareToIgnoreCase("null") == 0 ||
                time.compareToIgnoreCase("00:00:00") == 0)
            return null;

        String[] timeSplit = time.split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minutes = Integer.parseInt(timeSplit[1]);
        String am_pm = "AM";
        if (hour > 12) {
            if (hour != 12)
                hour = hour - 12;
            am_pm = "PM";
        }
        return hour + ":" + minutes + " " + am_pm;
    }


}
