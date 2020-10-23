package com.example.notes;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private  NotesDBHelper dbHelper;
    private RecyclerView recyclerView;
    // Used to load individual data sets into recycler rather than loading all at once and giving poor performance.
    private NotesAdapter recyclerAdapter;
    private GridLayoutManager recyclerLayoutManager;
    ArrayList<NoteItem> notesList;

    ArrayList<NoteItem> notesToBeDeleted;

    private View dimmer;
    private LinearLayout searchContainer;
    private EditText searchBar;
    private LinearLayout deletePrompt;
    private ImageButton deleteButton;
    private ImageButton addButton;
    private ImageButton confirmDelete;
    private ImageButton cancelDelete;
    private ImageButton searchButton;
    private ImageView clearButton;

    /**
     *  Possible states:
     *  none
     *  deleting
     *  adding
     * */
    private String appState = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dimmer = findViewById(R.id.dim);

        notesToBeDeleted = new ArrayList<>();

       dbHelper = new NotesDBHelper(this);
        database = dbHelper.getWritableDatabase();
        notesList = dbHelper.getAllNotes();
        sortNotesDescDate();

        buildRecyclerView();
        setButtonsEditText();

    }

    /**
     * Sorts notes by descending date.
     */
    public void sortNotesDescDate(){
        Collections.sort(notesList, Collections.<NoteItem>reverseOrder());
    }

    public void setButtonsEditText() {
        deleteButton = findViewById(R.id.delButton);
        addButton = findViewById(R.id.addButton);
        confirmDelete = findViewById(R.id.confirm_delete);
        cancelDelete = findViewById(R.id.cancel_delete);
        searchContainer = findViewById(R.id.searchContainer);
        searchBar = findViewById(R.id.editSearch);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);
        deletePrompt = findViewById(R.id.delPrompContainer);

        checkDeleteButton();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instateDeleteMode();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteDetails.class);
                intent.putExtra("State", "creating");
                // request code 2 for new note
                startActivityForResult(intent, 2);
                dimmer.setVisibility(View.VISIBLE);
            }
        });

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NoteItem> toBeDel = new ArrayList<>();
                for (NoteItem noteItem : notesList){
                    if (noteItem.getSelected()) {
                        toBeDel.add(noteItem);
                    }
                }


                for (NoteItem toBeDelNote : toBeDel){
                    removeItem(toBeDelNote);
                }
                notesToBeDeleted.clear();
                    removeDeleteMode();
            }
        });

        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                notesToBeDeleted.clear();
