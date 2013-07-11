package edu.denishamann.guesstimate;

import org.osmdroid.util.GeoPoint;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import edu.denishamann.guesstimate.activitys.MapActivity;
import edu.denishamann.guesstimate.model.Game;

public class ProximityAlert extends BroadcastReceiver {

	private static final String TAG = "Proximity Alert";
	
	private PendingIntent pIntent;
	private MapActivity   mapActivity;
	private GeoPoint      proximityPoint;

	private CircleOverlay circleOverlay;
	
	private long roundStartWalkTime_;

	private float radius = Game.getInstance().getAlertRadius();

	public ProximityAlert() {
	}

	public ProximityAlert(MapActivity mActivity) {
		mapActivity = mActivity;

		pIntent = PendingIntent.getBroadcast(mapActivity.getApplicationContext(), 0, new Intent("edu.denishamann.guesstimate.PROXIMITYALERT"), PendingIntent.FLAG_ONE_SHOT);
	}

	public ProximityAlert(MapActivity mActivity, GeoPoint pPoint) {
		mapActivity = mActivity;
		proximityPoint = pPoint;

		registerReceiver();

		circleOverlay = new CircleOverlay(mapActivity.getApplicationContext(), proximityPoint, radius, 155, true);
		mapActivity.getMapView().getOverlays().add(circleOverlay);

		this.roundStartWalkTime_= System.currentTimeMillis();
		
		Log.i(TAG, "Set up ProximityAlert");
	}

	public void setProximityPoint(GeoPoint pPoint) {
		proximityPoint = pPoint;

		if (circleOverlay != null) {
			mapActivity.getMapView().getOverlays().remove(circleOverlay);
		}

		circleOverlay = new CircleOverlay(mapActivity, proximityPoint, radius, 155, true);
		mapActivity.getMapView().getOverlays().add(circleOverlay);

		mapActivity.getLocationManager().removeProximityAlert(pIntent);
		mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
				proximityPoint.getLongitudeE6() / 1e6, radius, -1, pIntent);
		
		this.roundStartWalkTime_= System.currentTimeMillis();
	}

	public void registerReceiver() {
		Log.i(TAG, "ProxAlert registered");

		mapActivity.getLocationManager().removeProximityAlert(pIntent);
		mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
				proximityPoint.getLongitudeE6() / 1e6, radius, -1, pIntent);
	}

	public void unregisterReceiver() {
		Log.i(TAG, "ProxAlert unregistered");
		mapActivity.getLocationManager().removeProximityAlert(pIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (context instanceof MapActivity) {
			Log.i(TAG, "cought application context");
			if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
				Log.i(TAG, "Location Approached via Proximity Alert");
				
				if((Game.getInstance().getRoundStartWalkTime()+1000)>(this.roundStartWalkTime_)){
					mapActivity.getLocationManager().removeProximityAlert(pIntent);
					Toast.makeText(context, "You are here!", Toast.LENGTH_LONG).show();
					Game.getInstance().guessedLocationApproached();
					mapActivity.getNewGuessPoints();
				}else{
					Log.i(TAG, "Proximity Alert already handled. Cleaning up...");
					removeProximityPoint();
				}
			} else {
				Toast.makeText(context, "Where are you going?", Toast.LENGTH_LONG).show();
			}
		} else {
			Log.i(TAG, "other context than application context");

		}
	}

	public void removeProximityPoint() {
		mapActivity.getLocationManager().removeProximityAlert(pIntent);
		mapActivity.getMapView().getOverlays().remove(circleOverlay);
	}
}
