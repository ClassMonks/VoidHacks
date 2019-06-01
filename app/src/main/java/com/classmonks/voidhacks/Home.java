package com.classmonks.voidhacks;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    FirebaseFirestore db;
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    Button notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        notes = findViewById(R.id.notes);
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AllNotes.class);
                startActivity(i);
            }
        });

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_PHONE_STATE
                },
                    SEND_SMS_PERMISSION_REQUEST_CODE);

        createNotificationChannel();

        Bundle extras = getIntent().getExtras();

        try {
            if (extras != null) {
                String device = extras.getString("Device");
                if (device.equals("Mobile")) {
                    Map<String, String> data = new HashMap<>();
                    data.put("device", "Mobile");
                    data.put("reply", "");
                    data.put("this_message", "");
                    db.collection("messages").document("message")
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Device updated successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        } catch (Exception e) {
            Log.d("VoidHacks", e.getMessage());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification";
            String description = "Notification for VoidHacks";
            String CHANNEL_ID = "notification_ch_id";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
