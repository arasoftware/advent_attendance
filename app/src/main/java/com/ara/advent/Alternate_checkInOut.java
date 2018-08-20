package com.ara.advent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.advent.http.HttpCaller;
import com.ara.advent.http.HttpRequest;
import com.ara.advent.http.HttpResponse;
import com.ara.advent.models.Alternate_attendaceModel;
import com.ara.advent.models.User;
import com.ara.advent.utils.AppConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.ara.advent.utils.AppConstants.CHECK_IN;
import static com.ara.advent.utils.AppConstants.CHECK_OUT;
import static com.ara.advent.utils.AppConstants.MY_CAMERA_REQUEST_CODE;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;
import static com.ara.advent.utils.AppConstants.REQUEST_IMAGE_CAPTURE;
import static com.ara.advent.utils.AppConstants.SUCCESS_MESSAGE;
import static com.ara.advent.utils.AppConstants.user;

public class Alternate_checkInOut extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    long diffMinutes;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2 * 1000; /* 2 sec */
    String progressMessage, snackmessage;
    private LocationManager locationManager;
    private static final String TAG = "Alternate_CheckInOUt";
    private static final int REQUEST_TAKE_IMAGE_ONE = 1;
    @BindView(R.id.ConstraintCheckInOut)
    ConstraintLayout constraintLayout;
    @BindView(R.id.userCamera_Button)
    CardView onClickCamera;
    @BindView(R.id.username_text)
    TextView usenameView;
    @BindView(R.id.userimageView)
    ImageView userImageView;
    @BindView(R.id.todaydate_text)
    TextView todayDateView;
    @BindView(R.id.locationAdd_text)
    TextView locationView;
    @BindView(R.id.checkin_text)
    TextView checkinview;
    @BindView(R.id.checkOut_text)
    TextView checkOutView;
    @BindView(R.id.checkin_btn)
    Button onClickCheckIN;
    @BindView(R.id.checkout_btn)
    Button onClickCheckOUT;
    @BindView(R.id.showalert)
    Button buttonAlert;
    String userId;
    private static final int PERMISSION_REQUEST_CODE = 200;
    String starting_date, closin_date;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;
    Alternate_attendaceModel attendaceModel = new Alternate_attendaceModel();
    boolean alreadyCheckin = false;
    boolean GPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_check_in_out);
        ButterKnife.bind(this);
        if (!isNetworkAvailable()) {
            showSnackbar("PLease Check Your Internet Connection");
        }
        initializeViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if (checkPermission()) {
            showSnackbar("permisssion Granted");
        } else {
            showSnackbar("request Permisssion");
            requestPermission();
        }

    }

    //    __________________________________________________________________________________________________________

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLocationEnabled()) {
            showAlert();
        } else {
            if (mGoogleApiClient.isConnected()){
                startLocationUpdates();
                showSnackbar("Loation is enabled");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        showSnackbar("Permission Granted");
                        checkLocation();
                    } else {
                        showSnackbar("Permission Denied");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermission();
                        }

                    }
                }


                break;
        }
    }


    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog_one);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        paramDialogInterface.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.dismiss();
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //    __________________________________________________________________________________________________________

    private void initializeViews() {

        String todayDate_default = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        todayDateView.setText(todayDate_default);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String currentTimeForShift_ = mdformat.format(calendar.getTime());
        checkOutView.setText(currentTimeForShift_);
        checkinview.setText(currentTimeForShift_);

        final SharedPreferences sharedPreferences1 = getSharedPreferences("user", MODE_PRIVATE);
        userId = sharedPreferences1.getString("uid", "");
        attendaceModel.setId(userId);
        String username = sharedPreferences1.getString("username", "");
        Log.e(TAG, "id -- " + userId);
        if (username != "") {

            usenameView.setText(username);
        }

        SharedPreferences sharedPreferences11 = getSharedPreferences("s", MODE_PRIVATE);
        String logstatus = sharedPreferences11.getString("logstatus", "");
        String logtime = sharedPreferences11.getString("logtime", "");
        Log.e(TAG, "log status and time " + logstatus + "- " + "- " + logtime);
        String logdate = sharedPreferences11.getString("logdate", "");
        if (logstatus.equalsIgnoreCase("2")) {
            if (logtime != "") {
                checkOutView.setText(logtime);
            }

            onClickCheckOUT.setEnabled(false);
            onClickCheckOUT.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.graly, null));

        } else {
            if (logtime != "") {
                checkinview.setText(logtime);
            }
            onClickCheckIN.setEnabled(false);
            onClickCheckIN.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.graly, null));


        }

        onClickCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestPermissionForCamera(REQUEST_TAKE_IMAGE_ONE);
            }
        });
        onClickCheckIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInOut(CHECK_IN);

            }
        });

        onClickCheckOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkInOut(CHECK_OUT);


            }
        });
        buttonAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("alert", MODE_PRIVATE);
                String json = sharedPreferences.getString("json", "");
                String response = sharedPreferences.getString("failres", "");
                String excep = sharedPreferences.getString("excep", "");
                String model = sharedPreferences.getString("model", "");

                if (json != "" || response != "" || excep != "") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Alternate_checkInOut.this);
                    builder.setTitle("Response Data And Exceptions : ");
                    builder.setMessage("FAil Response : \n" + response + "\n "
                            + "json Response : \n" + json + "\n "
                            + "Exception Response : \n" + excep + "\n "
                            + "MODel Response : \n" + model);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

    }


    //--------------------------------------------------------------------------------------------------------------------
    private boolean hasGoogleService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int apiAvailableStatusCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailableStatusCode == ConnectionResult.SUCCESS) {
            Log.i(TAG, "Play API Available");
            return true;
        } else {
            if (googleApiAvailability.isUserResolvableError(apiAvailableStatusCode)) {
                googleApiAvailability.showErrorNotification(this, apiAvailableStatusCode);
            }
        }
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {

//             mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
//            mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));

        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String strAdd = "";
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String loc = addresses.get(0).getAddressLine(0);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w(TAG, strAdd);
            } else {
                Log.w(TAG, "No Address returned!");
            }
              /*  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();*/

                attendaceModel.setLattitude(String.valueOf(location.getLatitude()));
                attendaceModel.setLongitude(String.valueOf(location.getLongitude()));
                attendaceModel.setLocationAddress(strAdd);
                locationView.setText(strAdd);
             /*   showSnackbar(address+" , "+city+" , "+state+" , "+country+" , "+postalCode+" , "+knownName);
                Toast.makeText(this,address+" , "+city+" , "+state+" , "+country+" , "+postalCode+" , "+knownName , Toast.LENGTH_SHORT).show();
                Log.e(TAG,"location ------------------"+address+" , "+city+" , "+state+" , "+country+" , "+postalCode+" , "+knownName);
*/

        } catch (Exception e) {
            Log.e(TAG, "ERROR--" + e);
        }
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

