package com.example.desy.todoapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.desy.todoapp.database.NotesContract.NotesEntry;

/**
 * Created by desy on 8/30/16.
 */
public class NotesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "notes.db";

    public NotesDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NotesEntry.TABLE_NAME + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NotesEntry.NOTES_TITLE + " TEXT NOT NULL, " +
                NotesEntry.NOTES_DESCRIPTION + " TEXT NOT NULL, " +
                NotesEntry.NOTES_DATE + " TEXT NOT NULL, " +
                NotesEntry.NOTES_TIME + " TEXT NOT NULL, " +
                NotesEntry.NOTES_IMAGE + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotesEntry.TABLE_NAME);
        onCreate(db);
    }
}
