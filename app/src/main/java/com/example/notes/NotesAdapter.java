package com.example.notes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements Filterable {

    // Filtered list which is shown to user
    private ArrayList<NoteItem> notesList;
    // entire list
    private ArrayList<NoteItem> noteListFull;
    private OnItemCLickListener notesListener;

    @Override
    public Filter getFilter() {
        return null;
    }

    public interface OnItemCLickListener{
        void onItemCLick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemCLickListener listener) {
        notesListener = listener;
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView choosingDelButton;
        public ImageView imageView;
        public TextView heading;
        public TextView body;

        public NotesViewHolder(@NonNull View itemView, final OnItemCLickListener listener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            choosingDelButton = itemView.findViewById(R.id.chooseDelButton);
//            imageView = itemView.findViewById(R.id.imageView);
            heading = itemView.findViewById(R.id.headingView);
            body = itemView.findViewById(R.id.bodyView);

            // All items click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemCLick(position);
                        }
                    }
                }
            });

            // x button click listener
            choosingDelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }


        public void toggleDelButton(){
            choosingDelButton.setVisibility(View.VISIBLE);
        }

    }

    public NotesAdapter(ArrayList<NoteItem> notesList) {
        this.notesList = notesList;
        noteListFull = new ArrayList<>(notesList);
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        NotesViewHolder nvh = new NotesViewHolder(v, notesListener);
        return nvh;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        NoteItem currentItem = notesList.get(position);

        holder.cardView.setCardBackgroundColor(Color.parseColor(currentItem.getColor()));
//        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.heading.setText(currentItem.getHeading());
        holder.body.setText(currentItem.getBody());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }


}