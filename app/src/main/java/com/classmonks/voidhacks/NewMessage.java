package com.classmonks.voidhacks;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class NewMessage extends AppCompatActivity {
    private static final String TAG = "VoidHacks";
    FirebaseFirestore db;
    TextView senderTextView, messageTextView;
    LinearLayout messagesContainer;
    Button sendButton;
    EditText messageEditText;
    private ListenerRegistration listener;
    Bundle extras;
    String sender;
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        db = FirebaseFirestore.getInstance();

        extras = getIntent().getExtras();
        sender = extras.getString("Sender");
        message = extras.getString("Message");

        messagesContainer = findViewById(R.id.messages_container);

        senderTextView = findViewById(R.id.sender);
        senderTextView.setText(sender);

        messageTextView = findViewById(R.id.message);
        messageTextView.setText(message);

        final Map<String, String> thisMessage = new HashMap<>();
        thisMessage.put("this_message", messageTextView.getText().toString());
        thisMessage.put("device", "");
        thisMessage.put("reply", "");

        db.collection("messages").document("message")
                .set(thisMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Message added to firestore", Toast.LENGTH_SHORT).show();
                    }
                });

        listener = db.collection("messages").document("message")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Listening Failed");
                            Toast.makeText(getApplicationContext(), "Listening Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            if (snapshot.getString("reply") != null &&
                            !snapshot.getString("reply").isEmpty()) {
                                TextView reply = new TextView(getApplicationContext());
                                reply.setText(snapshot.getString("reply"));
                                reply.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                                reply.setPadding(50, 50, 50, 50);
                                reply.setBackgroundColor(getResources().getColor(R.color.reply));
                                reply.setGravity(Gravity.CENTER);
                                reply.setTextColor(getResources().getColor(R.color.black));
                                reply.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                messagesContainer.addView(reply);

                                TextView sending = new TextView(getApplicationContext());
                                sending.setText("Sending...");
                                sending.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                                messagesContainer.addView(sending);
                                Log.d(TAG, "Current data: " + snapshot.getData());

                                Intent pIntent = new Intent(getApplicationContext(), Home.class);
                                pIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                pIntent.putExtra("Device", "PC");
                                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, pIntent, PendingIntent.FLAG_ONE_SHOT);

                                String message = snapshot.getString("reply");
                                String phoneNo = sender;
                                if(!TextUtils.isEmpty(message) && !TextUtils.isEmpty(phoneNo)) {
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(phoneNo, null, message,
                                                pi, null);
                                }
                            }

                            Log.d(TAG, "Snapshot not updated");

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

        sendButton = findViewById(R.id.send);
        messageEditText = findViewById(R.id.enter_message);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pIntent = new Intent(getApplicationContext(), Home.class);
                pIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pIntent.putExtra("Device", "Mobile");
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, pIntent, PendingIntent.FLAG_ONE_SHOT);

                String message = messageEditText.getText().toString();
                String phoneNo = sender;
                if(!TextUtils.isEmpty(message) && !TextUtils.isEmpty(phoneNo)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message,
                            pi, null);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        listener.remove();
    }
}
