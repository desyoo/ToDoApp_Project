package com.example.desy.todoapp.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by desy on 8/30/16.
 */
public class NotesContract {

    public static final String CONTENT_AUTHORITY = "com.example.desy.todoapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_NOTES = "notes";


    public static final class NotesEntry implements BaseColumns {
        public static final Uri URI_TABLE = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_NOTES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        //public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".notes";
        //public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".notes";


        public static final String TABLE_NAME = "notes";

        public static final String NOTE_ID = "_ID";
        public static final String NOTES_TITLE = "notes_title";
        public static final String NOTES_DESCRIPTION = "notes_description";
        public static final String NOTES_DATE = "note_date";
        public static final String NOTES_TIME = "notes_time";
        public static final String NOTES_IMAGE = "notes_image";

        public static Uri buildNoteUri(String noteId) {
            return URI_TABLE.buildUpon().appendEncodedPath(noteId).build();
        }

        public static String getNoteId(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }
}
