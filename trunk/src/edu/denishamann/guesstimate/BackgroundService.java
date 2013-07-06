package edu.denishamann.guesstimate;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import edu.denishamann.guesstimate.database.SQLiteDatamanager;

/**
 * 
 * @author denis
 * 
 */
public class BackgroundService extends Service implements LocationListener {

	private int				ticker	= 0;
	private int				trackid	= 0;
	private SQLiteDatabase	dbConn;
	private String			sSQL	= "";
	private Location		lastKnownLocation;
	private LocationManager	lm;
	private String			locationProvider;

	private final IBinder	mBinder	= new BackgroundServiceBinder();

	public void onCreate() {
		super.onCreate();

		trackid = (int) (Math.random() * Integer.MAX_VALUE);

		//
		SQLiteDatamanager dbManager = new SQLiteDatamanager(this);
		dbConn = dbManager.getWritableDatabase();

		locationProvider = LocationManager.GPS_PROVIDER;
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		lastKnownLocation = lm.getLastKnownLocation(locationProvider);

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
	}

	public void onDestroy() {
		dbConn.close();
		Log.i("GM", "Destroyed Service");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class BackgroundServiceBinder extends Binder {
		BackgroundService getService() {
			return BackgroundService.this;
		}
	}

	public int getValue() {
		return this.ticker;
	}

	@Override
	public void onLocationChanged(Location location) {

		lastKnownLocation = lm.getLastKnownLocation(locationProvider);
		sSQL = "INSERT INTO logging (trackid,timestamp, lat, long) VALUES ('" + trackid + "','"
				+ ((int) (System.currentTimeMillis() / 1000)) + "','" + lastKnownLocation.getLatitude() + "','"
				+ lastKnownLocation.getLongitude() + "')";
		dbConn.execSQL(sSQL);

		Log.i("GM", "Wrote to SQL:[" + sSQL + "]");

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
