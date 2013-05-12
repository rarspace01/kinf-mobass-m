package edu.denishamann.guesstimate;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.util.constants.MapViewConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapActivity extends Activity implements LocationListener, MapViewConstants {

	private MapController					mapController;
	private MapView							mapView;
	private ItemizedOverlay<OverlayItem>	mMyItemsOverlay;
	private LocationManager					lm;
	private ArrayList<OverlayItem>			items;
	private GeoPoint						curLoc;
	private ProximityAlert					proximityAlert;

	private ResourceProxy					mResourceProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// get Intent Data if available

		Log.i("GM", "pre intent test");

		Intent intent = getIntent();

		Log.i("GM", "after intent test");

		String intentData = "";

		if (intent != null) {
			intentData = intent.getDataString();
		}
		GeoPoint intentPoint = null;
		/*
		 * regex check:
		 * ^geo:(([\d]{1,3})|([\d]{1,3}[.][\d]*))[,](([\d]{1,3})|([\
		 * d]{1,3}[.][\d]*))$
		 */
		if (intentData != null) {
			if (intentData.matches("^geo:(([\\d]{1,3})|([\\d]{1,3}[.][\\d]*))[,](([\\d]{1,3})|([\\d]{1,3}[.][\\d]*))$")) {
				Log.i("GM", "Intent data: " + intentData);

				intentData = intentData.substring(intentData.indexOf("geo:") + "geo:".length());

				Log.i("GM", "Intent data2: " + intentData);

				intentData.substring(0, intentData.indexOf(","));
				intentData.substring(intentData.indexOf(",") + 1);

				intentPoint = new GeoPoint(Double.parseDouble(intentData.substring(0, intentData.indexOf(","))),
						Double.parseDouble(intentData.substring(intentData.indexOf(",") + 1)));
			}
		}

		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

		mapView.setMultiTouchControls(true);
		mapView.setBuiltInZoomControls(true);
		// mapView.set

		mapController = mapView.getController();

		// get current location

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// refresh location every 10sec or 100meter if we move
		// lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 0,
		// this);
		// lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,
		// this);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		//		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(crit, true);

		Log.i("GM", provider);

		Location loc = lm.getLastKnownLocation(provider);
		GeoPoint currentPosition = null;

		if (loc != null) {
			Log.i("GM", "Current Location: " + loc.getLatitude() + " - " + loc.getLongitude());
			currentPosition = new GeoPoint(loc.getLatitude(), loc.getLongitude());
		} else {
			Log.i("GM", "Loc == null, using default value");
			currentPosition = new GeoPoint(49.903038, 10.869427);
		}

		mapController.setZoom(15);
		if (intentPoint != null) {
			mapController.setCenter(intentPoint);
		} else {
			mapController.setCenter(currentPosition);
		}

		// create overlay icon

		items = new ArrayList<OverlayItem>();

		GeoPoint erbaInsel = new GeoPoint(49903259, 10869727);

		curLoc = new GeoPoint(currentPosition);
		OverlayItem curLocItem = new OverlayItem("ProxAlertPoint", "ProxAlertPoint Decsription", curLoc);
		Drawable newMarker = getResources().getDrawable(R.drawable.curloc);
		curLocItem.setMarker(newMarker);

		items.add(new OverlayItem("Erba Insel", "Erba Insel Descr", erbaInsel));
		items.add(curLocItem);

		proximityAlert = new ProximityAlert(this, new GeoPoint(49907635, 10901713));

		/* OnTapListener for the Markers, shows a simple Toast. */
		mMyItemsOverlay = new ItemizedIconOverlay<OverlayItem>(items,
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						Toast.makeText(MapActivity.this, "Item '" + item.mTitle + "'", Toast.LENGTH_LONG).show();
						Log.i("GM", "Pressed Icon");
						return true; // We 'handled' this event.
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						Toast.makeText(MapActivity.this, item.mDescription, Toast.LENGTH_LONG).show();
						Log.i("GM", "Long Pressed Icon");
						return false;
					}
				}, mResourceProxy);

		mapView.getOverlays().add(mMyItemsOverlay);
		mapView.invalidate();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Log.i("GM", "Menu item: " + item.getTitle());

		if (item.getTitle().toString().contains(getString(R.string.show_gps))) {
			Log.i("GM", "Got correct item");

			Intent myIntent = new Intent(MapActivity.this, ShowGPSActivity.class);
			// myIntent.putExtra("key", value); //Optional parameters
			MapActivity.this.startActivity(myIntent);

		}

		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		Log.i("GM", "Location updated & redraw to " + lat + ":" + lng);

		curLoc.setCoordsE6(lat, lng);

		mapView.invalidate();
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

	protected void onPause() {
		Log.i("GM", "GM paused");
		proximityAlert.unregisterReceiver();

		super.onPause();
	}

	protected void onResume() {
		Log.i("GM", "GM resumed");
		if (!proximityAlert.isRegistered)
			proximityAlert.registerReceiver();

		super.onResume();
	}

	public LocationManager getLocationManager() {
		return lm;
	}

	public ArrayList<OverlayItem> getOverlayItems() {
		return items;
	}
}
