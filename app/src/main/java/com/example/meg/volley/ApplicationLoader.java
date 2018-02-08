package com.example.meg.volley;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by meg on 06/02/18.
 */

public class ApplicationLoader extends Application {
    public static final String TAG = ApplicationLoader.class.getSimpleName();
    private static ApplicationLoader mInstance;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized ApplicationLoader getInstance() {
        return mInstance;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ApplicationLoader.getInstance());
        }

        return mRequestQueue;
    }
}
