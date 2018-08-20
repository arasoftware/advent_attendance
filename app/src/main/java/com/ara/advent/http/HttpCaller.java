package com.ara.advent.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ara.advent.R;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by sathishbabur on 1/31/2018.
 */

public class HttpCaller extends AsyncTask<HttpRequest, String, HttpResponse> {


    private static final String UTF_8 = "UTF-8";
    private Context context;
    private ProgressDialog progressDialog;
    private String progressMessage;
    private boolean socketTimedOut = false;
    public HttpCaller(Context context, String progressMessage) {
        super();
        this.context = context;
        this.progressMessage = progressMessage;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(progressMessage);
        progressDialog.show();
    }

    @Override
    protected HttpResponse doInBackground(HttpRequest... httpRequests) {
        HttpRequest httpRequest = httpRequests[0];
        HttpResponse httpResponse = new HttpResponse();
        OkHttpClient client = new OkHttpClient();
        try {

            Request request = new Request.Builder()
                    .url(httpRequest.getUrl())
                    .post(httpRequest.getRequestBody())
                    .build();
            Response response = client.newCall(request).execute();

            String message = response.body().string();
            if (response.isSuccessful()) {
                httpResponse.setSuccessMessage(message);
            } else {
                httpResponse.setErrorMessage(message);
            }
            response.body().close();
            return httpResponse;

        } catch (SocketTimeoutException connTimeout) {
            this.socketTimedOut = true;
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e("Http URL", e.toString());
            httpResponse.setErrorMessage(e.getMessage());
        }
        return httpResponse;
    }



    @Override
    protected void onPostExecute(HttpResponse response) {
        if(this.socketTimedOut){
            showTimeoutAlert();
        }
        super.onPostExecute(response);
        progressDialog.dismiss();
        onResponse(response);
        context = null;

    }

    private void showTimeoutAlert() {
        Toast.makeText(context, "Server time Out Please Try Again", Toast.LENGTH_SHORT).show();
    }

    public void onResponse(HttpResponse response) {
    }
}