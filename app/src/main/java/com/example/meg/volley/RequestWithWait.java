package com.example.meg.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.android.volley.VolleyError;


/**
 * Created by meg on 06/02/18.
 */

public class RequestWithWait {

    private com.android.volley.Response.Listener<String> mLocalVolleyListener;
    private com.android.volley.Response.ErrorListener mLocalVolleyErrorListener;

    //Here Latch count is number of retries for push-notification
    private CountDownLatch mDone = new CountDownLatch(1);
    private Context mContext;
    private BroadcastListener mBroadcastListener;
    private boolean mValid = true;
    private int INVALID_REQUEST = 1;
    private String mRoutingKey, mListenerRoutingKey;
    private Listener mListener;

    public RequestWithWait(final Context context, final Listener listener, JSONObject body) {

        mContext = context;
        mListener = listener;
        try {

            mRoutingKey = (String) body.get("routing_key");
            mListenerRoutingKey = mRoutingKey;
        } catch (JSONException e) {

            mValid = false;
        }
        mBroadcastListener = new BroadcastListener(mDone, context, mListenerRoutingKey, mListener);
        mBroadcastListener.register();

        mLocalVolleyListener = new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //SUCCESS: wait for Push Notification

                mListener.volleySuccess();

                Thread waitThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            //PushNotification FAILURE/DELAY
                            if (!mDone.await(Constants.TIMEOUT, TimeUnit.SECONDS)) {
                                mBroadcastListener.unregister();
                                mListener.timeout();
                            }
                        } catch (InterruptedException e) {
                            mBroadcastListener.unregister();
                            mListener.timeout();
                        }
                    }
                };
                waitThread.start();

            }
        };
        mLocalVolleyErrorListener = new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //ERROR: Return error message
                //TODO

                String body;

                if (error.networkResponse == null) {
                    mListener.volleyError(error);
                    return;
                }
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                Log.d("volley error", error.toString());
                Log.d("status code", statusCode);

                mListener.volleyError(error);
                //get response body and parse with appropriate encoding
                if (error.networkResponse.data != null) {
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                        Log.d("error body", body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                error.printStackTrace();
            }
        };
    }


    //method to make a PUT request to the server
    public int run() {
        if (!mValid) {
            return INVALID_REQUEST;
        }

        Thread requestThread = new Thread() {

            @Override
            public void run() {
                final StringRequest apiStringRequest = new StringRequest(Request.Method.GET,
                        mContext.getString(R.string.endpoint_url), mLocalVolleyListener, mLocalVolleyErrorListener) {

                    @Override
                    protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                        int mStatusCode = response.statusCode;
                        Log.d("status code", String.valueOf(mStatusCode));
                        return super.parseNetworkResponse(response);
                    }

                };

                apiStringRequest.setShouldCache(false);
                ApplicationLoader.getRequestQueue().add(apiStringRequest);
            }
        };
        requestThread.start();
        return 0;
    }
}
