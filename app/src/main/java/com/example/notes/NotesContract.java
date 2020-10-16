package com.example.notes;

import android.provider.BaseColumns;

// Contains all table columns
public class NotesContract {

    private NotesContract() {}
    public static final class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "allNotes";
        public static final String COLUMN_HEADING = "heading";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
