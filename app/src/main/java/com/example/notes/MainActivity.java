package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    // Used to load individual data sets into recycler rather than loading all at once and giving poor performance.
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<NoteItem> notesList = new ArrayList<>();
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // keep size of the view the same size no matter number of items
        recyclerLayoutManager = new GridLayoutManager(this, 2);
        recyclerAdapter = new NotesAdapter(notesList);

        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}