package edu.denishamann.guesstimate.activitys;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.util.constants.MapViewConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.denishamann.guesstimate.OsmItemizedOverlay;
import edu.denishamann.guesstimate.ProximityAlert;
import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.model.Game;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;
import edu.denishamann.ors_api.Route;

/**
 * 
 * @author denis
 * 
 */
public class MapActivity extends Activity implements LocationListener, MapViewConstants {

	private MapController		mapController;
	private MapView				mapView;
	private OsmItemizedOverlay	itemizedOverlay;
	private LocationManager		lm;
	private GeoPoint			curLoc;
	private ProximityAlert		proximityAlert	= new ProximityAlert(this);
	private List<GuessPoint>	guessPoints;

	private ResourceProxy		mResourceProxy;

	private boolean				started			= false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (started) {
			return;
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		Intent intent = getIntent();
		String intentDataString = "";
		if (intent != null) {
			intentDataString = intent.getDataString();
		}

		GeoPoint intentPoint = null;
		if (intentDataString != null) {
			if (intentDataString
					.matches("^geo:(([\\d]{1,3})|([\\d]{1,3}[.][\\d]*))[,](([\\d]{1,3})|([\\d]{1,3}[.][\\d]*))$")) {
				Log.i("GM", "Intent data: " + intentDataString);

				intentDataString = intentDataString.substring(intentDataString.indexOf("geo:") + "geo:".length());

				Log.i("GM", "Intent data2: " + intentDataString);

				intentPoint = new GeoPoint(Double.parseDouble(intentDataString.substring(0,
						intentDataString.indexOf(","))), Double.parseDouble(intentDataString.substring(intentDataString
						.indexOf(",") + 1)));
			}
		}

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

		mapView.setMultiTouchControls(true);
		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();

		// start location updates
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
		}
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(crit, true);

		Log.i("GM", provider);

		Location loc = lm.getLastKnownLocation(provider);

		if (loc != null) {
			Log.i("GM", "Current Location: " + loc.getLatitude() + " - " + loc.getLongitude());
			curLoc = new GeoPoint(loc.getLatitude(), loc.getLongitude());
		} else {
			Log.i("GM", "Loc == null, using default value");
			curLoc = new GeoPoint(49.903038, 10.869427);
		}

		mapController.setZoom(15);
		if (intentPoint != null) {
			mapController.setCenter(intentPoint);
		} else {
			mapController.setCenter(curLoc);
		}

		guessPoints = Game.getUniqueInstance().getLocationsToBeGuessed(new GeoLocation(curLoc));

		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		itemizedOverlay = new OsmItemizedOverlay(new ArrayList<OverlayItem>(),
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						for (GuessPoint gp : guessPoints) {
							if (gp.getDescription_().equals(item.mTitle)) {
								inputDialog(gp);
							}
						}
						return true;
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						Toast.makeText(MapActivity.this, item.mDescription, Toast.LENGTH_LONG).show();
						return true;
					}
				}, mResourceProxy);

		if (Game.getUniqueInstance().getDifficulty_() == 0) {
			for (GuessPoint gp : guessPoints) {
				OverlayItem guessItem = new OverlayItem(gp.getDescription_(), gp.getDescription_(), gp.getLocation_()
						.toGeoPoint());
				itemizedOverlay.addItem(guessItem);
			}
		}

		Drawable newMarker = getResources().getDrawable(R.drawable.curloc);
		newMarker.setAlpha(155);
		OverlayItem curLocItem = new OverlayItem("ProxAlertPoint", "ProxAlertPoint Decsription", curLoc);
		curLocItem.setMarker(newMarker);
		curLocItem.setMarkerHotspot(HotspotPlace.CENTER);
		itemizedOverlay.addItem(curLocItem);

		mapView.getOverlays().add(itemizedOverlay);
		mapView.invalidate();

		started = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("GM", "Menu item: " + item.getTitle());

		if (item.getTitle().toString().contains(getString(R.string.enter_guesstimate))) {
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			String provider = lm.getBestProvider(crit, true);
			Location loc = lm.getLastKnownLocation(provider);
			drawRouteOnMap(new Route(new GeoLocation(49.90337, 10.894733), new GeoLocation(loc.getLatitude(),
					loc.getLongitude())));
			//inputDialog();
		}

		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		Log.i("GM", "Location updated & redraw to " + lat + ":" + lng + " | zoom: " + mapView.getZoomLevel());

		curLoc.setCoordsE6(lat, lng);

		mapView.invalidate();
	}

	public void goToLoc(View view) {
		mapController.animateTo(new GeoPoint(curLoc));
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

		// proximityAlert.unregisterReceiver();

		super.onPause();
	}

	protected void onResume() {
		Log.i("GM", "GM resumed");

		//		if (!proximityAlert.isRegistered)
		//			proximityAlert.registerReceiver();

		super.onResume();
	}

	public LocationManager getLocationManager() {
		return lm;
	}

	public MapView getMapView() {
		return mapView;
	}

	private void inputDialog(final GuessPoint gp) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Please enter the guessed distance to " + gp.getDescription_());

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				gp.setGuessDistance_(Double.valueOf(input.getText().toString()));

				Game.getUniqueInstance().addGuess(gp);

				if (Game.getUniqueInstance().evaluateGuesses() == 0) {
					GeoLocation loc = Game.getUniqueInstance().getGuessedLocation_();
					proximityAlert.setProximityPoint(loc.toGeoPoint());
					Log.i("GM", "proxpoint: " + loc + "|" + loc);

					mapView.getOverlays().remove(itemizedOverlay);
					for (GuessPoint gp : guessPoints) {
						itemizedOverlay.removeOverlayByTitle(gp.getDescription_());
					}
					mapView.getOverlays().add(itemizedOverlay);

					mapController.zoomToSpan(Math.abs(curLoc.getLatitudeE6() - loc.toGeoPoint().getLatitudeE6()),
							Math.abs(curLoc.getLongitudeE6() - loc.toGeoPoint().getLongitudeE6()));
					mapController.animateTo(new GeoPoint(
							(curLoc.getLatitudeE6() + loc.toGeoPoint().getLatitudeE6()) / 2,
							(curLoc.getLongitudeE6() + loc.toGeoPoint().getLongitudeE6()) / 2));
				}
			}
		});

		alert.setNegativeButton("Cancel", null);

		alert.show();
	}

	public void drawRouteOnMap(Route route) {

		new RetrieveRouteTask().execute(route);

	}

	private class RetrieveRouteTask extends AsyncTask<Route, Void, List<GeoLocation>> {

		@Override
		protected List<GeoLocation> doInBackground(Route... params) {
			Route route = params[0];
			return route.getPath();
		}

		protected void onPostExecute(List<GeoLocation> route) {

			List<GeoLocation> glList = new LinkedList<GeoLocation>();
			glList = route;

			PathOverlay routePath = new PathOverlay(Color.RED, MapActivity.this);
			for (int i = 0; i < glList.size(); i++) {
				routePath.addPoint(new GeoPoint(glList.get(i).getLatitude(), glList.get(i).getLongitude()));
			}
			MapActivity.this.mapView.getOverlays().add(routePath);
			MapActivity.this.mapView.invalidate();

		}

	}
}
