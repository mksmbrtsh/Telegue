package maximsblog.blogspot.com.telegue;

import maximsblog.blogspot.com.telegue.TrackJoystickView.OnTrackJoystickViewMoveListener;


import org.json.JSONException;
import org.json.JSONObject;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTrackJoystickViewMoveListener {

	String mWSuri;
	private Virt2Real mVirt2real;
	private IntentFilter mIntentFilter;
	private long mTimeout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		mVirt2real = App.virt2real;
		TrackJoystickView joystick = (TrackJoystickView) findViewById(R.id.joystickView);
		joystick.setOnTrackJoystickViewMoveListener(this, TrackJoystickView.DEFAULT_LOOP_INTERVAL);

		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Virt2Real.CONNECT);
		mIntentFilter.addAction(Virt2Real.DISCONNECT);
		mIntentFilter.addAction(Virt2Real.ERROR);
		mIntentFilter.addAction(Virt2Real.STATUS);
		registerReceiver(mIntentReceiver, mIntentFilter);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mWSuri = "ws://" + prefs.getString("ip", "192.168.1.128") + ":" + prefs.getString("port", "1083");
		mTimeout = Long.parseLong(prefs.getString("timeout", "6000"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (mVirt2real.isConnected())
			menu.findItem(R.id.item1).setTitle("disconnect");
		else
			menu.findItem(R.id.item1).setTitle("connect");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.item1) {
			if (mVirt2real.isConnected()) {
				mVirt2real.disconnect();
				getActionBar().setTitle("");
				invalidateOptionsMenu();
			} else {
				mVirt2real.connect(mWSuri, mTimeout);
			}
		} else if (item.getItemId() == R.id.action_settings){
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent,1);
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mWSuri = "ws://" + prefs.getString("ip", "192.168.1.128") + ":" + prefs.getString("port", "1083");
		mTimeout = Long.parseLong(prefs.getString("timeout", "6000"));
		if (mVirt2real.isConnected()) {
			mVirt2real.disconnect();
			getActionBar().setTitle("");
			invalidateOptionsMenu();
		}
	};

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String m = intent.getAction();
			if (m.equals(Virt2Real.CONNECT)) {
				invalidateOptionsMenu();
				getActionBar().setTitle("");
			}
			if (m.equals(Virt2Real.DISCONNECT)) {
				invalidateOptionsMenu();
				getActionBar().setTitle("");
				String s = intent.getStringExtra(Virt2Real.DISCONNECT);
				if(s!=null)
					Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
			}
			if (m.equals(Virt2Real.ERROR)) {
				invalidateOptionsMenu();
				getActionBar().setTitle("");
				Toast.makeText(MainActivity.this, "connection error", Toast.LENGTH_SHORT).show();
			}
			if (m.equals(Virt2Real.STATUS)) {
				JSONObject j;
				try {
					j = new JSONObject(intent.getStringExtra(Virt2Real.STATUS));
					String vol = (String) j.get("vol");
					String lev = (String) j.get("lev");
					String lnk = (String) j.get("lnk");
					getActionBar().setTitle("70/"+lnk+" " +lev +"db"+" v:"+ vol);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
	};

	@Override
	public void onValueChanged(int y1, int y2) {

				JSONObject j = new JSONObject();
				try {
					j.put("cmd", "drive");
					j.put("v1", 254 * (y1 + 100) / 200);
					j.put("v2", 254 * (y2 + 100) / 200);
					String payload = j.toString();
					if (mVirt2real.isConnected())
						mVirt2real.sendTextMessage(payload);
				} catch (JSONException e) {
					e.printStackTrace();
				}
	}

}