//                recyclerAdapter.cancelPreview();
                removeDeleteMode();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchContainer.isShown()){
                    searchContainer.setVisibility(View.GONE);
                    recyclerAdapter.notifyDataSetChanged();
                    hideKeyboard(v);
                }
                else {
                    searchContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        searchBar.addTextChangedListener(textWatcher);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
                // Reset the filtered list back to original list
                recyclerAdapter.filterList(notesList);
            }
        });
    }


    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // keep size of the view the same size no matter number of items
        recyclerLayoutManager = new GridLayoutManager(this, 2);
        recyclerAdapter = new NotesAdapter(this, notesList);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerAdapter.setOnItemClickListener(new NotesAdapter.OnItemCLickListener() {
            @Override
            public void onClickNote(int position) {
                // Open details if not deleting
                if (!recyclerAdapter.isDelMode()) {
                    // try to change bg of detail card
                    Log.i("clicked", "Note No. " + position + " clicked");
                    Intent intent = new Intent(MainActivity.this, NoteDetails.class);
                    intent.putExtra("State", "editing");
                    intent.putExtra("Note Item", notesList.get(position));
                    // Use this for changing note on saving.
                    intent.putExtra("Note position", position);

                    // asks for activity which wishes to start and request code
                    startActivityForResult(intent, 1);
                    dimmer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Opens note details poge and requests new info for note either new note or exisiting.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            // EDITING
            if (resultCode == RESULT_OK) {
                String headingText = data.getStringExtra("Heading text");
                String bodyText = data.getStringExtra("Body text");
                String timestamp = data.getStringExtra("Timestamp");
                int color = data.getIntExtra("Selected color", Color.parseColor("#c6d8d3"));
                int position = data.getIntExtra("Note position", -1); // if nothing returned then -1

                NoteItem changingNoteItem = notesList.get(position);
                changingNoteItem.setHeading(headingText);
                changingNoteItem.setBody(bodyText);
                changingNoteItem.setColor(color);
                changingNoteItem.setTimestamp(timestamp);

                // Reorder notes for the recyclerview
                sortNotesDescDate();
                dbHelper.updateNote(changingNoteItem);
                // Tell view that item has moved from old position to new position
                recyclerAdapter.notifyItemChanged(position);
                recyclerAdapter.notifyItemMoved(position, 0);
            }
        }
        // CREATION
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String headingText = data.getStringExtra("Heading text");
                String bodyText = data.getStringExtra("Body text");
                int color = data.getIntExtra("Selected color", Color.parseColor("#c6d8d3"));
                String timestamp = data.getStringExtra("Timestamp");
                // Default to this icon for now
//                String color = "#C6D8D3";
                String icon = "android";

                // if the note is completely empty then stop process
                if (headingText.trim().length() == 0 && bodyText.trim().length() == 0){
                    return;
                }

                NoteItem newNote =  new NoteItem(headingText, bodyText, color, icon, timestamp);
                Log.i("New Note", newNote.getBody());
                notesList.add(newNote);
                sortNotesDescDate();
                dbHelper.addNote(newNote);
                // Assign note id from database insertion autoincrementation
                newNote.setId(dbHelper.getNewestId());
                // notify adapter that a new addition at end of list which is length
                recyclerAdapter.notifyItemInserted(0);
                checkDeleteButton();
            }
        }
        // get rid of dimmer
        dimmer.setVisibility(View.INVISIBLE);
    }

    protected TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            filter(s.toString());
        }
    };

    private void filter(String text) {
        ArrayList<NoteItem> filteredList = new ArrayList<>();
            for (NoteItem note : notesList){
                if (note.getHeading().toLowerCase().contains(text) || note.getBody().toLowerCase().contains(text)) {
                    filteredList.add(note);
                }
            }
            // Update the list shown on recyclerview
            recyclerAdapter.filterList(filteredList);

    }

    public void removeItem(NoteItem noteItem){
        int noteItemPosition = notesList.indexOf(noteItem);
        dbHelper.deleteNote(noteItem.getId());
        notesList.remove(noteItemPosition);
        recyclerAdapter.notifyItemRemoved(noteItemPosition);
        checkDeleteButton();
    }

    public void removeDeleteMode() {
        deletePrompt.setVisibility(View.GONE);
        confirmDelete.setVisibility(View.GONE);
        cancelDelete.setVisibility(View.GONE);
        addButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        recyclerAdapter.setDelMode(false);

        // Delay before refreshing dataset for nondel mode
        (new Handler()).postDelayed(() -> {
            recyclerAdapter.notifyDataSetChanged();
        }, 130);
    }

    public void instateDeleteMode() {
        recyclerAdapter.setDelMode(true);
        deletePrompt.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        confirmDelete.setVisibility(View.VISIBLE);
        cancelDelete.setVisibility(View.VISIBLE);

        recyclerAdapter.notifyDataSetChanged();
    }

    /*Hides keyboard*/
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     * Returns true if the notes list is empty
     */
    private boolean isNotesListEmpty(){
        return notesList.size() < 1 ? true: false;
        }

    /**
     *  Make delete button toggle visble if there are notes.
      */
    private void checkDeleteButton(){
        if (isNotesListEmpty()) {
            deleteButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
        }
    }

}