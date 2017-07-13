package com.cortrium.cortriumc3;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/* Fragment used as page 2 */
public class RecordingsFragment extends Fragment {

    @BindView(R.id.recordings_recycler_view) RecyclerView mRecyclerView;
    private final String TAG = RecordingsFragment.class.getName();
    private Unbinder unbinder;
    private final String FOLDER_NAME = "CortriumC3Data";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private File[] files;
    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            int itemPosition = mRecyclerView.getChildLayoutPosition(view);
            //String item = mList.get(itemPosition);
            Log.d(TAG,itemPosition+"");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recordings, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //String myDataset[] = {"One", "Two", "Three"};
        files = getRecordingsFromInternalStorage();

        mAdapter = new MyAdapter(files, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }

    public OnClickListener getOnClickListener(){
        return listener;
    }

    public File[] getRecordingsFromInternalStorage(){
        File mainDirectory = new File(getContext().getExternalFilesDir(null)+File.separator+FOLDER_NAME);
        File[] directories = mainDirectory.listFiles();
        File[] files = new File[directories.length];
        for(int i=0;i<directories.length;i++){
            files[i] = directories[i].listFiles()[0];
        }
        return files;
    }


}
