package edu.denishamann.guesstimate;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityAlert extends BroadcastReceiver {

	private static final String			PROXIMITY_ALERT	= "edu.denishamann.guesstimate.PROXIMITYALERT";
	private static final IntentFilter	iFilter			= new IntentFilter(PROXIMITY_ALERT);

	private PendingIntent				pIntent;
	private MapActivity					mapActivity;
	private GeoPoint					proximityPoint;

	public boolean						isRegistered	= false;

	public ProximityAlert() {
	}

	public ProximityAlert(MapActivity mActivity, GeoPoint pPoint) {
		mapActivity = mActivity;
		proximityPoint = pPoint;

		Intent proxIntent = new Intent(PROXIMITY_ALERT);
		pIntent = PendingIntent.getBroadcast(mapActivity, 0, proxIntent, PendingIntent.FLAG_ONE_SHOT); // TODO: POI-ID statt 0?

		registerReceiver();

		mapActivity.getOverlayItems().add(
				new OverlayItem("ProxAlertPoint", "ProxAlertPoint Decsription", proximityPoint));

		Log.i("GM", "Set up ProximityAlert");
	}

	public void setProximityPoint(GeoPoint pPoint) {

	}

	public void registerReceiver() {
		Log.i("GM", "ProxAlert registered");
		mapActivity.registerReceiver(this, iFilter);
		mapActivity.getLocationManager().addProximityAlert(proximityPoint.getLatitudeE6() / 1e6,
				proximityPoint.getLongitudeE6() / 1e6, 10, -1, pIntent);
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
