package com.example.notes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    private Context context;
    // Filtered list which is shown to user
    private ArrayList<NoteItem> notesList;
    private ArrayList<NoteItem> notesToBeDeleted;
    private ArrayList<Integer> positionsToBeReinstated;
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

        notesToBeDeleted = new ArrayList<>();
        positionsToBeReinstated = new ArrayList<>();
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
        public LinearLayout cardContainer;
        public CardView cardView;
        public ImageView choosingDelButton;
        public TextView heading;
        public TextView body;

        public CardView noteDetails;


        public NotesViewHolder(@NonNull final View itemView, final OnItemCLickListener listener) {
            super(itemView);

            cardContainer = itemView.findViewById(R.id.cardContainer);
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

    // State for delete mode
    private static boolean delMode = false;

    public boolean isDelMode() {
        return delMode;
    }

    public void setDelMode(boolean delMode) {
        NotesAdapter.delMode = delMode;
    }







    // Binds data to item
    @Override
    public void onBindViewHolder(@NonNull final NotesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final NoteItem currentItem = notesList.get(position);
        holder.cardView.setCardBackgroundColor(currentItem.getColor());
        holder.heading.setText(currentItem.getHeading());
        holder.body.setText(currentItem.getBody());

        // CHECK IF SHOULD DISPLAY X BUTTON
        if (!isDelVisible()) {
            holder.choosingDelButton.animate().alpha(0f).setDuration(8).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    holder.choosingDelButton.setVisibility(View.INVISIBLE);
                }
            });
        }
        else{
            holder.choosingDelButton.animate().alpha(1.0f).setDuration(8).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    holder.choosingDelButton.setVisibility(View.VISIBLE);
                }
            });
        }

        holder.cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CHECK IF SELECTED
                if (isDelMode()){
                    currentItem.setSelected(!currentItem.getSelected());
                    if(currentItem.getSelected()){
                        holder.cardContainer.setAlpha(0.5f);
                        Log.i("Opacity", "0.5");
                    }
                    else if (!currentItem.getSelected()){
                        holder.cardContainer.setAlpha(1f);
                        Log.i("Opacity", "1.0");
                    }

                }
            }
        });

        //  On exiting delete mode recolor these
        if (!isDelMode()){
            currentItem.setSelected(false);
            holder.cardContainer.setAlpha(1f);
        }

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void filterList(ArrayList<NoteItem> filteredList) {
         notesList = filteredList;
         notifyDataSetChanged();
    }

//    public void removeForPreview(int position) {
//         // For canceling the preview
//         notesToBeDeleted.add(notesList.get(position));
//         positionsToBeReinstated.add(position);
//
//
//         notesList.remove(position);
//         notifyItemRemoved(position);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void cancelPreview() {
//         for (NoteItem noteToBeDel : notesToBeDeleted){
//             notesList.add(noteToBeDel);
//         }
//         Collections.sort(notesList, Collections.<NoteItem>reverseOrder());
//         notifyDataSetChanged();
//         notesToBeDeleted.clear();
//    }

}