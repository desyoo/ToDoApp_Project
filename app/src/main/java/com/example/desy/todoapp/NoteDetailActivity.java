package com.example.desy.todoapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.desy.todoapp.database.NotesContract;

import java.util.Calendar;
import java.util.Locale;


/**
 * Created by desy on 6/8/16.
 */
public class NoteDetailActivity extends BaseActivity {
    // Constants
    private static final String TAG = NoteDetailActivity.class.getSimpleName();
    public static final int NORMAL = 1;
    public static final int LIST = 2;
    public static final int CAMERA_REQUEST = 1888;
    public static final int TAKE_GALLERY_CODE = 1;
    private static int sMonth, sYear, sHour, sDay, sMinute, sSecond;
    private static TextView sDateTextView, sTimeTextView;
    private static Button adf;
    private static boolean sIsInAuth;
    private static String sTmpFlNm;
    private String mCameraFileName;
    private NoteCustomList mNoteCustomList;
    private EditText mTitleEditText, mDescriptionEditText;
    private ImageView mNoteImage;
    private String mImagePath = AppConstant.NO_IMAGE;
    private String mId;
    private boolean mGoingToCameraOrGallery = false, mIsEditing = false;
    private boolean mIsImageSet = false;
    private boolean mIsList = false;
    private Bundle mBundle;
    private boolean mIsNotificationMode = false;
    private String mDescription;
    private Button bt_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBundle = savedInstanceState;
        setContentView(R.layout.activity_detail_note_layout);
        activateToolbarWithHomeEnabled();

        initializeComponents();

