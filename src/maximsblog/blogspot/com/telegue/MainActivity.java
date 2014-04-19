package maximsblog.blogspot.com.telegue;

import maximsblog.blogspot.com.telegue.TrackJoystickView.OnTrackJoystickViewMoveListener;


import org.json.JSONException;
import org.json.JSONObject;

import com.gstreamer.GStreamer;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTrackJoystickViewMoveListener, SurfaceHolder.Callback {

	String mWSuri;
	private Virt2Real mVirt2real;
	private IntentFilter mIntentFilter;
	private long mTimeout;
	// gstreamer
	private native void nativeInit();     // Initialize native code, build pipeline, etc
    private native void nativeFinalize(); // Destroy pipeline and shutdown native code
    private native void nativePlay();     // Set pipeline to PLAYING
    private native void nativePause();    // Set pipeline to PAUSED
    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
    private native void nativeSurfaceInit(Object surface);
    private native void nativeSurfaceFinalize();
    private long native_custom_data;      // Native code will use this to keep private data
    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING
	

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("telegue");
        nativeClassInit();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); 
            return;
        }
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("");
		SurfaceView sv = (SurfaceView) this.findViewById(R.id.surface_video);
        SurfaceHolder sh = sv.getHolder();
        sh.addCallback(this);
        mVirt2real = App.virt2real;
        // init video receive
        is_playing_desired = mVirt2real.isConnected();    
        nativeInit();
        // init controls
		TrackJoystickView joystick = (TrackJoystickView) findViewById(R.id.joystickView);
		joystick.setOnTrackJoystickViewMoveListener(this, TrackJoystickView.DEFAULT_LOOP_INTERVAL);
		// set receive msg from virt2real
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Virt2Real.CONNECT);
		mIntentFilter.addAction(Virt2Real.DISCONNECT);
		mIntentFilter.addAction(Virt2Real.ERROR);
		mIntentFilter.addAction(Virt2Real.STATUS);
		registerReceiver(mIntentReceiver, mIntentFilter);
		// get saved settings
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
				nativePlay();
			}
			if (m.equals(Virt2Real.DISCONNECT)) {
				invalidateOptionsMenu();
				getActionBar().setTitle("");
				String s = intent.getStringExtra(Virt2Real.DISCONNECT);
				if(s!=null)
					Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
				nativePause();
			}
			if (m.equals(Virt2Real.ERROR)) {
				invalidateOptionsMenu();
				getActionBar().setTitle("");
				Toast.makeText(MainActivity.this, "connection error", Toast.LENGTH_SHORT).show();
				nativePause();
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

    protected void onDestroy() {
        nativeFinalize();
        super.onDestroy();
    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
    	final Activity cur = this;
    	runOnUiThread (new Runnable() {
    		public void run() {
    			Log.i ("GStreamer", "message:" + message);
    		}
    	});
    }

    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized () {
        Log.i ("GStreamer", "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        Log.d("GStreamer", "Surface changed to format " + format + " width "
                + width + " height " + height);
        nativeSurfaceInit (holder.getSurface());
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface created: " + holder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface destroyed");
        nativeSurfaceFinalize ();
    }
	
}