//-----------------------------------------------------------------------------------------------------------------------

    public boolean validate() {
        boolean valid = true;

        if (attendaceModel.getCameraImagepath() == null) {
            showSnackbar("Photo Not Updated");
            valid = false;
        }
        if (attendaceModel.getLocationAddress() == null) {
            Toast.makeText(this, "Location Not getted", Toast.LENGTH_SHORT).show();
            valid = false;

        }
        if (todayDateView.getText().toString().equalsIgnoreCase("DD/MM/YYYY")) {
            showSnackbar("Date not Updated");
            valid = false;
        }


        return valid;
    }

    private void checkInOut(final int type) {

        if (!validate()) {
            return;
        }
        if (!isNetworkAvailable()) {
            showSnackbar("please Check Your Mobile Internet Connection");
            return;
        }

        if (type == CHECK_IN) {
            progressMessage = "check in";
            snackmessage = "Checked In Successfully";

        } else if (type == CHECK_OUT) {
            progressMessage = "check out";
            snackmessage = "Checked Out Succesfully";
        }

        HttpRequest httpRequest = new HttpRequest(AppConstants.getSaveAction());
        httpRequest.setRequestBody(attendaceModel.api(type));
        try {
            new HttpCaller(this, progressMessage) {
                @Override
                public void onResponse(HttpResponse response) {
                    super.onResponse(response);

                    if (response.getStatus() == HttpResponse.ERROR) {
                        SharedPreferences sharedPreferences = getSharedPreferences("alert", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("failres", response.getMesssage());
                        editor.commit();
                        onFailed(response);
                    } else {
                        String res = response.getMesssage();
                        Log.e(TAG, "Alternate Check in / out Response : " + res);
                        Log.e(TAG, "Alternate Check in / out Response : " + attendaceModel);

                        SharedPreferences sharedPreferences = getSharedPreferences("alert", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("json", res);
                        editor.putString("model", attendaceModel.toString());
                        editor.commit();
                        responseParsing(res, type);
                    }
                }

            }.execute(httpRequest);

        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
            SharedPreferences sharedPreferences = getSharedPreferences("alert", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("excep", exception.getMessage());
            editor.commit();
            showSnackbar("Something went wrong, contact Ara software", false);
        }

    }

    private void responseParsing(String response, int type) {


        JSONObject jsonObject = null;
        JSONObject jsonObject1 = null;
        String time = null;
        String msg = null;
        Pattern p = Pattern.compile(".*([01]?[0-9]|2[0-3]):[0-5][0-9].*");


        try {

            jsonObject = new JSONObject(response);
            msg = jsonObject.getString("msg");
            time = jsonObject.getString("time");

            if (msg.equalsIgnoreCase("Checkin Success")) {
                showSnackbar(msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                onClickCheckIN.setEnabled(false);
                onClickCheckIN.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.graly, null));
                onClickCheckOUT.setEnabled(true);
                onClickCheckOUT.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_dark, null));
                Matcher matcher = p.matcher(time);
                if (matcher.matches()) {
                    checkinview.setText(time);


                }
                attendaceModel.setCameraImagepath(null);
                userImageView.setImageResource(R.drawable.camera_icon);
            } else if (msg.equalsIgnoreCase("Checkout Success")) {
                showSnackbar(msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                onClickCheckOUT.setEnabled(false);
                onClickCheckOUT.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.graly, null));
                onClickCheckIN.setEnabled(true);
                onClickCheckIN.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.primary_dark, null));
                Matcher matcher = p.matcher(time);
                if (matcher.matches()) {
                    checkOutView.setText(time);

                }
                attendaceModel.setCameraImagepath(null);
                userImageView.setImageResource(R.drawable.camera_icon);
            } else if (msg.equalsIgnoreCase("U Already Checked In")) {
                showSnackbar(msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                Matcher matcher = p.matcher(time);
                if (matcher.matches()) {
                    checkinview.setText(time);

                }
                attendaceModel.setCameraImagepath(null);
                userImageView.setImageResource(R.drawable.camera_icon);
            } else if (msg.equalsIgnoreCase("U Already Checked Out")) {
                showSnackbar(msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                Matcher matcher = p.matcher(time);
                if (matcher.matches()) {
                    checkOutView.setText(time);

                }
                attendaceModel.setCameraImagepath(null);
                userImageView.setImageResource(R.drawable.camera_icon);
            } else if (msg.equalsIgnoreCase("fail")) {
                showSnackbar(msg + " " + "Data Was Not Sent");
                Toast.makeText(this, msg + " " + "Data Was Not Sent", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                attendaceModel.setCameraImagepath(null);
                userImageView.setImageResource(R.drawable.camera_icon);
            }

        } catch (JSONException ex) {
            Log.e(TAG, "JSOn EXception : " + ex);

        }
    }


    private void onFailed(HttpResponse response) {
        if (response != null) {
            showSnackbar("Something went wrong, Check Network connection!");
            Log.e(TAG, response.getMesssage());

        }
        if (response.getMesssage().compareToIgnoreCase(AppConstants.SUCCESS_MESSAGE) != 0) {
            showSnackbar(response.getMesssage(), false);
            return;
        }
    }

    private void requestPermissionForCamera(int request) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                showSnackbar("This App needs Camera", true);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_CAMERA_REQUEST_CODE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dispatchTakePictureIntent(request);
            } else {
                cameraForOld(request);
            }

        }
    }

    private void cameraForOld(int REQUEST_TAKE_PHOTO) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage(), ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void dispatchTakePictureIntent(int REQUEST_TAKE_PHOTO) {
        Context context = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getMessage(), ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.ara.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void compressImageFile(Bitmap bitmap, String fileName) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 12, outputStream);


            File file = new File(fileName);
            file.deleteOnExit();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] array = outputStream.toByteArray();
            fileOutputStream.write(array);
            fileOutputStream.close();


        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage(), exception);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap image = BitmapFactory.decodeFile(attendaceModel.getCameraImagepath());
            compressImageFile(image, attendaceModel.getCameraImagepath());
            userImageView.setImageBitmap(image);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED && alreadyCheckin == true) {
            attendaceModel.setCameraImagepath(null);
            alreadyCheckin = false;
            showSnackbar("user cancel Image capture");

        } else {

            attendaceModel.setCameraImagepath(null);
            alreadyCheckin = false;
            showSnackbar("Something went wrong while Capture image");

        }
    }

    private File createImageFile(int REQUEST) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" +
                "" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        String mCurrentPhotoPath;
        mCurrentPhotoPath = image.getAbsolutePath();
        if (REQUEST == 1) {
            attendaceModel.setCameraImagepath(mCurrentPhotoPath);
        }

        return image;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(constraintLayout, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void showSnackbar(String message, final boolean finishApp) {
        final Snackbar snackbar = Snackbar.make(constraintLayout, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if (finishApp)
                    finish();

            }
        });
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences11 = getSharedPreferences("s", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences11.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(Alternate_checkInOut.this, MainActivity.class));
        finish();
    }

}
