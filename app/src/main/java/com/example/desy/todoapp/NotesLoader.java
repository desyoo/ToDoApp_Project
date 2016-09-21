package com.example.desy.todoapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.desy.todoapp.database.NotesContract;
import com.example.desy.todoapp.models.Note;
import com.example.desy.todoapp.utils.AppConstant;

import java.util.ArrayList;
import java.util.Calendar;
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
                NotesContract.NotesEntry.NOTES_IMAGE };

        Uri uri = NotesContract.NotesEntry.URI_TABLE;
        mCursor = mContentResolver.query(uri, projection, null, null, BaseColumns._ID + " DESC");
        if(mCursor != null) {
            if(mCursor.moveToFirst()) {
                do {
                    String date = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_DATE));
                    String title = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_TITLE));
                    String description = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_DESCRIPTION));
                    String imagePath = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesEntry.NOTES_IMAGE));

                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID));

                    String priority =  setPriority(date);

                    Note note = new Note( _id, title, description, priority);
                    if(!imagePath.equals(AppConstant.NO_IMAGE)) {
                        note.setBitmap(imagePath);
                    } else {
                        note.setImagePath(AppConstant.NO_IMAGE);
                    }

                    entries.add(note);

                } while(mCursor.moveToNext());
            }
        }

        return entries;
    }

    private String setPriority(String date) {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);



        if (date.length() > 0 && !date.equals("") && !date.equals(getContext().getString(R.string.Today))) {
            String[] calendar = date.split("/");
            Log.d("NotesLoader ", String.format("Year: %s, Month: %s, Days: %s", calendar[2],calendar[0],calendar[1]));
            if (Integer.parseInt(calendar[2]) >= mYear) {
                if (Integer.parseInt(calendar[0]) >= mMonth) {
                    int diffDays = Integer.parseInt(calendar[1]) - mDay;
                    if (diffDays > 1 && diffDays < 4 && Integer.parseInt(calendar[0]) == mMonth) {
                        return getContext().getString(R.string.High_Priority);
                    } else if (diffDays >= 4 && diffDays < 8 && Integer.parseInt(calendar[0]) == mMonth){
                        return getContext().getString(R.string.Medium_Priority);
                    } else if (diffDays < 0 && Integer.parseInt(calendar[0]) <= mMonth){
                        return getContext().getString(R.string.Done_Priority);
                    } else {
                        return getContext().getString(R.string.Low_Priority);
                    }
                } else {
                    return getContext().getString(R.string.Done_Priority);
                }
            } else {
                return getContext().getString(R.string.Done_Priority);
            }
        } else if (date.equals(getContext().getString(R.string.Today))) {
            return getContext().getString(R.string.High_Priority);
        }

        return "";
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

