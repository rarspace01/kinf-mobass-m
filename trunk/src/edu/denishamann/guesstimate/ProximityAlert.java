package edu.denishamann.guesstimate;

import org.osmdroid.util.GeoPoint;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;
import edu.denishamann.guesstimate.activitys.MapActivity;

public class ProximityAlert extends BroadcastReceiver {

	private static final String			PROXIMITY_ALERT	= "edu.denishamann.guesstimate.PROXIMITYALERT";
	private static final IntentFilter	iFilter			= new IntentFilter(PROXIMITY_ALERT);

	private PendingIntent				pIntent;
	private MapActivity					mapActivity;
	private GeoPoint					proximityPoint;

	public boolean						isRegistered	= false;
	public boolean						isFired			= false;

	private CircleOverlay				circleOverlay;
	private int							radius			= 50;

	public ProximityAlert() {
	}

	public ProximityAlert(MapActivity mActivity) {
		mapActivity = mActivity;
	}

	public ProximityAlert(MapActivity mActivity, GeoPoint pPoint) {
		mapActivity = mActivity;
		proximityPoint = pPoint;

		registerReceiver();

		circleOverlay = new CircleOverlay(mapActivity, proximityPoint, mapActivity.getMapView());
		circleOverlay.setRadius(radius);
		mapActivity.getMapView().getOverlays().add(circleOverlay);

		Log.i("GM", "Set up ProximityAlert");
	}

	public void setProximityPoint(GeoPoint pPoint) {
		proximityPoint = pPoint;

		if (circleOverlay != null) {
			mapActivity.getMapView().getOverlays().remove(circleOverlay);
		}

		circleOverlay = new CircleOverlay(mapActivity, proximityPoint, mapActivity.getMapView());
		circleOverlay.setRadius(radius);
		mapActivity.getMapView().getOverlays().add(circleOverlay);

		if (!isRegistered) {
			registerReceiver();
		} else {
			mapActivity.getLocationManager().removeProximityAlert(pIntent);
			mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
					proximityPoint.getLongitudeE6() / 1e6, radius, -1, pIntent);
		}

		isFired = false;
	}

	public void registerReceiver() {
		Log.i("GM", "ProxAlert registered");

		pIntent = PendingIntent.getBroadcast(mapActivity, 0, new Intent(PROXIMITY_ALERT), PendingIntent.FLAG_ONE_SHOT);
		mapActivity.registerReceiver(this, iFilter);
		mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
				proximityPoint.getLongitudeE6() / 1e6, radius, -1, pIntent);

		isRegistered = true;
	}

	public void unregisterReceiver() {
		Log.i("GM", "ProxAlert unregistered");
		mapActivity.unregisterReceiver(this);
		mapActivity.getLocationManager().removeProximityAlert(pIntent);
		isRegistered = false;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!isFired) {
			isFired = true;

			String info = "ProximityAlert: ";
			if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
				Toast.makeText(context, "You are here!", Toast.LENGTH_LONG).show();
				info += "incoming";
			} else {
				Toast.makeText(context, "Where are you going?", Toast.LENGTH_LONG).show();
				info += "outgoing";
			}
			Log.i("GM", info);
		}
	}
}
