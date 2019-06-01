package com.classmonks.voidhacks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> notes;
    private Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        View noteView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            noteView = itemView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    MyAdapter(ArrayList<String> notesList, Context context) {
        notes = notesList;
        mContext = context;
    }

    @NonNull
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout noteView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_view, parent, false);
        return new MyViewHolder(noteView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final TextView noteTextView = holder.noteView.findViewById(R.id.note_text_view);
        noteTextView.setText(notes.get(position));

        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, NewNote.class);
                i.putExtra("Note", noteTextView.getText().toString());
                mContext.startActivity(i);
                ((Activity)mContext).finish();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return notes.size();
    }
}
