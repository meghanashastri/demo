package com.example.meg.volley;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.util.TimeUnit;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    boolean identifier = true;
    String message;
    ProgressBar progressBar;
    private boolean notificationStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        makeApiBodyAndCall();

    }

    private void makeApiBodyAndCall() {
        progressBar.setVisibility(View.VISIBLE);
        final JSONObject body = new JSONObject();
        try {
            body.put("routing_key", "message");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Util.isConnectingToInternet(MainActivity.this)) {
            RequestWithWait requestWithWait = new RequestWithWait(MainActivity.this, new Listener() {
                @Override
                public void onMessageReceived(String object) {

                }

                @Override
                public void timeout() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("MainActivity", "timed_out");
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }

                @Override
                public void volleySuccess() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("MainActivity", "request_successfuly_sent");
                        }
                    });
                }

                @Override
                public void volleyError(VolleyError error) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("MainActivity", "request_failed");
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                }
            }, body);

            if (requestWithWait.run() != 0) {
                Log.d("invalid", "Invalid Routing Key");
            }

        } else {
            Toast.makeText(this, "check_internet_connection", Toast.LENGTH_SHORT).show();
        }
    }


}
