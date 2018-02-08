package com.example.meg.volley;

/**
 * Created by meg on 06/02/18.
 */

import com.android.volley.VolleyError;

public interface Listener {

    void onMessageReceived(String object);

    void timeout();

    void volleySuccess();

    void volleyError(VolleyError error);
}
