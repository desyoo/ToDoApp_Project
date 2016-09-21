package com.example.desy.todoapp.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.desy.todoapp.R;
import com.example.desy.todoapp.models.Note;
import com.example.desy.todoapp.utils.AppConstant;

import java.util.Collections;
import java.util.List;

/**
 * Created by desy on 8/30/16.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {

    private LayoutInflater mInflater;
    private List<Note> mNotes = Collections.emptyList();
    private Context mContext;

    public NotesAdapter (Context mContext, List<Note> mNotes) {
        mInflater = LayoutInflater.from(mContext);
        this.mNotes = mNotes;
        this.mContext = mContext;
    }

    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_notes_adapter_layout, parent, false);
        return new NoteHolder(view);
    }


    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        holder.mId.setText(mNotes.get(position).getId() + "");
        holder.mTitle.setText(mNotes.get(position).getTitle());

        holder.mDescription.setText(mNotes.get(position).getDescription());

        if (!mNotes.get(position).getDate().equals("")) {
            String temp = mNotes.get(position).getDate();
            if (temp.equals(mContext.getString(R.string.High_Priority))) {
                holder.mDate.setTextColor(ContextCompat.getColor(mContext,R.color.colorAccent));
            } else if (temp.equals(mContext.getString(R.string.Medium_Priority))) {
                holder.mDate.setTextColor(ContextCompat.getColor(mContext,R.color.notesColorBackgroundAccent));
            } else if (temp.equals(mContext.getString(R.string.Low_Priority))) {
                holder.mDate.setTextColor(ContextCompat.getColor(mContext,R.color.notesPrimaryBackgroundColor));
            } else if (temp.equals(mContext.getString(R.string.Done_Priority))) {
                holder.mDate.setTextColor(ContextCompat.getColor(mContext,R.color.notesSecondaryTextColor));
            }
            holder.mDate.setText(mNotes.get(position).getDate());

        } else {
            holder.mDate.setVisibility(View.GONE);
        }

        Log.d("NotesAdapter", mNotes.get(position).getDate());

        // Display an image, but only if we have one to display.
        if (mNotes.get(position).getBitmap() != null) {
            holder.mImage.setImageBitmap(mNotes.get(position).getBitmap());
            holder.mImage.setVisibility(View.VISIBLE);
        } else if (mNotes.get(position).getImagePath() == null || mNotes.get(position).getImagePath().equals(AppConstant.NO_IMAGE)) {
            // No image, so hide.
            holder.mImage.setVisibility(View.GONE);
        } else {
            // Just in case...
            holder.mImage.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void setData(List<Note> notes) {
        this.mNotes = notes;
    }

    public void delete(int position) {
        mNotes.remove(position);
        notifyItemRemoved(position);
    }


    public class NoteHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mDescription, mDate, mId;
        ImageView mImage;
        LinearLayout mLinearLayout;

        public NoteHolder(View itemView) {
            super(itemView);
            mId = (TextView) itemView.findViewById(R.id.id_note_custom_home);
            mTitle = (TextView) itemView.findViewById(R.id.title_note_custom_home);
            mDescription = (TextView) itemView.findViewById(R.id.description_note_custom_home);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.home_list);
            mDate = (TextView) itemView.findViewById(R.id.date_time_note_custom_home);
            mImage = (ImageView) itemView.findViewById(R.id.image_note_custom_home);
        }
    }
}
