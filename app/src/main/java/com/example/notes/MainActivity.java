package com.example.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

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


    private View dimmer;
    private LinearLayout searchContainer;
    private EditText searchBar;
    private ImageButton deleteButton;
    private ImageButton addButton;
    private ImageButton searchButton;
    private ImageView clearButton;
    private Boolean isDeleting = false;
    private Boolean isSearching = false;


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
        searchContainer = findViewById(R.id.searchContainer);
        searchBar = findViewById(R.id.editSearch);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateDeletion();
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchContainer.isShown()){
                    searchContainer.setVisibility(View.GONE);
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
            public void onDeleteClick(int position) {
                removeItem(position);
            }

            @Override
            public void onClickNote(int position) {
                // try to change bg of detail card
                Log.i("clicked", "Note No. " + position  + " clicked");
                Intent intent = new Intent(MainActivity.this, NoteDetails.class);
                intent.putExtra("State", "editing");
                intent.putExtra("Note Item", notesList.get(position));
                // Use this for changing note on saving.
                intent.putExtra("Note position", position);

                // asks for activity which wishes to start and request code
                startActivityForResult(intent, 1);
                dimmer.setVisibility(View.VISIBLE);
//                startActivity(intent);
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
                int position = data.getIntExtra("Note position", -1); // if nothing returned then -1

                NoteItem changingNoteItem = notesList.get(position);
                changingNoteItem.setHeading(headingText);
                changingNoteItem.setBody(bodyText);
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
                String timestamp = data.getStringExtra("Timestamp");
                // Default to this colour & icon for now
                String color = "#C6D8D3";
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
                // notify adapter that a new addition at end of list which is length
                recyclerAdapter.notifyItemInserted(0);
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
            recyclerAdapter.getFilter().filter(s);
        }
    };

    public void removeItem(int position){
        dbHelper.deleteNote(notesList.get(position).getId());
        notesList.remove(position);
        recyclerAdapter.notifyItemRemoved(position);
    }

    public void initiateDeletion(){
        if (!isDeleting){
            isDeleting = true;

            addButton.setVisibility(View.INVISIBLE);
            deleteButton.setImageResource(R.drawable.ic_done);

            recyclerAdapter.setDelVisible(true);
            recyclerAdapter.notifyDataSetChanged();

        }
        else if (isDeleting){
            isDeleting = false;
            addButton.setVisibility(View.VISIBLE);
            deleteButton.setImageResource(R.drawable.ic_delete);

            recyclerAdapter.setDelVisible(false);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    /*Hides keyboard*/
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}