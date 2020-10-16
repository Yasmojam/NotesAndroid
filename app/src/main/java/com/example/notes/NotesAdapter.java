package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;
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
    private Context context;
    // Filtered list which is shown to user
    private ArrayList<NoteItem> notesList;
    // entire list
    private ArrayList<NoteItem> noteListFull;
    private NotesDBHelper dbHelper;
    private OnItemCLickListener notesListener;



    /**
     * Constructor for adapter.
     */
    public NotesAdapter(Context context, ArrayList<NoteItem> notesList) {
        this.context = context;
        this.notesList = notesList;
        noteListFull = new ArrayList<>(notesList);
        dbHelper = new NotesDBHelper(context);
    }


    public interface OnItemCLickListener {
        void onDeleteClick(int position);

        void onClickNote(int position);
    }

    public void setOnItemClickListener(OnItemCLickListener listener) {
        notesListener = listener;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView choosingDelButton;
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
                public void onClick(View view) {
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


        }
    }



    // Adds visual to item
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        NotesViewHolder nvh = new NotesViewHolder(v, notesListener);
        return nvh;
    }

    // State for delete button
    private static boolean delVisible = false;
    // getter
     public boolean isDelVisible() {
         return delVisible;
     }
     // setter
     public void setDelVisible(boolean delVisible) {
         this.delVisible = delVisible;
     }




    // Binds data to item
    @Override
    public void onBindViewHolder(@NonNull final NotesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        NoteItem currentItem = notesList.get(position);
        holder.cardView.setCardBackgroundColor(Color.parseColor(currentItem.getColor()));
//        holder.imageView.setImageResource(currentItem.getImageResource());
        holder.heading.setText(currentItem.getHeading());
        holder.body.setText(currentItem.getBody());

        // CHECK IF SHOULD DISPLAY X BUTTON
        if (!isDelVisible()) {
            holder.choosingDelButton.setVisibility(View.INVISIBLE);
        }
        else{
            holder.choosingDelButton.setVisibility(View.VISIBLE);
        }

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