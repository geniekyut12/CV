package com.example.firstpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "Notification triggered!");

        // Send a broadcast to ProfileFragment
        Intent updateIntent = new Intent("com.example.firstpage.NOTIFICATION_RECEIVED");
        context.sendBroadcast(updateIntent);
    }
}
