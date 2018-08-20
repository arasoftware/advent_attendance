package com.ara.advent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ara.advent.http.HttpResponse;
import com.ara.advent.http.MySingleton;
import com.ara.advent.models.User;
import com.ara.advent.utils.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ara.advent.utils.AppConstants.ATTENDANCEOBJ;
import static com.ara.advent.utils.AppConstants.PARAM_PASSWORD;
import static com.ara.advent.utils.AppConstants.PARAM_USER_NAME;
import static com.ara.advent.utils.AppConstants.PREFERENCE_NAME;
import static com.ara.advent.utils.AppConstants.PREF_TYPE;
import static com.ara.advent.utils.AppConstants.SUCCESS_MESSAGE;
import static com.ara.advent.utils.AppConstants.USER_TYPE;

public class LoginActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "LoginActivity";
    @BindView(R.id.input_login_userName)
    EditText _login_userName;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.scroll_view_login)
    ScrollView _rootScrollView;
    int type = USER_TYPE;
    ArrayList<User> userArrayList = new ArrayList<User>();
    String login, daycheckinuser, daycheckoutuser, resFshift, nightcheckinuser, nightcheckoutuser, resFdate, resSshift, resSdate, username, userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        type = USER_TYPE;

        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String userSession = sharedPreferences.getString("uid", "");
        if (userSession != "") {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    public boolean validate() {
        boolean valid = true;

        String mobile = _login_userName.getText().toString();
        String password = _passwordText.getText().toString();

        if (mobile.isEmpty()) {
            _login_userName.setError("Enter the Valid Name");
            valid = false;
        } else {
            _login_userName.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Enter valid password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    public void login_onClick(View view) {

        loginVolley();


    }

    private void loginVolley() {
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        if (!isNetworkAvailable()) {
            progressDialog.dismiss();
            showSnackbar("Please check Your network connection");
            return;
        }
        if (!validate()) {
            progressDialog.dismiss();
            return;
        }
        final User user = new User();
        user.setUserName(_login_userName.getText().toString());
        user.setPassword(_passwordText.getText().toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.getLoginAction(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e(TAG, "Response Login = " + response);
                JSONArray jsonArray = null;
                JSONObject jsonObject = null;

                try {
                    jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        login = jsonObject.getString("login");

                    }

                    if (!login.equalsIgnoreCase(SUCCESS_MESSAGE)) {
                        progressDialog.dismiss();
                        showSnackbar("PLease Check Your Username and Password ");
                        Toast.makeText(LoginActivity.this, "You are enter a Wrong Password", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        username = jsonObject.getString("username");
                        userid = jsonObject.getString("userid");
                        SharedPreferences sharedPreferences1 = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences1.edit();
                        edit.putString("username",username);
                        edit.putString("uid", userid);
                        edit.commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                } catch (JSONException ex) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Json lgin exception = " + ex);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Login Error Response : " + error);

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map map = new HashMap();
                map.put(PARAM_USER_NAME, _login_userName.getText().toString());
                map.put(PARAM_PASSWORD, _passwordText.getText().toString());
                map.put("type", "1");
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

    private void showSnackbar(String message) {
        final Snackbar snackbar = Snackbar.make(_rootScrollView, message,
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
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
        return;
    }
}
