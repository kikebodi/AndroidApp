package com.cortrium.cortriumc3.RecordingsTab;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cortrium.cortriumc3.ApiConnection.ApiConnectionManager;
import com.cortrium.cortriumc3.C3EcgActivity;
import com.cortrium.cortriumc3.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/* Fragment used as page 2 */
public class RecordingsFragment extends Fragment {

    @BindView(R.id.recordings_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.floatingActionButtonDisconnect) FloatingActionButton disconnectFab;
    private final String TAG = RecordingsFragment.class.getName();
    private Unbinder unbinder;
    private final String FOLDER_NAME = "CortriumC3Data";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<File> files;
    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            int itemPosition = mRecyclerView.getChildLayoutPosition(view);
            int id = view.getId();
            Log.d(TAG,itemPosition+"");
        }
    };
    private OnClickListener deleteListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            int itemPosition = mRecyclerView.getChildLayoutPosition(view);
            File fileToDelete = files.get(itemPosition).getParentFile();
            if(deleteRecursive(fileToDelete)){
                //https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data
                files.remove(itemPosition);
                mLayoutManager.removeViewAt(itemPosition);
                mAdapter.notifyItemRemoved(itemPosition);
                mAdapter.notifyItemRangeChanged(itemPosition,files.size());
                Log.d(TAG,"Successfully deleted");
            }else{
                Log.d(TAG,"Error deleting file");
            }
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
        files = getRecordingsFromInternalStorage();

        mAdapter = new MyAdapter(files, this, new CustomItemClickListener() {
            @Override
            public void deleteItem(int pos) {
                Log.d(TAG, "delete "+pos);
                deleteFile(pos);
            }

            @Override
            public void uploadItem(int pos) {
                Log.d(TAG, "upload "+pos);
                ApiConnectionManager connector = new ApiConnectionManager(getContext().getResources().getString(R.string.api_url));
                connector.uploadSavedRecording(files.get(pos));
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0 ){
                    if(disconnectFab.isShown()) disconnectFab.hide();
                } else {
                    if(!disconnectFab.isShown()) disconnectFab.show();
                }
            }
        });

        disconnectFab.setOnClickListener(((C3EcgActivity)getActivity()).getFabOnCLickListener());
    }

    public void setFabOnClickListener(OnClickListener listener){
        disconnectFab.setOnClickListener(listener);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }

    public OnClickListener getOnClickListener(){
        return listener;
    }

    public OnClickListener getOnDeleteClickListener(){
        return deleteListener;
    }

    public List<File> getRecordingsFromInternalStorage(){
        File mainDirectory = new File(getContext().getExternalFilesDir(null)+File.separator+FOLDER_NAME);
        // Check if exists
        if(mainDirectory.isDirectory()){
            File[] directories = mainDirectory.listFiles();
            List<File> files = new ArrayList<>();
            for(int i=0;i<directories.length;i++){
                if(directories[i].listFiles().length > 0){
                    files.add(directories[i].listFiles()[0]);
                }
            }
            return files;
        }else {
            List<File> emptyList = new ArrayList<>();
            return emptyList;
        }
    }

    public void deleteFile(int position){
        File fileToDelete = files.get(position).getParentFile();
        if(deleteRecursive(fileToDelete)){
            //https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data
            files.remove(position);
            mLayoutManager.removeViewAt(position);
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position,files.size());
            Log.d(TAG,"Successfully deleted");
        }else{
            Log.d(TAG,"Error deleting file");
        }
    }

    //https://stackoverflow.com/questions/13410949/how-to-delete-folder-from-internal-storage-in-android
    public boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        return fileOrDirectory.delete();
    }
}
