package com.example.desy.todoapp.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by desy on 8/30/16.
 */
public class NotesProvider extends ContentProvider{
    protected NotesDbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int NOTES = 100;
    private static final int NOTES_ID = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = NotesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "notes", NOTES);
        matcher.addURI(authority, "notes/*", NOTES_ID);
        return matcher;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        //NotesDbHelper.deleteDatabase(getContext());
        mOpenHelper = new NotesDbHelper(getContext());
    }




    @Override
    public boolean onCreate() {
        mOpenHelper = new NotesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(match) {
            case NOTES:
                queryBuilder.setTables("notes");
                break;
            case NOTES_ID:
                queryBuilder.setTables("notes");
                String noteId = NotesContract.NotesEntry.getNoteId(uri);
                queryBuilder.appendWhere(BaseColumns._ID + "=" + noteId);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case NOTES:
                return NotesContract.NotesEntry.CONTENT_TYPE;
            case NOTES_ID:
                return NotesContract.NotesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                long noteRecordId = db.insertOrThrow("notes", null, values);
                return NotesContract.NotesEntry.buildNoteUri(String.valueOf(noteRecordId));

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uri.equals(NotesContract.BASE_CONTENT_URI)) {
            deleteDatabase();
            return 0;
        }

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case NOTES_ID:
                String noteId = NotesContract.NotesEntry.getNoteId(uri);
                String notesSelectionCriteria = BaseColumns._ID + "=" + noteId
                        + (!TextUtils.isEmpty(selection) ? " AND ( " + selection + ")" : "");
                return db.delete("notes", notesSelectionCriteria, selectionArgs);

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String selectionCriteria = selection;
        switch (match) {
            case NOTES:
                return db.update("notes", values, selection, selectionArgs);

            case NOTES_ID:
                String noteId = NotesContract.NotesEntry.getNoteId(uri);
                selectionCriteria = BaseColumns._ID + "=" + noteId
                        + (!TextUtils.isEmpty(selection) ? " AND ( " + selection + ")" : "");
                return db.update("notes", values, selectionCriteria, selectionArgs);


            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }
}
