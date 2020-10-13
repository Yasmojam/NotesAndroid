package com.example.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NotesDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 1;

    public NotesDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NotesContract.NoteEntry.TABLE_NAME
                + " (" +
                NotesContract.NoteEntry._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotesContract.NoteEntry.COLUMN_HEADING + " TEXT NOT NULL, " +
                NotesContract.NoteEntry.COLUMN_BODY + " TEXT NOT NULL, " +
                NotesContract.NoteEntry.COLUMN_COLOR + " TEXT NOT NULL, " +
                NotesContract.NoteEntry.COLUMN_ICON + " TEXT NOT NULL, " +
                NotesContract.NoteEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotesContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
