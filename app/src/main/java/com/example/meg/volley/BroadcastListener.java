package com.example.meg.volley;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.CountDownLatch;

/**
 * Created by meg on 06/02/18.
 */

public class BroadcastListener{

    private CountDownLatch mDone;
    private Context mContext;
    private Listener mDiplomaticoListener;
    private String mAction;

    private BroadcastReceiver mListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //PushNotification SUCCESS
            String message = intent.getStringExtra("message");
            mDiplomaticoListener.onMessageReceived(message);

            unregister();
            mDone.countDown();
        }
    };

    public BroadcastListener(CountDownLatch done, Context context, String action, Listener listener) {
        mDone = done;
        mContext = context;
        mAction = action;
        mDiplomaticoListener = listener;
    }


    public void register() {
        //register broadcast receiver
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mListener,
                new IntentFilter(mAction));
    }


    public void unregister() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mListener);
    }
}
