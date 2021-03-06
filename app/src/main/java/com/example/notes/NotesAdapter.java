package com.example.notes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    private Context context;
    // Filtered list which is shown to user
    private ArrayList<NoteItem> notesList;
    // entire list
    private OnItemCLickListener notesListener;



    /**
     * Constructor for adapter.
     */
    public NotesAdapter(Context context, ArrayList<NoteItem> notesList) {
        this.context = context;
        this.notesList = notesList;
    }


    public interface OnItemCLickListener {
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
        public ImageView itemIcon;
        public TextView heading;
        public TextView body;
        public TextView timeStampView;
        public CardView noteDetails;


        public NotesViewHolder(@NonNull final View itemView, final OnItemCLickListener listener) {
            super(itemView);

            cardContainer = itemView.findViewById(R.id.cardContainer);
            cardView = itemView.findViewById(R.id.cardView);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            heading = itemView.findViewById(R.id.headingView);
            body = itemView.findViewById(R.id.bodyView);
            timeStampView = itemView.findViewById(R.id.timeStampView);
            noteDetails = itemView.findViewById(R.id.noteDetailsBg);

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
    // State for delete mode
    private static boolean delMode = false;

    public boolean isDelMode() {
        return delMode;
    }

    public void setDelMode(boolean delMode) {
        NotesAdapter.delMode = delMode;
    }





    // Binds data to item
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final NotesViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final NoteItem currentItem = notesList.get(position);
        holder.cardView.setCardBackgroundColor(currentItem.getColor());
        holder.itemIcon.setImageResource(currentItem.getIconFromString(currentItem.getIcon()));
        holder.heading.setText(currentItem.getHeading());
        holder.body.setText(currentItem.getBody());
        holder.timeStampView.setText(getHumanReadableDate(currentItem.getTimestamp()));


        int duration = 130;
        // CHECK IF IN DEL MODE SO NOT TWO ON CLICKS AT ONCE
        if (isDelMode()){
            holder.cardView.setClickable(true);
            Log.i("DelMode", String.valueOf(isDelMode()));
            holder.cardView.setOnClickListener(v -> {
                // CHECK IF SELECTED
                currentItem.setSelected(!currentItem.getSelected());
                if(currentItem.getSelected()){
                    holder.cardView.animate().alpha(0.5f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            Log.i("alpha", "0.5");
                            holder.cardView.setAlpha(0.5f);
                        }
                    });
                }
                else if (!currentItem.getSelected()){
                    holder.cardView.animate().alpha(1.0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            Log.i("alpha", "1.0");
                            holder.cardView.setAlpha(1.0f);
                        }
                    });
                }
            });
        }

        //  On exiting delete mode recolor these
        if (!isDelMode()){
            holder.cardView.setClickable(false);
            currentItem.setSelected(false);
            holder.cardView.animate().alpha(1.0f).setDuration(duration).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    holder.cardView.setAlpha(1.0f);
                }
            });
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getHumanReadableDate(String timestamp){
        Instant time = Timestamp.valueOf(timestamp).toInstant();
//        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm");
        DateTimeFormatter myDateFormatter =  DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm")
                .withLocale( Locale.UK )
                .withZone( ZoneId.systemDefault() );
        String output = myDateFormatter.format(time);
        return output;
    }
}