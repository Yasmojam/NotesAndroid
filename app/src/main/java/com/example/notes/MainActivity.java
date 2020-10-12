package com.example.notes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    // Used to load individual data sets into recycler rather than loading all at once and giving poor performance.
    private NotesAdapter recyclerAdapter;
    private GridLayoutManager recyclerLayoutManager;
//    private RecyclerView.LayoutManager recyclerLayoutManager;
    ArrayList<NoteItem> notesList;

    private LinearLayout searchContainer;
    private EditText searchBar;
//    private ImageView chooseDelButton;
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

        createNotesList();
        buildRecyclerView();
        setButtonsEditText();

    }

    public void setButtonsEditText() {
        deleteButton = findViewById(R.id.delButton);
        addButton = findViewById(R.id.addButton);
        searchContainer = findViewById(R.id.searchContainer);
        searchBar = findViewById(R.id.editSearch);
        searchButton = findViewById(R.id.searchButton);
        clearButton = findViewById(R.id.clearButton);
//        chooseDelButton = findViewById(R.id.chooseDelButton);

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
    public void createNotesList(){
        notesList = new ArrayList<>();
        notesList.add(new NoteItem(R.drawable.ic_shopping_basket, "To buy", "Buy stuff", "#D88C9A"));
        notesList.add(new NoteItem(R.drawable.ic_assignment, "Assignment", "Do assignment", "#F2D0A9"));
        notesList.add(new NoteItem(R.drawable.ic_bookmark, "Here  Here bookmark Here bookmark v Here bookmark Here bookmark Here bookmark", "Bookmark is here", "#F1E3D3"));
        notesList.add(new NoteItem(R.drawable.ic_shopping_basket, "To buy", "Buy stuff", "#99C1B9"));
        notesList.add(new NoteItem(R.drawable.ic_assignment, "Assignment", "Do assignment", "#8E7DBE"));
        notesList.add(new NoteItem(R.drawable.ic_bookmark, "Here bookmark", "Bookmark is here", "#D88C9A"));
        notesList.add(new NoteItem(R.drawable.ic_shopping_basket, "To buy", "Buy stuff", "#F2D0A9"));
        notesList.add(new NoteItem(R.drawable.ic_assignment, "Assignment", "Do assignment", "#F1E3D3"));
        notesList.add(new NoteItem(R.drawable.ic_bookmark, "Here bookmark", "Bookmark is here", "#99C1B9"));
        notesList.add(new NoteItem(R.drawable.ic_shopping_basket, "To buy", "Buy stuff", "#8E7DBE"));
        notesList.add(new NoteItem(R.drawable.ic_assignment, "Assignment", "Do assignment", "#D88C9A"));
        notesList.add(new NoteItem(R.drawable.ic_bookmark, "Here bookmark", "Bookmark is here Bookmark is here Bookmark is here Bookmark is here Bookmark is herev Bookmark is here", "#F2D0A9"));
    }

    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // keep size of the view the same size no matter number of items
        recyclerLayoutManager = new GridLayoutManager(this, 2);
        recyclerAdapter = new NotesAdapter(notesList);
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
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK) {
                String headingText = data.getStringExtra("Heading text");
                String bodyText = data.getStringExtra("Body text");
                int position = data.getIntExtra("Note position", -1); // if nothing returned then -1

                NoteItem changingNoteItem = notesList.get(position);
                changingNoteItem.setHeading(headingText);
                changingNoteItem.setBody(bodyText);
                recyclerAdapter.notifyItemChanged(position);
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String headingText = data.getStringExtra("Heading text");
                String bodyText = data.getStringExtra("Body text");
                // Default to this colour & icon for now
                String color = "#C6D8D3";
                int imageResource = R.drawable.ic_android;

                NoteItem newNote =  new NoteItem(imageResource, headingText, bodyText, color);
                notesList.add(newNote);
                // notify adapter that a new addition at end of list which is length
                recyclerAdapter.notifyItemInserted(notesList.size()-1);
            }
        }
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

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState = appState;
    }

    public void removeItem(int position){
        notesList.remove(position);
        recyclerAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text){
        notesList.get(position).setHeading(text);
        recyclerAdapter.notifyItemChanged(position);
    }

    public void initiateDeletion(){
        if (!isDeleting){
            isDeleting = true;

            addButton.setVisibility(View.INVISIBLE);
//            chooseDelButton.setVisibility(View.VISIBLE);
            deleteButton.setImageResource(R.drawable.ic_done);

        }
        else if (isDeleting){
            isDeleting = false;
            addButton.setVisibility(View.VISIBLE);
//            chooseDelButton.setVisibility(View.INVISIBLE);
            deleteButton.setImageResource(R.drawable.ic_delete);
        }
    }

    /*Hides keyboard*/
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}