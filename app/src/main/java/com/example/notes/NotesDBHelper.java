package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

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

    /**
     *  Build a list of NoteItem instances from each row of table.
     *  Return list of NoteItems
     */
    public ArrayList<NoteItem> getAllNotes(){
        String tableName = NotesContract.NoteEntry.TABLE_NAME;
        String[] columns = {"_id", "heading", "body", "color", "icon", "timestamp"};

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<NoteItem> storeNotes = new ArrayList<>();
        Cursor cursor = db.query(
                tableName,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()){
            do {
                String id = cursor.getString(0);
                String heading = cursor.getString(1);
                String body = cursor.getString(2);
                int color = cursor.getInt(3);
                String icon = cursor.getString(4);
                String timestamp = cursor.getString(5);
                // Add cursor row to storeNotes
                storeNotes.add(new NoteItem(id, heading, body, color, icon, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeNotes;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotesContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }


    public void addNote(NoteItem noteItem){
        ContentValues cv = new ContentValues();
        cv.put(NotesContract.NoteEntry.COLUMN_HEADING, noteItem.getHeading());
        cv.put(NotesContract.NoteEntry.COLUMN_BODY, noteItem.getBody());
        cv.put(NotesContract.NoteEntry.COLUMN_COLOR, noteItem.getColor());
        cv.put(NotesContract.NoteEntry.COLUMN_ICON, noteItem.getIcon());
        cv.put(NotesContract.NoteEntry.COLUMN_TIMESTAMP, noteItem.getTimestamp());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(NotesContract.NoteEntry.TABLE_NAME, null, cv);
    }

    public void updateNote(NoteItem noteItem){
        ContentValues cv = new ContentValues();
        cv.put(NotesContract.NoteEntry.COLUMN_HEADING, noteItem.getHeading());
        cv.put(NotesContract.NoteEntry.COLUMN_BODY, noteItem.getBody());
        cv.put(NotesContract.NoteEntry.COLUMN_COLOR, noteItem.getColor());
        cv.put(NotesContract.NoteEntry.COLUMN_ICON, noteItem.getIcon());
        cv.put(NotesContract.NoteEntry.COLUMN_TIMESTAMP, noteItem.getTimestamp());

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(NotesContract.NoteEntry.TABLE_NAME, cv, "_id = ?", new String[] {String.valueOf(noteItem.getId())});
    }

    public void deleteNote(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(NotesContract.NoteEntry.TABLE_NAME, "_id=?", new String[] {row_id});
        if (result == -1){
            Log.i("Delete state", "deletion failed");
        }
        else {
            Log.i("Delete state", "deleted");
        }
    }

    public String getNewestId() {
        String id = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String tableName = NotesContract.NoteEntry.TABLE_NAME;
        String[] columns = {"MAX(_id)"};
        Cursor cursor = db.query(
                tableName,
                columns,
                null,
                null,
                null,
                null,
                null,
                "1");

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getString(0);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return id;
    }
}
