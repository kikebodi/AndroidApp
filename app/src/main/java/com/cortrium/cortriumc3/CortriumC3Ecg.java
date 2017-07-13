package com.cortrium.cortriumc3;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.cortrium.opkit.ConnectionManager;
import com.cortrium.opkit.CortriumC3;
import com.cortrium.opkit.datapackages.EcgData;
import com.cortrium.opkit.datatypes.SensorMode;

import butterknife.ButterKnife;

public class CortriumC3Ecg extends BaseActivity
{
	private final static String  TAG   = "CortriumC3Ecg";
	private              boolean DEBUG = BuildConfig.DEBUG;

	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{

			final String action = intent.getAction();
			if (ConnectionManager.ACTION_GATT_CONNECTED.equals(action))
			{
				mCortriumC3Device = mConnectionManager.getConnectedDevice();
				invalidateOptionsMenu();
				//updateConnectionState();
			}
			else if (ConnectionManager.ACTION_GATT_DISCONNECTED.equals(action))
			{
				invalidateOptionsMenu();
				//updateConnectionState();
				mHandler.sendEmptyMessage(MSG_CLEAR_UI);
			}
			else if (ConnectionManager.ACTION_DEVICE_MODE_UPDATED.equals(action))
			{
				SensorMode mode = intent.getParcelableExtra(ConnectionManager.EXTRA_VALUE);
				// If a disconnect was requested, disconnect the C3 peripheral
				if (mode.getMode() == (byte)CortriumC3.DeviceModes.DeviceModeDisconnect.ordinal() ||
					mode.getMode() == (byte)CortriumC3.DeviceModes.DeviceModeIdle.ordinal())
				{
					mConnectionManager.disconnectDevice();
				}
			}
		}
	};

	private final int MSG_CLEAR_UI                = 0;
	private final int MSG_UPDATE_CONNECTION_STATE = 1;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case MSG_CLEAR_UI:
				{
					getMainFragment().clearUI();
					break;
				}
				case MSG_UPDATE_CONNECTION_STATE:
				{
					//TODO:updateConnectionState();
					break;
				}
			}
		}
	};

	private CortriumC3 mCortriumC3Device;
	private ConnectionManager mConnectionManager;
	private DataLogger mDataLogger;

	// The ViewPager is responsible for sliding pages (fragments) in and out upon user input
	private ViewPager mViewPager;

	private EventBroadcastReceiver eventReceiver;

	// Titles of the individual pages (displayed in tabs)
	private final String[] PAGE_TITLES = new String[] {
			"Graph",
			"Page 2"
	};

	// The fragments that are used as the individual pages
	private final Fragment[] PAGES = new Fragment[] {
			new C3EcgFragment(),
			new RecordingsFragment()
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		IntentFilter intentFilter = new IntentFilter("com.cortrium.event");
		if (intentFilter != null)
		{
			eventReceiver = new EventBroadcastReceiver();
			registerReceiver(eventReceiver, intentFilter);
		}

		mConnectionManager = ConnectionManager.getInstance(this);
		mConnectionManager.setEcgDataListener(mListener);
		mCortriumC3Device = mConnectionManager.getConnectedDevice();

		mDataLogger = new DataLogger(this, mCortriumC3Device);

		//setContentView(R.layout.main_layout);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		// Set the Toolbar as the activity's app bar (instead of the default ActionBar)
		/*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);*/

		// Connect the ViewPager to our custom PagerAdapter. The PagerAdapter supplies the pages
		// (fragments) to the ViewPager, which the ViewPager needs to display.
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

		// Connect the tabs with the ViewPager (the setupWithViewPager method does this for us in
		// both directions, i.e. when a new tab is selected, the ViewPager switches to this page,
		// and when the ViewPager switches to a new page, the corresponding tab is selected)
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(mViewPager);
		/** **/

		/*FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
		main_fragment  = new C3EcgFragment();
		fragmentTransaction.replace(R.id.your_placeholder, main_fragment);
		fragmentTransaction.commit();*/
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (eventReceiver != null) {
			unregisterReceiver(eventReceiver);
		}

		mDataLogger.unregisterReceiver();

		mHandler.removeCallbacksAndMessages(null);

	}

	@Override
	public void onBackPressed(){
		if (mConnectionManager.getConnectionState() != ConnectionManager.ConnectionStates.Connected){
			super.onBackPressed();
		}
	}

	public C3EcgFragment getMainFragment(){
		return (C3EcgFragment) PAGES[0];
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.gatt_services, menu);
		if (mConnectionManager.getConnectionState() == ConnectionManager.ConnectionStates.Connected)
		{
			menu.findItem(R.id.menu_connect).setVisible(false);
			menu.findItem(R.id.menu_disconnect).setVisible(true);
		}
		else
		{
			menu.findItem(R.id.menu_connect).setVisible(true);
			menu.findItem(R.id.menu_disconnect).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_connect:
				mConnectionManager.connectDevice(mCortriumC3Device);
				return true;
			case R.id.menu_disconnect:
				showPopup(findViewById(R.id.menu_disconnect));
				return true;
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showPopup(View v)
	{
		PopupMenu popup = new PopupMenu(this, v);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.disconnect_menu, popup.getMenu());

		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId())
				{
					case R.id.disconnect_holter:
						mCortriumC3Device.changeMode(CortriumC3.DeviceModes.DeviceModeDisconnect);
						finish();
						break;
					case R.id.disconnect_poweroff:
						mCortriumC3Device.changeMode(CortriumC3.DeviceModes.DeviceModeIdle);
						finish();
						break;
				}
				//Unpair device.
				Utils.setPairedDevice(getBaseContext(),"");
				return true;
			}
		});
		popup.show();
	}

	private IntentFilter makeGattUpdateIntentFilter()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectionManager.ACTION_GATT_CONNECTED);
		intentFilter.addAction(ConnectionManager.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(ConnectionManager.ACTION_DEVICE_MODE_UPDATED);

		return intentFilter;
	}

	/*private void updateConnectionState()
	{
		if (mConnectionManager.getConnectionState() == ConnectionManager.ConnectionStates.Connected)
		{
			mConnectionState.setText(R.string.connected);
		}
		else {
			mConnectionState.setText(R.string.disconnected);
		}
	}*/

	private void onDeviceInformationUpdated(CortriumC3 device)
	{
		Log.d(TAG, getString(R.string.revision_format,
				device.getSoftwareRevision(),
				device.getFirmwareRevision(),
				device.getHardwareRevision()));
	}

	private void onModeRead(SensorMode sensorMode)
	{
		Log.e(TAG, String.format("%s - ECG1(%s), ECG2(%s), ECG3(%s)",
										  SensorMode.getModeName(sensorMode.getMode()),
										  sensorMode.isChannelEnabled(1),
										  sensorMode.isChannelEnabled(2),
										  sensorMode.isChannelEnabled(3)));
	}

	private void onEcgDataUpdated(EcgData ecgData)
	{
		//Log data (We keep the logger here).
		if (!ecgData.isFillerSamples()){
			mDataLogger.logBlePayload(ecgData.getRawBlePayload(), ecgData.getMiscInfo().getSerial());
		}
		getMainFragment().onEcgDataUpdated(ecgData);
	}

	private final ConnectionManager.EcgDataListener mListener = new ConnectionManager.EcgDataListener() {
		@Override
		public void ecgDataUpdated(final EcgData ecgData) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onEcgDataUpdated(ecgData);
				}
			});
		}

		@Override
		public void modeRead(final SensorMode mode) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onModeRead(mode);
				}
			});
		}

		@Override
		public void deviceInformationRead(final CortriumC3 device) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onDeviceInformationUpdated(device);
				}
			});
		}
	};

	/* PagerAdapter for supplying the ViewPager with the pages (fragments) to display. */
	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			return PAGES[position];
		}

		@Override
		public int getCount() {
			return PAGES.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return PAGE_TITLES[position];
		}

	}
}
