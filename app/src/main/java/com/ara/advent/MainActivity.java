package com.ara.advent;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.http.MySingleton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.LOG_DETTIAL;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;
import static com.ara.advent.utils.AppConstants.USER_TYPE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    @BindView(R.id.layout_scroll_view)
    ScrollView rootLayout;

    @BindView(R.id.goto_CheckInOut)
    Button gotoCheckInOut;

    @BindView(R.id.profileimage)
    ImageView imageView;

    @BindView(R.id.profiletext)
    TextView textUser;
    int CAMERA_REQUEST = 1;
    int GALLERY_REQUEST = 2;
    String id;
    String mCurrentPhotoPath;
    int type = USER_TYPE;
    String picturepath;
    String userid;
    String responseJson, logtime, logdatee, logstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        if (isNetworkAvailable()) {

        } else {
            Snackbar bar = Snackbar.make(rootLayout, "Something went wrong , Check Network connection!", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

            bar.show();
        }
        SharedPreferences sharedPreferences1 = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getSharedPreferences("userimage", MODE_PRIVATE);
        userid = sharedPreferences1.getString("uid", "");
        String secondId = sharedPreferences2.getString("proid", "");
        String username = sharedPreferences1.getString("username", "");


        if (username != "") {
            textUser.setText(username);
        }
        String imagepath = sharedPreferences2.getString("procampath", "");
        if (imagepath != "" && secondId.equalsIgnoreCase(userid)) {
            Picasso.get().load(new File(imagepath))
                    .resize(95, 95)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            imageView.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "exception camera : " + e);
                            imageView.setImageResource(R.drawable.camera_icon);
                            Toast.makeText(MainActivity.this, "Something Went Wrong While storing image", Toast.LENGTH_SHORT).show();
                        }
                    });

        }


        Intent in = getIntent();
        String text = in.getStringExtra("bookingSucceed");
        if (text != null) {
            Snackbar bar = Snackbar.make(rootLayout, "" + text, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle user action
                        }
                    });

            bar.show();
        }


        String text1 = in.getStringExtra("OncallBooked");
        if (text1 != null) {
            Snackbar bar = Snackbar.make(rootLayout, "" + text1, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle user action
                        }
                    });

            bar.show();
        }
        gotoCheckInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    showSnackbar("PLease Check Your Netwok Connection");
                    return;
                }

                getData();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


    }

    private void getData() {
        if (!isNetworkAvailable()) {
            showSnackbar("Please Check your Network Connection");
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOG_DETTIAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "Response " + response);

                progressDialog.dismiss();
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        responseJson = jsonObject.getString("response");
                        logtime = jsonObject.getString("log_time");
                        logdatee = jsonObject.getString("log_date");
                        logstatus = jsonObject.getString("log_status");
                    }
                    Pattern p = Pattern.compile(".*([01]?[0-9]|2[0-3]):[0-5][0-9].*");
                    Matcher matcher = p.matcher(logtime);
                    if (responseJson.equalsIgnoreCase("Record Empty")) {
                        showSnackbar("Please Check Your Connection");
                        SharedPreferences sharedPreferences1 = getSharedPreferences("s", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences1.edit();

                        editor.putString("logstatus", logstatus);
                        if (matcher.matches()) {
                            editor.putString("logtime", logtime);
                        }
                        Log.e(TAG, "empty prefeerence :");
                        editor.commit();
                        startActivity(new Intent(MainActivity.this, Alternate_checkInOut.class));
                        finish();
                    } else if (responseJson.equalsIgnoreCase("Success")) {

                        SharedPreferences sharedPreferences1 = getSharedPreferences("s", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                        editor.putString("logstatus", logstatus);
                        if (matcher.matches()) {
                            editor.putString("logtime", logtime);
                        }
                        editor.putString("logdate", logdatee);
                        Log.e(TAG, "Success Preference : " + logstatus + "-" + logtime + "-" + logdatee);
                        editor.commit();
                        startActivity(new Intent(MainActivity.this, Alternate_checkInOut.class));
                        finish();
                    }
                } catch (JSONException ex) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Exeception " + ex);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Log.e(TAG, "error response : " + error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map map = new HashMap();
                map.put("id", userid);
                return map;
            }
        };
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    public void selectImage() {

        final CharSequence items[] = {"camera", "gallery", "cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog_one);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (items[i].equals("camera")) {

                    requestPermissionForCamera(CAMERA_REQUEST);
                    dialogInterface.dismiss();
                } else if (items[i].equals("gallery")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST);
                } else if (items[i].equals("cancel")) {

                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void requestPermissionForCamera(int request) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                showSnackbar("This App needs Camera");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        request);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dispatchTakePictureIntent(request);
            } else {
                cameraForOld(request);
            }
        }
    }

    private void dispatchTakePictureIntent(int REQUEST_TAKE_PHOTO) {
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

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {

            Picasso.get().load(new File(mCurrentPhotoPath))
                    .resize(95, 95)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            imageView.setImageDrawable(imageDrawable);
                            SharedPreferences sharedPreferences = getSharedPreferences("userimage", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("procampath", mCurrentPhotoPath);
                            editor.putString("proid", userid);
                            editor.commit();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "exception camera : " + e);
                            imageView.setImageResource(R.drawable.camera_icon);
                            Toast.makeText(MainActivity.this, "Something Went Wrong While storing image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (requestCode == GALLERY_REQUEST) {

            final Uri selectedImageUri = data.getData();
            picturepath = getPath(MainActivity.this, selectedImageUri);
            Picasso.get().load(new File(picturepath))
                    .resize(95, 95)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            imageView.setImageDrawable(imageDrawable);
                            SharedPreferences sharedPreferences = getSharedPreferences("userimage", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("procampath", String.valueOf(picturepath));
                            editor.putString("proid", userid);
                            editor.commit();
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "exception camera : " + e);
                            imageView.setImageResource(R.drawable.camera_icon);
                            Toast.makeText(MainActivity.this, "Something Went Wrong While storing image", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(rootLayout, message,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.text_ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor1.clear();
        editor.commit();
        editor1.commit();
        SharedPreferences sharedPreferences2 = getSharedPreferences("time", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.clear();
        editor2.commit();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout_id:
                logout();
                break;

            default:
                break;
        }

        return true;
    }


    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    @Override
    public void onBackPressed() {
        finish();

    }


}

