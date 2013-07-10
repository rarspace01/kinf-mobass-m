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

	public boolean isRegistered = false;

	private CircleOverlay circleOverlay;

	public ProximityAlert() {
	}

	public ProximityAlert(MapActivity mActivity) {
		mapActivity = mActivity;
	}

	public ProximityAlert(MapActivity mActivity, GeoPoint pPoint) {
		mapActivity = mActivity;
		proximityPoint = pPoint;

		registerReceiver();

		circleOverlay = new CircleOverlay(mapActivity, proximityPoint, Game.getInstance().getAlertRadius(), 155, true);
		mapActivity.getMapView().getOverlays().add(circleOverlay);

		Log.i("Proximity Alert", "Set up ProximityAlert");
	}

	public void setProximityPoint(GeoPoint pPoint) {
		proximityPoint = pPoint;

		if (circleOverlay != null) {
			mapActivity.getMapView().getOverlays().remove(circleOverlay);
		}

		circleOverlay = new CircleOverlay(mapActivity, proximityPoint, Game.getInstance().getAlertRadius(), 155, true);
		mapActivity.getMapView().getOverlays().add(circleOverlay);

		if (!isRegistered) {
			registerReceiver();
		} else {
			mapActivity.getLocationManager().removeProximityAlert(pIntent);
			mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
					proximityPoint.getLongitudeE6() / 1e6, Game.getInstance().getAlertRadius(), -1, pIntent);
		}
	}

	public void registerReceiver() {
		Log.i("Proximity Alert", "ProxAlert registered");

		pIntent = PendingIntent.getBroadcast(mapActivity, 0, new Intent("edu.denishamann.guesstimate.PROXIMITYALERT"), PendingIntent.FLAG_CANCEL_CURRENT);
		mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
				proximityPoint.getLongitudeE6() / 1e6, Game.getInstance().getAlertRadius(), -1, pIntent);

		isRegistered = true;
	}

	public void unregisterReceiver() {
		if (isRegistered) {
			Log.i("Proximity Alert", "ProxAlert unregistered");
			mapActivity.getLocationManager().removeProximityAlert(pIntent);
			isRegistered = false;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (context instanceof MapActivity) {
			Log.i("Proximity Alert", "cought application context");
			if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
				Toast.makeText(context, "You are here!", Toast.LENGTH_LONG).show();
				Game.getInstance().guessedLocationApproached();
				mapActivity.getNewGuessPoints();
			} else {
				Toast.makeText(context, "Where are you going?", Toast.LENGTH_LONG).show();
			}
			isRegistered = false;
		}else{
			Log.i("Proximity Alert", "other context than application context");
			
		}
	}

	public void hideProximityPoint() {
		mapActivity.getMapView().getOverlays().remove(circleOverlay);
	}
}
