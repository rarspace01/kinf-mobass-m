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

	private PendingIntent pIntent;
	private MapActivity   mapActivity;
	private GeoPoint      proximityPoint;

	private CircleOverlay circleOverlay;

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

		Log.i("Proximity Alert", "Set up ProximityAlert");
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
	}

	public void registerReceiver() {
		Log.i("Proximity Alert", "ProxAlert registered");

		mapActivity.getLocationManager().removeProximityAlert(pIntent);
		mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
				proximityPoint.getLongitudeE6() / 1e6, radius, -1, pIntent);
	}

	public void unregisterReceiver() {
		Log.i("Proximity Alert", "ProxAlert unregistered");
		mapActivity.getLocationManager().removeProximityAlert(pIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (context instanceof MapActivity) {
			Log.i("Proximity Alert", "cought application context");
			if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
				mapActivity.getLocationManager().removeProximityAlert(pIntent);
				Toast.makeText(context, "You are here!", Toast.LENGTH_LONG).show();
				Game.getInstance().guessedLocationApproached();
				mapActivity.getNewGuessPoints();
			} else {
				Toast.makeText(context, "Where are you going?", Toast.LENGTH_LONG).show();
			}
		} else {
			Log.i("Proximity Alert", "other context than application context");

		}
	}

	public void removeProximityPoint() {
		mapActivity.getLocationManager().removeProximityAlert(pIntent);
		mapActivity.getMapView().getOverlays().remove(circleOverlay);
	}
}
