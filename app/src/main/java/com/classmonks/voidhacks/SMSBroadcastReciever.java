package com.classmonks.voidhacks;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSBroadcastReciever extends BroadcastReceiver {

    private static final String TAG = "VoidHacks";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle intentExtras = intent.getExtras();
        Log.d(TAG, "Getting Extras Bundle");

        if (intentExtras != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i = 0; i < pdus.length; i++) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    }else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    String senderPhoneNo = messages[i].getDisplayOriginatingAddress();
                    String message = messages[i].getMessageBody();

                    Intent pIntent = new Intent(context, NewMessage.class);
                    pIntent.putExtra("Sender", senderPhoneNo);
                    pIntent.putExtra("Message", message);

                    Log.d(TAG, "Got Extras");

                    PendingIntent pi = PendingIntent.getActivity(context,
                            0,
                            pIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification_ch_id")
                            .setContentTitle(senderPhoneNo)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(pi)
                            .setAutoCancel(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
                    notificationManager.notify(0, builder.build());
                }
            }

        }

    }
}
