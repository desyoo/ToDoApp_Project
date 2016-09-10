package com.example.desy.todoapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.AsyncTaskLoader;

import com.example.desy.todoapp.database.NotesContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by desy on 6/7/16.
 */
public class NotesLoader extends AsyncTaskLoader<List<Note>> {
    private static final String TAG = NotesLoader.class.getSimpleName();
    private List<Note> mNotes;
    private ContentResolver mContentResolver;
    private Cursor mCursor;
    private int mType; // Reminder of a note

    public NotesLoader(Context context, ContentResolver contentResolver, int type) {
        super(context);
        mContentResolver = contentResolver;
        mType = type;
    }

    @Override
    public List<Note> loadInBackground() {
        List<Note> entries = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                NotesContract.NotesEntry.NOTES_TITLE,
                NotesContract.NotesEntry.NOTES_DESCRIPTION,
                NotesContract.NotesEntry.NOTES_DATE,
                NotesContract.NotesEntry.NOTES_TIME,
                NotesContract.NotesEntry.NOTES_IMAGE };

        Uri uri = NotesContract.NotesEntry.URI_TABLE;
        mCursor = mContentResolver.query(uri, projection, null, null, BaseColumns._ID + " DESC");
        if(mCursor != null) {
            if(mCursor.moveToFirst()) {
                do {
                    String date = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_DATE));
                    String time = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_TIME));
                    String title = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_TITLE));
                    String description = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_DESCRIPTION));
                    String imagePath = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_IMAGE));

                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID));

                    if(time.equals(AppConstant.NO_TIME)) {
                        time = "";
                        Note note = new Note( _id, title, description, date, time);
                        if(!imagePath.equals(AppConstant.NO_IMAGE)) {
                            note.setBitmap(imagePath);
                        } else {
                            note.setImagePath(AppConstant.NO_IMAGE);
                        }

                        entries.add(note);
                    }


                } while(mCursor.moveToNext());
            }
        }

        return entries;
    }

    @Override
    public void deliverResult(List<Note> notes) {
        if (isReset()) {
            if(notes != null) {
                releaseResources();
                return;
            }
        }
        List<Note> oldNotes = mNotes;
        mNotes = notes;
        if(isStarted()) {
            super.deliverResult(notes);
        }
        if(oldNotes != null && oldNotes != notes) {
            releaseResources();
        }
    }

    @Override
    protected void onStartLoading() {
        if(mNotes != null) {
            deliverResult(mNotes);
        }
        if(takeContentChanged()) {
            forceLoad();
        } else if(mNotes == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if(mNotes != null) {
            releaseResources();
            mNotes = null;
        }
    }


    @Override
    public void onCanceled(List<Note> notes) {
        super.onCanceled(notes);
        releaseResources();
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    private void releaseResources() {
        mCursor.close();
    }
}

