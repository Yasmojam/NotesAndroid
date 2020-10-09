package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements Filterable {

    // Filtered list which is shown to user
    private ArrayList<NoteItem> notesList;
    // entire list
    private ArrayList<NoteItem> noteListFull;
    private OnItemCLickListener notesListener;
    private boolean delVisible = false;

    public interface OnItemCLickListener{
        void onDeleteClick(int position);
        void onClickNote(int position);
    }

    public void setDelVisible(boolean bool){
        this.delVisible = bool;
    }

    public boolean isDelVisible(){
        return this.delVisible;
    }

    public void setOnItemClickListener(OnItemCLickListener listener) {
        notesListener = listener;
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ImageView getChoosingDelButton() {
            return choosingDelButton;
        }

        public ImageView choosingDelButton;
        public ImageView imageView;
        public TextView heading;
        public TextView body;

        public CardView noteDetails;


        public NotesViewHolder(@NonNull final View itemView, final OnItemCLickListener listener) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            choosingDelButton = itemView.findViewById(R.id.chooseDelButton);
//            imageView = itemView.findViewById(R.id.imageView);
            heading = itemView.findViewById(R.id.headingView);
            body = itemView.findViewById(R.id.bodyView);
            noteDetails = itemView.findViewById(R.id.noteDetailsBg);
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

            // item click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            // what you put in function oin main activity will happen
                            listener.onClickNote(position);

                        }
                    }
                }
            });

//            if (){
//                choosingDelButton.setVisibility(View.VISIBLE);
//            }
//            else if (!visibleDel){
//                choosingDelButton.setVisibility(View.INVISIBLE);
//            }


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


    @Override
    public Filter getFilter() {
        return notesFilter;
    }

    private Filter notesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
                List<NoteItem> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() ==0) {
                    filteredList.addAll(noteListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (NoteItem note : noteListFull){
                        if (note.getHeading().toLowerCase().contains(filterPattern) || note.getBody().toLowerCase().contains(filterPattern)) {
                            filteredList.add(note);
                        }
                    }
            }
                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
        }

        // Sends filtered data to UI thread.
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesList.clear();
            notesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}