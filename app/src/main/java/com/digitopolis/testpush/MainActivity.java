package com.digitopolis.testpush;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    String url = "http://www.devpassbookbydigitopolis.com/PushNotificationService/add";
    String responseTest;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(isNetworkConnected()){
            textView = (TextView) findViewById(R.id.textview1);
            new GcmRegistration().execute(this);
        }
        else{
            Toast.makeText(getApplicationContext(), "Internet not connect", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    class GcmRegistration extends AsyncTask<Context, Void, String> {

        private GoogleCloudMessaging mGcm;
        private Context mContext;


        private static final String SENDER_ID = "392089405024";

        @Override
        protected String doInBackground(Context... params) {

            ////////// Get register ID ///////////
            mContext = params[0];

            String regId = "";
            try {
                if (mGcm == null) {
                    mGcm = GoogleCloudMessaging.getInstance(mContext);
                }
                regId = mGcm.register(SENDER_ID);

            } catch (IOException ex) {
                ex.printStackTrace();
                String msg = "Error: " + ex.getMessage();
                Log.e("Register ID error",msg);
            }

            //////////// Connect /////////////
            try {
                URL object = new URL(url);
                HttpURLConnection con = (HttpURLConnection) object.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestMethod("POST");
                con.setUseCaches(false);

                //Header
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                con.setRequestProperty("Authorization", "Basic REdUVGVzdGVyOmVlYzY5MTMwNDYyN2E5ZDk4YzExZGM3OTQ0MWFkZDlk");

                //Sent request (post body)
                String str = "push_token="+regId+"&device_model=TAB&device_os_version=1&app_version=1.0&app_identifier=com.digitopolis.pushnotification&app_os=Android";
                byte[] strInByte = str.getBytes("UTF-8");
                OutputStream os = con.getOutputStream();
                os.write(strInByte);
                os.flush();
                os.close();

                con.connect();
                Log.e("DEBUG XXX", con.getResponseMessage().toString());
                Log.e("DEBUG XXX", con.getRequestMethod());
                Log.e("DEBUG XXX", con.getURL().toString());
                //Get response
                InputStream is = con.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                responseTest = response.toString();

                Log.e("Response", response.toString());

            }catch (ConnectException e) {
                Log.e("Connect con error", e.getMessage().toString());
            }catch (IOException e) {
                Log.e("Connect IO error", e.getMessage().toString());
            }catch (Exception e) {
                Log.e("Connect default error", e.getMessage().toString());
            }
            return regId;
        }

        @Override
        protected void onPostExecute(String regId) {
            Toast.makeText(mContext, regId, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, regId);
            Log.e("Register ID", regId);
            textView.setText("Response : " + responseTest);
            Log.e("Response", responseTest);
        }
    }
}
