package maximsblog.blogspot.com.telegue;

import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class Virt2Real implements Callback {
	
	public static final String CONNECT="connect";
	public static final String DISCONNECT="disconnect";
	public static final String ERROR="error";
	public static final String STATUS="status";
	
	public long mTimeout = 6000;
	public  Handler mTimer;

	private WebSocketConnection mWebSocketConnection = new WebSocketConnection();
	private Context mContext;
	private Runnable mConnectionLostTask = new TimerTask() {
		
		@Override
		public void run() {
			Intent intent = new Intent();
			intent.setAction(DISCONNECT);
			intent.putExtra(DISCONNECT, "connection timeout");
			mContext.sendBroadcast(intent);
		}
	};
	
	
	public Virt2Real(Context c){
		mContext = c;
		mTimer = new Handler(this);
	}
	
	public void connect(String mWebSocketConnectionuri, long timeout){
		try {
			mTimeout = timeout;
			mTimer.postDelayed(mConnectionLostTask, mTimeout);
			mWebSocketConnection.connect(mWebSocketConnectionuri, new WebSocketHandler() {

				@Override
				public void onOpen() {
					Intent intent = new Intent();
					intent.setAction(CONNECT);
					mContext.sendBroadcast(intent);
				}

				@Override
				public void onTextMessage(String payload) {
					Intent intent = new Intent();
					intent.setAction(STATUS);
					intent.putExtra(STATUS, payload);
					mContext.sendBroadcast(intent);
					mTimer.removeCallbacks(mConnectionLostTask);
					mTimer.postDelayed(mConnectionLostTask, mTimeout);
				}

				@Override
				public void onClose(int code, String reason) {
					Intent intent = new Intent();
					intent.setAction(DISCONNECT);
					intent.putExtra(DISCONNECT, reason);
					mContext.sendBroadcast(intent);
				}
				
			});
		} catch (WebSocketException e) {
			Intent intent = new Intent();
			intent.setAction(ERROR);
			mContext.sendBroadcast(intent);
		}
	}
	public void disconnect(){
		mTimer.removeCallbacks(mConnectionLostTask);
		mWebSocketConnection.disconnect();
	}
	public boolean isConnected(){
		return mWebSocketConnection.isConnected();
	}
	public void sendTextMessage(String msg){
		 mWebSocketConnection.sendTextMessage(msg);
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
}