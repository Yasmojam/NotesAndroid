package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
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
        notesList.add(new NoteItem(R.drawable.ic_shopping_basket, "To buy", "Buy stuff", "#5D4E6D"));
        notesList.add(new NoteItem(R.drawable.ic_assignment, "Assignment", "Do assignment", "#8FA998"));
        notesList.add(new NoteItem(R.drawable.ic_bookmark, "Here bookmark", "Bookmark is here", "#C9F299"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // keep size of the view the same size no matter number of items
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerAdapter = new NotesAdapter(notesList);

        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}