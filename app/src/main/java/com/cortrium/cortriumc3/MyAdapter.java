package com.cortrium.cortriumc3;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Kike Bodi on 13/07/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final String TAG = MyAdapter.class.getName();
    private File[] mDataset;
    private RecordingsFragment mContext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView filename;
        public TextView folder;
        public ViewHolder(ConstraintLayout v) {
            super(v);
            filename = (TextView) v.findViewById(R.id.filename);
            folder = (TextView) v.findViewById(R.id.folder);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(File[] myDataset, RecordingsFragment myContext) {
        mDataset = myDataset;
        mContext = myContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recording_card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        v.setOnClickListener(mContext.getOnClickListener());

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.filename.setText(mDataset[position].getName());
        //holder.folder.setText(mDataset[position].getParentFile().getName());
        holder.folder.setText("");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