        setUpIfEditing();

    }

    private void initializeComponents() {
        mNoteCustomList = new NoteCustomList(this);
        mNoteCustomList.setUp();

        mTitleEditText = (EditText) findViewById(R.id.make_note_title);
        mNoteImage = (ImageView) findViewById(R.id.image_make_note);
        mDescriptionEditText = (EditText) findViewById(R.id.make_note_detail);
        sDateTextView = (TextView) findViewById(R.id.date_textview_make_note);
        sTimeTextView = (TextView) findViewById(R.id.time_textview_make_note);
        ImageView datePickerImageView = (ImageView) findViewById(R.id.date_picker_button);
        final ImageView dateTimeDeleteImageView = (ImageView) findViewById(R.id.delete_make_note);

//        dateTimeDeleteImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sDateTextView.setText("");
//                sTimeTextView.setText(AppConstant.NO_TIME);
//            }
//        });

//        datePickerImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AppDatePickerDialog datePickerDialog = new AppDatePickerDialog();
//                datePickerDialog.show(getSupportFragmentManager(), AppConstant.DATE_PICKER);
//            }
//        });

    }



    private void setUpIfEditing() {
        if (getIntent().getStringExtra(AppConstant.ID) != null) {
            mId = getIntent().getStringExtra(AppConstant.ID);
            mIsEditing = true;
            setValues(mId);
        }

    }



    private void setValues(String id) {
        String[] projection = {BaseColumns._ID,
                NotesContract.NotesEntry.NOTES_TITLE,
                NotesContract.NotesEntry.NOTES_DESCRIPTION,
                NotesContract.NotesEntry.NOTES_DATE,
                NotesContract.NotesEntry.NOTES_IMAGE,
                NotesContract.NotesEntry.NOTES_TIME};
        // Query database - check parameters to return only partial records.
        Uri r = NotesContract.NotesEntry.URI_TABLE;
        String selection = NotesContract.NotesEntry.NOTE_ID + " = " + id;
        Cursor cursor = getContentResolver().query(r, projection, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_TITLE));
                    String description = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_DESCRIPTION));
                    String time = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_TIME));
                    String date = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_DATE));
                    String image = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.NOTES_IMAGE));

                    mTitleEditText.setText(title);

                    mDescriptionEditText.setText(description);

                    sTimeTextView.setText(time);
                    sDateTextView.setText(date);
                    mImagePath = image;
                    if (!image.equals(AppConstant.NO_IMAGE)) {
                        mNoteImage.setImageBitmap(MainNoteActivity.mSendingImage);
                    }


                } while (cursor.moveToNext());
            }
        }

    }

    private void setValues(Note note) {
        getSupportActionBar().setTitle(AppConstant.REMINDERS);
        String title = note.getTitle();
        String description = note.getDescription();
        String time = note.getTime();
        String date = note.getDate();
        String image = note.getImagePath();

        mTitleEditText.setText(title);

        mDescriptionEditText.setText(description);

        sTimeTextView.setText(time);
        sDateTextView.setText(date);
        mImagePath = image;

    }

    protected void saveNote() {
        if (mIsEditing) {

            editForSaveInDevice();

        } else if (mTitleEditText.getText().toString().length() > 0 && !mGoingToCameraOrGallery) {

            saveInDevice();

        }
        startActivity(new Intent(NoteDetailActivity.this, MainNoteActivity.class));
        finish();
    }


    private void removeFromReminder(Note reminder) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.parse(NotesContract.BASE_CONTENT_URI + "/notes/" + reminder.getId());
        cr.delete(uri, null, null);
    }

    private void editForSaveInDevice() {
        ContentValues values = createContentValues(mImagePath, false);
        updateNote(values);
        createNoteAlarm(values, Integer.parseInt(mId));
    }

    private void saveInDevice() {
        ContentValues values = createContentValues(mImagePath,  true);
        int id = insertNote(values);
        mId = id + "";
        createNoteAlarm(values, id);
    }


    private ContentValues createContentValues(String noteImage,  boolean isSave) {
        if(noteImage == null || noteImage.equals("")) {
            noteImage = AppConstant.NO_IMAGE;
        }
        ContentValues values = new ContentValues();
        values.put(NotesContract.NotesEntry.NOTES_TITLE, mTitleEditText.getText().toString().toUpperCase());
        values.put(NotesContract.NotesEntry.NOTES_DATE, sDateTextView.getText().toString());
        values.put(NotesContract.NotesEntry.NOTES_TIME, sTimeTextView.getText().toString());
        if(mIsImageSet || isSave) {
            values.put(NotesContract.NotesEntry.NOTES_IMAGE, noteImage);
        }

        //String type = AppConstant.NORMAL;
        String description = mDescriptionEditText.getText().toString();

        values.put(NotesContract.NotesEntry.NOTES_DESCRIPTION, description);
        Log.d (TAG, values.toString());
        return values;
    }

    private void updateNote(ContentValues values) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse(NotesContract.BASE_CONTENT_URI + "/notes");
        String selection = NotesContract.NotesEntry.NOTE_ID + " = " + mId;
        contentResolver.update(uri, values, selection, null);
    }

    private void createNoteAlarm(ContentValues values, int id) {
        if(!sTimeTextView.getText().toString().equals(AppConstant.NO_TIME)) {
            Note note = new Note(id, values.getAsString(NotesContract.NotesEntry.NOTES_TITLE),
                    values.getAsString(NotesContract.NotesEntry.NOTES_DESCRIPTION),
                    values.getAsString(NotesContract.NotesEntry.NOTES_DATE),
                    values.getAsString(NotesContract.NotesEntry.NOTES_TIME)
                    );
            note.setImagePath(values.getAsString(NotesContract.NotesEntry.NOTES_IMAGE));
            //setAlarm(getTargetTime(), note);
        } 
    }

    private Calendar getTargetTime() {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();
        calSet.set(Calendar.MONTH, sMonth);
        calSet.set(Calendar.YEAR, sYear);
        calSet.set(Calendar.DAY_OF_MONTH, sDay);
        calSet.set(Calendar.HOUR_OF_DAY, sHour);
        calSet.set(Calendar.MINUTE, sMinute);
        calSet.set(Calendar.SECOND, sSecond);
        calSet.set(Calendar.MILLISECOND, 0);
        if (calSet.compareTo(calNow) <= 0) {
            calSet.add(Calendar.DATE, 1);
        }

        return calSet;
    }


    private int insertNote(ContentValues values) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse(NotesContract.BASE_CONTENT_URI + "/notes");
        Uri returned = contentResolver.insert(uri, values);
        String[] temp = returned.toString().split("/");
        return Integer.parseInt(temp[temp.length-1]);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NoteDetailActivity.this, MainNoteActivity.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(NoteDetailActivity.this, MainNoteActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(AppConstant.TMP_FILE_NAME, sTmpFlNm);
        outState.putString("mCameraFileName", mCameraFileName);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_notes, menu);
        return true;
    }

    public void createNote(View view) {
        saveNote();
    }


    public static class AppDatePickerDialog extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private int mYear, mMonth, mDay;
        private String tempMonth;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            tempMonth = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK);
            return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if(year == mYear) {
                if(monthOfYear == mMonth) {
                    if(dayOfMonth == mDay) {
                        sDateTextView.setText(AppConstant.TODAY);
                    } else {
                        sDateTextView.setText(dayOfMonth + " " + sMonth);
                    }
                } else {
                    sDateTextView.setText(dayOfMonth + " " + sMonth);
                }
            } else {
                sDateTextView.setText(dayOfMonth + " " + sMonth + " " + year);
            }
            sYear = year;
            sMonth = monthOfYear;
            sDay = dayOfMonth;
            AppTimePickerDialog timePickerDialog = new AppTimePickerDialog();
            timePickerDialog.show(getFragmentManager(), AppConstant.DATE_PICKER);
        }
    }


    public static class AppTimePickerDialog extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private int mHour, mMinute;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, mHour, mMinute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(minute <10) {
                sTimeTextView.setText(hourOfDay + ":0" + minute);
            } else {
                sTimeTextView.setText(hourOfDay + ":" + minute);
            }
            sHour = hourOfDay;
            sMinute = minute;
            sSecond = 0;
        }
    }



}