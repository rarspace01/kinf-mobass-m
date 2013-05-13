package edu.denishamann.guesstimate;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service {

	private Timer timer = new Timer();
	private int ticker = 0;

	private final IBinder mBinder = new TimerBinder();

	public void onCreate() {
		super.onCreate();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				ticker++;
				Log.v("timer", "" + ticker);
			}
		}, 10, 1000);
	}

	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class TimerBinder extends Binder {
		BackgroundService getService() {
			return BackgroundService.this;
		}
	}

	public int getValue() {
		return this.ticker;
	}

}
