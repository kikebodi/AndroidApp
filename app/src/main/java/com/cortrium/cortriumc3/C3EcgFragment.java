package com.cortrium.cortriumc3;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.cortrium.cortriumc3.ApiConnection.models.Recordings;
import com.cortrium.opkit.CortriumC3;
import com.cortrium.opkit.Utils;
import com.cortrium.opkit.datapackages.EcgData;
import com.cortrium.opkit.datatypes.ECGSamples;
import com.cortrium.opkit.datatypes.Event;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class C3EcgFragment extends Fragment {

    @BindView(R.id.device_bodyposition) TextView mDeviceBodyPosition;
    @BindView(R.id.temperature_ambient) TextView mAmbientTemperature;
    @BindView(R.id.heart_rate) TextView mHeartRate;
    @BindView(R.id.respiration_rate) TextView mRespirationRate;
    @BindView(R.id.ecg1_plot) XYPlot mEcg1Plot;
    @BindView(R.id.ecg2_plot) XYPlot   mEcg2Plot;
    @BindView(R.id.ecg3_plot) XYPlot   mEcg3Plot;
    @BindView(R.id.respiration_plot) XYPlot   mRespirationPlot;
    @BindView(R.id.main_layout_fragment) ConstraintLayout mConstraintLayout;
    @BindView(R.id.floatingActionButton)  FloatingActionButton fab;

    private final static String  TAG   = "C3EcgFragment";
    private Unbinder unbinder;
    private final int MAX_DATA_COUNT = 250 * 6;
    private boolean isUIready = false;

    private SimpleXYSeries mEcg1Series        = null;
    private SimpleXYSeries        mEcg2Series        = null;
    private SimpleXYSeries        mEcg3Series        = null;
    private SimpleXYSeries        mRespirationSeries = null;
    private LineAndPointFormatter mPlotFormatter     = null;

    public Snackbar mySnackbar = null;
    private boolean shownTools = false;
    private List<Event> eventList = new ArrayList<>();

    @OnClick(R.id.main_layout_fragment) void screenTouch(){

        if(mySnackbar == null) return;

        if(shownTools){
            hideTools();
        }else{
            showTools();
        }
    }

    @OnFocusChange(R.id.main_layout_fragment) void loseFocus(){
        Log.d(TAG,"Change focus");
    }

    @OnClick(R.id.floatingActionButton) void addEvent(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Write event info");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Add event
                eventList.add(new Event(input.getText().toString()));
                screenTouch();
                Toast.makeText(getContext(),"Event saved",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Event saved");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //screenTouch();
            }
        });

        builder.show();
    }

    public C3EcgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c3_ecg, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        configureGraphs();
        isUIready = true;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
        isUIready = false;
    }

    public void configureGraphs() {
        //Configure graphs
        PixelUtils.init(getActivity());
        mPlotFormatter = new LineAndPointFormatter(Color.rgb(39, 204, 192), null, null, null);
        mPlotFormatter.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        mPlotFormatter.getLinePaint().setStrokeWidth(1);

        initialiseXYPlotWidget(mEcg1Plot);
        mEcg1Plot.setRangeBoundaries(-3000, BoundaryMode.FIXED, 3000, BoundaryMode.FIXED);

        initialiseXYPlotWidget(mEcg2Plot);
        mEcg2Plot.setRangeBoundaries(-3000, BoundaryMode.FIXED, 3000, BoundaryMode.FIXED);

        initialiseXYPlotWidget(mEcg3Plot);
        mEcg3Plot.setRangeBoundaries(-3000, BoundaryMode.FIXED, 3000, BoundaryMode.FIXED);

        initialiseXYPlotWidget(mRespirationPlot);
        mRespirationPlot.setRangeBoundaries(-2500, BoundaryMode.FIXED, 2500, BoundaryMode.FIXED);

        resetAllPlots();
    }

    private void initialiseXYPlotWidget(XYPlot ecgPlotWidget){
        ecgPlotWidget.getLayoutManager().remove(ecgPlotWidget.getDomainLabelWidget());
        ecgPlotWidget.getLayoutManager().remove(ecgPlotWidget.getRangeLabelWidget());
        ecgPlotWidget.getLayoutManager().remove(ecgPlotWidget.getTitleWidget());
        ecgPlotWidget.getLayoutManager().remove(ecgPlotWidget.getLegendWidget());

        ecgPlotWidget.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        ecgPlotWidget.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
        ecgPlotWidget.getGraphWidget().getDomainGridLinePaint().setColor(Color.TRANSPARENT);

        ecgPlotWidget.setRangeBoundaries(CortriumC3.SAMPLE_MIN_VALUE, BoundaryMode.FIXED, CortriumC3.SAMPLE_MAX_VALUE, BoundaryMode.FIXED);
        ecgPlotWidget.setDomainBoundaries(0, MAX_DATA_COUNT, BoundaryMode.FIXED);
    }

    private void resetAllPlots()
    {
        if (mEcg1Series != null)
        {
            mEcg1Plot.removeSeries(mEcg1Series);
            mEcg1Plot.redraw();
        }
        mEcg1Series = new SimpleXYSeries(getString(R.string.label_ecg1));
        mEcg1Series.useImplicitXVals();
        mEcg1Plot.addSeries(mEcg1Series, mPlotFormatter);

        if (mEcg2Series != null)
        {
            mEcg2Plot.removeSeries(mEcg2Series);
            mEcg2Plot.redraw();
        }
        mEcg2Series = new SimpleXYSeries(getString(R.string.label_ecg2));
        mEcg2Series.useImplicitXVals();
        mEcg2Plot.addSeries(mEcg2Series, mPlotFormatter);

        if (mEcg3Series != null)
        {
            mEcg3Plot.removeSeries(mEcg3Series);
            mEcg3Plot.redraw();
        }
        mEcg3Series = new SimpleXYSeries(getString(R.string.label_ecg3));
        mEcg3Series.useImplicitXVals();
        mEcg3Plot.addSeries(mEcg3Series, mPlotFormatter);

        if (mRespirationSeries != null)
        {
            mRespirationPlot.removeSeries(mRespirationSeries);
            mRespirationPlot.redraw();
        }
        mRespirationSeries = new SimpleXYSeries(getString(R.string.label_resp));
        mRespirationSeries.useImplicitXVals();
        mRespirationPlot.addSeries(mRespirationSeries, mPlotFormatter);
    }

    public void clearUI()
    {
        mDeviceBodyPosition.setText(null);
        //mSensorMode.setText(null);
        mAmbientTemperature.setText(null);
        //mObjectTemperature.setText(null);
        //mAccelerometer.setText(null);
        mHeartRate.setText(null);
        mRespirationRate.setText(null);
        //mBatteryLevel.setText(null);
        resetAllPlots();
    }

    public void onEcgDataUpdated(EcgData ecgData)
    {
        if(isUIready){
            mDeviceBodyPosition.setText(ecgData.getMiscInfo().getDeviceOrientationAsString());

            String tempString = String.format("%.02f", Utils.convertKelvinToCelcius(ecgData.getMiscInfo().getTemperature()));
            if (ecgData.getMiscInfo().isAmbientTemperature()) {
                mAmbientTemperature.setText(tempString);
            } /*else {
                mObjectTemperature.setText(tempString);
            }*/

            mHeartRate.setText(String.valueOf(ecgData.getHeartRate()));
            mRespirationRate.setText(String.valueOf(ecgData.getRespiratoryRate()));
            //mBatteryLevel.setText(String.format("%d%%", ecgData.getMiscInfo().getBatteryLevel()));

            if (!ecgData.isFillerSamples()) {
                // Add ECG lead values to the plot.
                plotEcgValues(mEcg1Series, ecgData.getFilteredEcg1Samples());
                plotEcgValues(mEcg2Series, ecgData.getFilteredEcg2Samples());
                plotEcgValues(mEcg3Series, ecgData.getFilteredEcg3Samples());
            }

            // Add Respiration value to the plot.
            for (int index = 0; index < ECGSamples.NUMBER_OF_SAMPLES; index++) {
                if (mRespirationSeries.size() == MAX_DATA_COUNT)
                    mRespirationSeries.removeFirst();
                mRespirationSeries.addLast(null, ecgData.getFilteredRespirationSample());
            }

            mEcg1Plot.redraw();
            mEcg2Plot.redraw();
            mEcg3Plot.redraw();
            mRespirationPlot.redraw();
        }
    }

    private void plotEcgValues(SimpleXYSeries ecgSeries, int[] filteredEcgSamples)
    {
        for (int index = 0; index < ECGSamples.NUMBER_OF_SAMPLES; index++)
        {
            if (ecgSeries.size() == MAX_DATA_COUNT)
                ecgSeries.removeFirst();

            if (filteredEcgSamples != null)
            {
                ecgSeries.addLast(null, filteredEcgSamples[index]);
            }
            else
            {
                ecgSeries.addLast(null, null);
            }
        }
    }

    public void hideTools(){
        if(mySnackbar != null){
            mySnackbar.dismiss();
        }
        fab.hide();
        shownTools = false;
    }

    public void showTools(){
        mySnackbar.show();
        fab.show();
        shownTools = true;
    }

    public void setSnackbar(String filename, View.OnClickListener listener) {
        this.mySnackbar = Snackbar.make(mConstraintLayout, filename, Snackbar.LENGTH_INDEFINITE)
        .setAction("UPLOAD",listener);
    }

    public List<Event> getEventList() {
        return eventList;
    }
}
