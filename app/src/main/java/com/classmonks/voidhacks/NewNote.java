package com.classmonks.voidhacks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewNote extends AppCompatActivity {
    FirebaseFirestore db;
    Button save;
    EditText note;
    String TAG = "VoidHacks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        db = FirebaseFirestore.getInstance();
        save = findViewById(R.id.saveclipboard);
        note = findViewById(R.id.note);

        final String editNote = getIntent().getStringExtra("Note");

        final boolean edit = !editNote.equals("null");

        if (edit) {
            note.setText(editNote);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit) {
                    db.collection("notes")
                            .whereEqualTo("this_note", editNote)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                                    db.collection("notes")
                                            .document(id)
                                            .update("this_note", note.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Note Updated", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                }

                else {
                    // Create a new user with a first and last name
                    Map<String, Object> noteText = new HashMap<>();
                    noteText.put("this_note", note.getText().toString());

                    // Add a new document with a generated ID
                    db.collection("notes")
                            .add(noteText)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    Toast.makeText(getApplicationContext(), "Note Added", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), AllNotes.class);
        startActivity(i);
        finish();
    }
}
