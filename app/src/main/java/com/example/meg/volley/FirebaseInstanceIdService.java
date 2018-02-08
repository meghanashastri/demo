package com.example.meg.volley;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by meg on 14/11/17.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("tag", "Refreshed token: " + refreshedToken);


    }
}
