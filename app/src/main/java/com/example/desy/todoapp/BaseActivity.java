package com.example.desy.todoapp;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by desy on 8/30/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    // Operation type (what is being executed)
    public static final int NOTES = 1;
    public static final int REMINDERS = 2;
    public static final int ARCHIVES = 3;
    public static final int TRASH = 4;
    public static final int SETTINGS = 5;

    public static String mTitle = AppConstant.NOTES;
    // Default type of operation
    public static int mType = NOTES;

    protected Toolbar mToolBar;


    protected Toolbar activateToolbar() {
        if (mToolBar == null) {
            mToolBar = (Toolbar) findViewById(R.id.app_bar);
            if (mToolBar != null) {
                setSupportActionBar(mToolBar);
                switch (mType) {
                    case REMINDERS:
                        getSupportActionBar().setTitle(AppConstant.REMINDERS);
                        break;
                    case NOTES:
                        getSupportActionBar().setTitle(AppConstant.NOTES);
                        break;
                    case ARCHIVES:
                        getSupportActionBar().setTitle(AppConstant.ARCHIVES);
                        break;
                    case TRASH:
                        getSupportActionBar().setTitle(AppConstant.TRASH);
                        break;
                }
            }
        }
        return mToolBar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        activateToolbar();
        if (mToolBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (mType == REMINDERS)
                getSupportActionBar().setTitle(AppConstant.MAKE_REMINDER);
            else if (mType == NOTES)
                getSupportActionBar().setTitle(AppConstant.MAKE_NOTES);
            else if (mType == SETTINGS)
                getSupportActionBar().setTitle(AppConstant.SETTINGS);
        }
        return mToolBar;
    }

}
