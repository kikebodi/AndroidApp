package com.cortrium.cortriumc3.RecordingsTab;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cortrium.cortriumc3.R;

import java.io.File;
import java.util.List;

/**
 * Created by Kike Bodi on 13/07/2017.
 * Cortrium
 * bodi.inf@gmail.com
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final String TAG = MyAdapter.class.getName();
    private List<File> mDataset;
    private RecordingsFragment mContext;
    CustomItemClickListener listener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView filename;
        public ImageButton deleteButton;
        public ImageButton uploadButton;
        public ViewHolder(ConstraintLayout v, final CustomItemClickListener listener) {
            super(v);
            filename = (TextView) v.findViewById(R.id.filename);
            deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
            uploadButton = (ImageButton) v.findViewById(R.id.uploadButton);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<File> myDataset, RecordingsFragment myContext, CustomItemClickListener listener) {
        mDataset = myDataset;
        mContext = myContext;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recording_card_view, parent, false);
        //v.setOnClickListener(mContext.getOnClickListener());
        final ViewHolder vh = new ViewHolder(v,listener);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.filename.setText(mDataset.get(position).getName());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteItem(position);
            }
        });
        holder.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.uploadItem(position);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
