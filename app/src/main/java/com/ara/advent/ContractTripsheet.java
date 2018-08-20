package com.ara.advent;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.Adapter.AutoComTxtAdapter;
import com.ara.advent.Adapter.RouteAndVehicleAdapter;
import com.ara.advent.models.AutoComTxtModel;
import com.ara.advent.models.Customer;
import com.ara.advent.http.MySingleton;
import com.ara.advent.Adapter.CustomerAdapter;
import com.ara.advent.models.RouteAndvehicleModel;
import com.ara.advent.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.BOOKING_URL;
import static com.ara.advent.utils.AppConstants.CUSTOMER_ID_FOR_SELECTING;
import static com.ara.advent.utils.AppConstants.PARAM_CLOSEDATE;
import static com.ara.advent.utils.AppConstants.PARAM_CLOSETIME;
import static com.ara.advent.utils.AppConstants.PARAM_CUSTOMERID;
import static com.ara.advent.utils.AppConstants.PARAM_ENDKM;
import static com.ara.advent.utils.AppConstants.PARAM_STARTDATE;
import static com.ara.advent.utils.AppConstants.PARAM_STARTKM;
import static com.ara.advent.utils.AppConstants.PARAM_STARTTIME;
import static com.ara.advent.utils.AppConstants.PARAM_TARIFF_ID;
import static com.ara.advent.utils.AppConstants.PARAM_TOTALKM;
import static com.ara.advent.utils.AppConstants.PARAM_TOTALTIME;
import static com.ara.advent.utils.AppConstants.PARAM_VEHICELNO;
import static com.ara.advent.utils.AppConstants.VEHICLE_NO_URL;


public class ContractTripsheet extends AppCompatActivity implements TextWatcher {

    private final static String TAG = "Contract TripSheet";


    int i1, i2, substract;
    String val_tarrif_id, vehNo, vehId, name;

    long diffMinutes, diffHours, diffDays;
    String valCusId, valvehicletype, valvehicleno, valroute, valclosedate, valstartdate, valstartkm, valendkm, valtotalkm, valstarttime, valendtime, valtotaltime;
    String vehiclename, vehicleid, customername, customerid, dstart, dend;
    String stime, ctime;

    @BindView(R.id.ContractTrip_scrollLayout)
    ScrollView tripScroll;
    @BindView(R.id.Contract_spinner_Customer)
    Spinner customer_spinner;
    @BindView(R.id.spinner_vehicle_type)
    Spinner vehicleType_spinner;
    @BindView(R.id.input_VehicleNo)
    TextView autoCompleteTextView;
    @BindView(R.id.input_date)
    TextView startingDate;
    @BindView(R.id.input_default_date)
    TextView closingDate;
    @BindView(R.id.input_start_km)
    EditText startingKm_Edittext;
    @BindView(R.id.input_closing_km)
    EditText closingKm_Edittext;
    @BindView(R.id.input_total_km)
    TextView totalKm_Edittext;
    @BindView(R.id.input_start_time)
    TextView start_time;
    @BindView(R.id.input_closing_time)
    TextView close_time;
    @BindView(R.id.input_total_time)
    TextView total_time;
    @BindView(R.id.btn_booking)
    Button booking_button;
    @BindView(R.id.edittext_starthoursInput)
    EditText startHoursInput;
    @BindView(R.id.edittext_startMinutesInput)
    EditText startMinutesInput;
    @BindView(R.id.edittext_endhoursInput)
    EditText closeHoursInput;
    @BindView(R.id.edittext_endMinutesInput)
    EditText closeMinutesInput;


