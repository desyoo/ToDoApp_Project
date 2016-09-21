package com.example.desy.todoapp.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.desy.todoapp.utils.AppConstant;
import com.example.desy.todoapp.models.Note;
import com.example.desy.todoapp.adapters.NotesAdapter;
import com.example.desy.todoapp.NotesLoader;
import com.example.desy.todoapp.R;
import com.example.desy.todoapp.RecyclerItemClickListener;
import com.example.desy.todoapp.database.NotesContract;

import java.util.ArrayList;
import java.util.List;

public class MainNoteActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<Note>>{
    private static final String TAG = MainNoteActivity.class.getSimpleName();
    private List<Note> mNotes;
    private RecyclerView mRecyclerView;
    private NotesAdapter mNotesAdapter;
    private ContentResolver mContentResolver;
    public static Bitmap mSendingImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar();
        setUpRecyclerView();

    }


    private void setUpRecyclerView() {
        mContentResolver = getContentResolver();
        mNotesAdapter = new NotesAdapter(MainNoteActivity.this, new ArrayList<Note>());
        int LOADER_ID = 1;
        getSupportLoaderManager().initLoader(LOADER_ID, null, MainNoteActivity.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_home);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mNotesAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //edit(view);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                PopupMenu popupMenu = new PopupMenu(MainNoteActivity.this, view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.action_notes, popupMenu.getMenu());
                popupMenu.show();
                final View v = view;
                final int pos = position;
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_delete) {
                            delete(v, pos);
                        } else if(item.getItemId() == R.id.action_edit) {
                            edit(v);
                        }

                        return false;
                    }
                });
            }
        }));
    }


    private void edit(View view) {
        Intent intent = new Intent(MainNoteActivity.this, NoteDetailActivity.class);
        String id = ((TextView) view.findViewById(R.id.id_note_custom_home)).getText().toString();
        intent.putExtra(AppConstant.ID, id);
//        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.home_list);
//        int isList = linearLayout.getVisibility();
//        if (isList == View.VISIBLE) {
//            intent.putExtra(AppConstant.LIST, AppConstant.TRUE);
//        }
        ImageView tempImageView = (ImageView) view.findViewById(R.id.image_note_custom_home);
        if(tempImageView.getDrawable() != null) {
            mSendingImage = ((BitmapDrawable) tempImageView.getDrawable()).getBitmap();
        }
        startActivity(intent);
    }


    private void delete(View view, int position) {
        ContentResolver cr = this.getContentResolver();
        String _ID = ((TextView) view.findViewById(R.id.id_note_custom_home)).getText().toString();
        Uri uri = NotesContract.NotesEntry.buildNoteUri(_ID);
        cr.delete(uri, null, null);
        mNotesAdapter.delete(position);
        changeNoItemTag();
    }

    private void changeNoItemTag() {
        TextView noItemTextView = (TextView) findViewById(R.id.no_item_textview);
        if(mNotesAdapter.getItemCount() !=0) {
            noItemTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noItemTextView.setText(AppConstant.EMPTY);
            noItemTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add:
                Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                intent.putExtra(AppConstant.NOTE_OR_REMINDER, mTitle);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    public void onAddItem(View view) {

    }

    @Override
    public Loader<List<Note>> onCreateLoader(int id, Bundle args) {
        mContentResolver = getContentResolver();
        Log.d(TAG, "loader going through");
        return new NotesLoader(MainNoteActivity.this, mContentResolver, BaseActivity.mType);
    }

    @Override
    public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {
        this.mNotes = data;
        Log.d(TAG, mNotes.toString());
        for (final Note aNote: mNotes) {
            aNote.setHasNoImage(true);
        }
        mNotesAdapter.setData(mNotes);
        changeNoItemTag();
    }

    @Override
    public void onLoaderReset(Loader<List<Note>> loader) {
        mNotesAdapter.setData(null);
    }
}
