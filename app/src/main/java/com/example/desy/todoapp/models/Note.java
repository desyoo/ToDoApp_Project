package com.example.desy.todoapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by desy on 8/30/16.
 */
public class Note {
    private String mTitle, mDescription, mDate, mImagePath;
    private int mId;
    private Bitmap mBitmap;
    private boolean mHasNoImage = false;

    public Note (int mId, String mTitle, String mDescription, String mDate) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mDate = mDate;
    }


    // Create note from a reminderString (contains contents in a single string
    // delimited by a $ sign - see convertToString method later which
    // creates this.
    public Note(String reminderString) {
        // using \\ before a character tells the function
        // to NOT treat the character as a special regular expression
        // $ is normally interpreted as end of line or end of string
        String[] fields = reminderString.split("\\$");

        this.mId = Integer.parseInt(fields[0]);
        this.mTitle = fields[1];
        //this.mTime = fields[2];
        this.mImagePath = fields[2];
        this.mDate = fields[3];


        this.mDescription = fields[7];
        Note aNote = new Note(this.mId, this.mTitle, this.mDescription, this.mDate);
            // Previous constructor does not set this, so we do it manually after invoking
            // the constructor
        aNote.setImagePath(this.mImagePath);

    }


    public void setBitmap(String path) {
        setImagePath(path);
        this.mBitmap = BitmapFactory.decodeFile(path);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setHasNoImage(boolean hasNoImage) {
        mHasNoImage = hasNoImage;
    }
}