    DatePickerDialog datePickerDialog;
    Boolean start_date_changed = false;
    Boolean close_date_changed = false;
    ArrayList<String> auto = new ArrayList<String>();
    ArrayList<Customer> customer = new ArrayList<Customer>();
    ArrayList<RouteAndvehicleModel> randvModel = new ArrayList<RouteAndvehicleModel>();
    ArrayList<AutoComTxtModel> autocomplete = new ArrayList<AutoComTxtModel>();
    EditText[] editTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_tripsheet);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tripScroll.setFocusableInTouchMode(true);
        tripScroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);


        editTexts = new EditText[]{startingKm_Edittext, closingKm_Edittext};
        for (int i = 0; i < editTexts.length; i++) {
            editTexts[i].addTextChangedListener(this);

        }
        EditText[] editTexts1 = new EditText[]{startHoursInput, startMinutesInput};
        for (int j = 0; j < editTexts1.length; j++) {
            editTexts1[j].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                   /* if (startHoursInput.getText().toString().length() == 2) {
                        startMinutesInput.requestFocus();
                    }*/

                }

                @Override
                public void afterTextChanged(Editable s) {

                   /* if (startMinutesInput.getText().toString().length() == 2) {
                        closeHoursInput.requestFocus();
                    }*/

                }
            });
        }

        EditText[] editTexts2 = new EditText[]{closeHoursInput, closeMinutesInput};
        for (int k = 0; k < editTexts2.length; k++) {
            editTexts2[k].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
/*
                    if (closeHoursInput.getText().toString().length() == 2) {
                        closeMinutesInput.requestFocus();
                    }*/
                }

                @Override
                public void afterTextChanged(Editable s) {

//                    if (closeMinutesInput.getText().toString().length() == 2 && startMinutesInput.getText().toString().length() == 2) {
//
//                        hideSoftKeyboard(closeMinutesInput);

                        calTimeDiff();
//                    }
                }
            });
        }


        if (isNetworkAvailable()) {

            customer.clear();
            getCustomerJson();

        } else {

            showSnackbar("Something went wrong, Check Network connection!");
        }

        closingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDate();
            }
        });
        startingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate();
            }
        });


        vehicleType_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                RouteAndvehicleModel routeAndvehicleModel = randvModel.get(position);
                val_tarrif_id = routeAndvehicleModel.getId();
                Log.e(TAG, "valtarrifid" + val_tarrif_id);
                autocomplete.clear();

                getVehicleNoJson(val_tarrif_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        booking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendDataForBooking();

            }
        });


    }


    private void getRouteAndVehicletype(final String value) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.CUSTOMER_SELECTING_SPINNER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "customer selecting value in spinner : " + response);

                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        String contract_tariff_id = jsonObject.getString("contract_tariff_id");
                        name = jsonObject.getString("name");

                        randvModel.add(new RouteAndvehicleModel(contract_tariff_id, name));
                    }

                    RouteAndVehicleAdapter routeAndvehicleModel = new RouteAndVehicleAdapter(ContractTripsheet.this, android.R.layout.simple_spinner_item, randvModel);
                    vehicleType_spinner.setAdapter(routeAndvehicleModel);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "EXCEPTION_CUSTOMERLIST-" + e);

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "customer selecting error value in spinner : " + error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(CUSTOMER_ID_FOR_SELECTING, value);
                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    public boolean formValid() {

        boolean error = true;

        String skm = startingKm_Edittext.getText().toString();
        String ckm = closingKm_Edittext.getText().toString();
        String stHIn = startHoursInput.getText().toString();
        String stMIn = startMinutesInput.getText().toString();
        String cHin = closeHoursInput.getText().toString();
        String cMin = closeMinutesInput.getText().toString();


        if (skm.isEmpty()) {
            startingKm_Edittext.setError("starting km not valid");
            error = false;
        } else {
            startingKm_Edittext.setError(null);
        }
        if (ckm.isEmpty() || Integer.parseInt(ckm) < Integer.parseInt(skm)) {
            closingKm_Edittext.setError("Closing km not valid");
            error = false;

        } else {
            closingKm_Edittext.setError(null);
        }

        if (stHIn.isEmpty() || Integer.parseInt(stHIn) > 24) {
            startHoursInput.setError("Hours not valid");
            error = false;
        } else {
            startHoursInput.setError(null);
        }
        if (stMIn.isEmpty() || Integer.parseInt(stMIn) > 60) {
            startMinutesInput.setError("minutes not valid");
            error = false;
        } else {
            startMinutesInput.setError(null);
        }

        if (cHin.isEmpty() || Integer.parseInt(cHin) > 24) {
            closeHoursInput.setError("Hours not valid");
            error = false;
        } else {
            closeHoursInput.setError(null);
        }
        if (cMin.isEmpty() || Integer.parseInt(cMin) > 60) {
            closeMinutesInput.setError("minutes not valid");
            error = false;
        } else {
            closeMinutesInput.setError(null);
        }
        if (start_date_changed == false && close_date_changed == true) {
            showSnackbar("starting Date is required");
            error = false;
        }

        if (close_date_changed == false && start_date_changed == true) {
            showSnackbar("closing date is required");
            error = false;
        }

        if (start_date_changed == false && close_date_changed == false) {
            showSnackbar("Enter Both Starting and closing Date");
            error = false;
        }
        return error;
    }


    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void calTimeDiff() {

        stime = startHoursInput.getText().toString() + ":" + startMinutesInput.getText().toString();
        ctime = closeHoursInput.getText().toString() + ":" + closeMinutesInput.getText().toString();
        String dateStarted = startingDate.getText().toString() + " " + stime;
        String dateClosed = closingDate.getText().toString() + " " + ctime;

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.ENGLISH);

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStarted);
            d2 = format.parse(dateClosed);

            long diff = d2.getTime() - d1.getTime();

            diffMinutes = diff / (60 * 1000) % 60;
            diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);

            Log.e(TAG, "diffrence days" + diffDays);
            Log.e(TAG, "diffrence hours" + diffHours);
            Log.e(TAG, "diffrence minutes" + diffMinutes);


        } catch (ParseException e) {
            Log.e(TAG, "parse Exception-" + e);

        }

        total_time.setText(diffHours + ":" + diffMinutes);

//        Toast.makeText(ContractTripsheet.this, String.valueOf(diffHours)+":"+String.valueOf(diffMinutes), Toast.LENGTH_SHORT).show();

    }

    private void startDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(ContractTripsheet.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dstart = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                        startingDate.setText(dstart);
                        start_date_changed = true;
                        Log.e(TAG, " date ---" + dstart);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    private void SendDataForBooking() {


        if (!formValid()) {
            Toast.makeText(ContractTripsheet.this, "Entered details are not valid", Toast.LENGTH_SHORT).show();
//            showSnackbar("please enter all in the field");
            return;
        }
        if (!isNetworkAvailable()) {
            showSnackbar("PLease Check Your Netwok Connection");
            return;
        }

        valstartdate = startingDate.getText().toString();
        valclosedate = closingDate.getText().toString();
        valstartkm = startingKm_Edittext.getText().toString();
        valendkm = closingKm_Edittext.getText().toString();
        valtotalkm = totalKm_Edittext.getText().toString();
        valstarttime = stime;
        valendtime = ctime;
        valtotaltime = total_time.getText().toString();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, BOOKING_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "Booking_DATA_response-" + response);
                if (response.equalsIgnoreCase("success")) {

                    startActivity(new Intent(ContractTripsheet.this, MainActivity.class)
                            .putExtra("bookingSucceed", "TripSheet Added Succesfully"));
                    finish();

                } else {

                    showSnackbar("Data was not sent ." + " please check the details");

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Booking_DATA_error-" + error);
                Snackbar bar = Snackbar.make(tripScroll, "" + error, Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });

                bar.show();


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(PARAM_CUSTOMERID, valCusId);
                map.put(PARAM_VEHICELNO, valvehicleno);
                map.put(PARAM_TARIFF_ID, val_tarrif_id);
                map.put(PARAM_STARTDATE, valstartdate);
                map.put(PARAM_CLOSEDATE, valclosedate);
                map.put(PARAM_STARTKM, valstartkm);
                map.put(PARAM_ENDKM, valendkm);
                map.put(PARAM_TOTALKM, valtotalkm);
                map.put(PARAM_STARTTIME, valstarttime);
                map.put(PARAM_CLOSETIME, valendtime);
                map.put(PARAM_TOTALTIME, valtotaltime);
                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void getCustomerJson() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConstants.CUSTOMER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "customer_response-" + response);

                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        customerid = jsonObject.getString("customerId");
                        customername = jsonObject.getString("customername");

                        customer.add(new Customer(customerid, customername));
                    }

                    CustomerAdapter customerAdapter = new CustomerAdapter(ContractTripsheet.this, android.R.layout.simple_spinner_item, customer);
                    customer_spinner.setAdapter(customerAdapter);

                    customer_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Customer selectedname = customer.get(position);
                            valCusId = selectedname.getCus_id();
                            Log.e(TAG, "value customer id inside listener " + valCusId);
                            randvModel.clear();
                            autoCompleteTextView.setText("");
                            getRouteAndVehicletype(valCusId);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "EXCEPTION_CUSTOMERLIST-" + e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "VehicleType_error-" + error);
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getVehicleNoJson(final String Id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VEHICLE_NO_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "response -------------------------------------- " + response);
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        vehNo = jsonObject.getString("vehicle_no");
                        vehId = jsonObject.getString("vehicle_id");


                    }
                    autoCompleteTextView.setText(vehNo);
                    valvehicleno = vehId;

                } catch (JSONException j) {
                    Log.e(TAG, "json exception for" + j);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Error - " + error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map map = new HashMap();
                map.put(PARAM_TARIFF_ID, Id);
                map.put(CUSTOMER_ID_FOR_SELECTING, valCusId);
                return map;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void closeDate() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(ContractTripsheet.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dend = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                        closingDate.setText(dend);
                        dateValidation();
                        close_date_changed = true;
                        Log.e(TAG, " date ---" + dend);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private Boolean dateValidation() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        boolean val = false;
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = simpleDateFormat.parse(startingDate.getText().toString());
            d2 = simpleDateFormat.parse(closingDate.getText().toString());
            if (d1.after(d2)) {
                closingDate.setText("Closing Date");
                showSnackbar("Please Enter Valid end Date");
            }
        } catch (ParseException parse) {
            Log.e(TAG, "Parse Exception -" + parse);
        }
        return val;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


    }

    @Override
    public void afterTextChanged(Editable s) {

        if (startingKm_Edittext.getText().toString().trim().length() != 0 && closingKm_Edittext.getText().toString().trim().length() > 0) {

            i1 = Integer.parseInt(startingKm_Edittext.getText().toString().trim());
            i2 = Integer.parseInt(closingKm_Edittext.getText().toString().trim());
            substract = i2 - i1;
            totalKm_Edittext.setText(substract + "");

        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ContractTripsheet.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }


    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(tripScroll, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


}
