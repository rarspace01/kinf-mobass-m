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
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.denishamann.guesstimate.CircleOverlay;
import edu.denishamann.guesstimate.OsmItemizedOverlay;
import edu.denishamann.guesstimate.ProximityAlert;
import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.model.Game;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;
import edu.denishamann.ors_api.Route;

/**
 * @author denis
 */
public class MapActivity extends Activity implements LocationListener, MapViewConstants {

	private MapController       mapController;
	private MapView             mapView;
	private OsmItemizedOverlay  itemizedOverlay;
	private LocationManager     locationManager;
	private GeoPoint            curLoc;
	private ProximityAlert      proximityAlert;
	private List<GuessPoint>    guessPoints;
	private List<CircleOverlay> circleOverlays;
	private ResourceProxy       mResourceProxy;
	private PathOverlay         routePath;
	private CountDownTimer      countDownTimer;

	public  boolean alreadyRunning = false;
	private boolean optionsVisible = false;
	private OverlayItem curLocItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		circleOverlays = new ArrayList<CircleOverlay>();
		proximityAlert = new ProximityAlert(this);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
		mapView.setMultiTouchControls(true);
		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();
		mapController.setZoom(15);

		// start location updates
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
		}
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		}

		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(crit, true);

		Location loc = locationManager.getLastKnownLocation(provider);
		if (loc != null) {
			Log.i("GM", "Current Location: " + loc.getLatitude() + " - " + loc.getLongitude());
			curLoc = new GeoPoint(loc.getLatitude(), loc.getLongitude());
		} else {
			Log.i("GM", "Loc == null, using default value");
			curLoc = new GeoPoint(49.903038, 10.869427);
		}

		guessPoints = Game.getInstance().getLocationsToBeGuessed();

		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

		itemizedOverlay = new OsmItemizedOverlay(new ArrayList<OverlayItem>(),
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
						if (item.mTitle.isEmpty()) {
							Toast.makeText(MapActivity.this, item.mDescription, Toast.LENGTH_LONG).show();
						} else if (item.mDescription.isEmpty()) {
							for (GuessPoint gp : guessPoints) {
								if (gp.getDescription_().equals(item.mTitle)) {
									inputDialog(gp);
								}
							}
						}
						return true;
					}

					@Override
					public boolean onItemLongPress(final int index, final OverlayItem item) {
						if (item.mTitle.isEmpty()) {
							Toast.makeText(MapActivity.this, item.mDescription, Toast.LENGTH_LONG).show();
						} else if (item.mDescription.isEmpty()) {
							Toast.makeText(MapActivity.this, item.mTitle, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MapActivity.this, item.mTitle, Toast.LENGTH_LONG).show();
						}
						return true;
					}
				}, mResourceProxy);

		if (Game.getInstance().getDifficulty() == 0) {
			for (GuessPoint gp : guessPoints) {
				OverlayItem guessItem = new OverlayItem(gp.getDescription_(), "", gp.getLocation_()
						.toGeoPoint());
				itemizedOverlay.addItem(guessItem);
			}
		} else if (Game.getInstance().getDifficulty() == 1) {
			for (GuessPoint gp : guessPoints) {
				OverlayItem guessItem = new OverlayItem("", gp.getDescription_(), gp.getLocation_()
						.toGeoPoint());
				itemizedOverlay.addItem(guessItem);
				CircleOverlay circleOverlay = new CircleOverlay(this, mapView, gp.getLocation_().toGeoPoint(), (int) gp.getGuessDistance_(), 55, false);
				circleOverlays.add(circleOverlay);
			}
			mapView.getOverlays().addAll(circleOverlays);

			proximityAlert.setProximityPoint(Game.getInstance().getCalculatedLocation().toGeoPoint());

			drawRouteOnMap(new Route(new GeoLocation(curLoc), Game.getInstance().getCalculatedLocation()));
		}

		Drawable newMarker = getResources().getDrawable(R.drawable.curloc);
		newMarker.setAlpha(155);
		curLocItem = new OverlayItem("Your current location", "Current Location", curLoc);
		curLocItem.setMarker(newMarker);
		curLocItem.setMarkerHotspot(HotspotPlace.CENTER);
		itemizedOverlay.addItem(curLocItem);

		mapView.getOverlays().add(itemizedOverlay);
		mapView.invalidate();

		countDownTimer = new CountDownTimer(Game.getInstance().getTimeLeft(), 1000) {

			public void onTick(long millisUntilFinished) {
				TextView timer = (TextView) findViewById(R.id.timer);
				String time = String.format("%02d", (int) (Game.getInstance().getTimeLeft() / (1000 * 60)));
				time += ":";
				time += String.format("%02d", (Game.getInstance().getTimeLeft() / 1000) % 60);
				timer.setText("" + time);
			}

			public void onFinish() {
				TextView timer = (TextView) findViewById(R.id.timer);
				timer.setText("00:00");

				Game.getInstance().gameEnded(getApplicationContext());
				Intent intent = new Intent(MapActivity.this, HighScoreActivity.class);
				intent.putExtra("gameEnded", true);
				startActivity(intent);
			}
		};
		countDownTimer.start();

		registerReceiver(proximityAlert, new IntentFilter("edu.denishamann.guesstimate.PROXIMITYALERT"));

		alreadyRunning = true;
	}

	public void getNewGuessPoints() {
		if (Game.getInstance().getDifficulty() == 0) {
			guessPoints = Game.getInstance().getLocationsToBeGuessed();

			mapView.getOverlays().removeAll(circleOverlays);

			circleOverlays.clear();
			itemizedOverlay.removeAllItems();

			for (GuessPoint gp : guessPoints) {
				OverlayItem guessItem = new OverlayItem(gp.getDescription_(), "", gp.getLocation_().toGeoPoint());
				itemizedOverlay.addItem(guessItem);
			}
			itemizedOverlay.addItem(curLocItem);

			proximityAlert.hideProximityPoint();

			if (routePath != null) {
				MapActivity.this.mapView.getOverlays().remove(routePath);
			}

			mapView.invalidate();
		} else {
			startActivity(new Intent(this, GuessActivity.class));
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		Log.i("GM", "Location updated & redraw to " + lat + ":" + lng + " | zoom: " + mapView.getZoomLevel());

		curLoc.setCoordsE6(lat, lng);

		mapView.invalidate();
	}

	public void goToCurrentLocation(View view) {
		mapController.animateTo(new GeoPoint(curLoc));
		mapView.invalidate();
	}

	public void showAllPointsAndCurrentLocationOnMap() {
		int tmpLongitude = 0;
		int tmpLatitude = 0;
		int maxLat = Integer.MIN_VALUE;
		int maxLng = Integer.MIN_VALUE;
		int minLat = Integer.MAX_VALUE;
		int minLng = Integer.MAX_VALUE;
		for (GuessPoint gp : guessPoints) {
			if (maxLat < gp.getLocation_().toGeoPoint().getLatitudeE6())
				maxLat = gp.getLocation_().toGeoPoint().getLatitudeE6();
			else if (minLat > gp.getLocation_().toGeoPoint().getLatitudeE6())
				minLat = gp.getLocation_().toGeoPoint().getLatitudeE6();
			if (maxLng < gp.getLocation_().toGeoPoint().getLongitudeE6())
				maxLng = gp.getLocation_().toGeoPoint().getLongitudeE6();
			else if (minLng > gp.getLocation_().toGeoPoint().getLongitudeE6())
				minLng = gp.getLocation_().toGeoPoint().getLongitudeE6();
			tmpLongitude += gp.getLocation_().toGeoPoint().getLongitudeE6();
			tmpLatitude += gp.getLocation_().toGeoPoint().getLatitudeE6();
		}

		if (maxLat < curLoc.getLatitudeE6())
			maxLat = curLoc.getLatitudeE6();
		else if (minLat > curLoc.getLatitudeE6())
			minLat = curLoc.getLatitudeE6();
		if (maxLng < curLoc.getLongitudeE6())
			maxLng = curLoc.getLongitudeE6();
		else if (minLng < curLoc.getLongitudeE6())
			minLng = curLoc.getLongitudeE6();

		tmpLatitude += curLoc.getLatitudeE6();
		tmpLongitude += curLoc.getLongitudeE6();

		double spanLat = (maxLat - minLat) * 1.0;
		double spanLng = (maxLng - minLng) * 1.0;
		mapController.setCenter(new GeoPoint(tmpLatitude / 5, tmpLongitude / 5));
		mapController.zoomToSpan((int) spanLat, (int) spanLng);
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus && !optionsVisible)
			showAllPointsAndCurrentLocationOnMap();
	}

	public LocationManager getLocationManager() {
		return locationManager;
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
				if (input.getText().toString().isEmpty()) {
					gp.setGuessDistance_(0);
				} else {
					gp.setGuessDistance_(Double.valueOf(input.getText().toString()));
				}

				itemizedOverlay.removeOverlayByTitle(gp.getDescription_());
				itemizedOverlay.addItem(new OverlayItem("", gp.getDescription_(), gp.getLocation_().toGeoPoint()));

				if (Game.getInstance().evaluateGuesses()) {
					GeoLocation loc = Game.getInstance().getCalculatedLocation();
					if (curLoc.distanceTo(loc.toGeoPoint()) < 50) {
						Toast.makeText(MapActivity.this, "Great guess!", Toast.LENGTH_LONG).show();
						Game.getInstance().guessedLocationApproached();
						getNewGuessPoints();
						return;
					}

					proximityAlert.setProximityPoint(loc.toGeoPoint());

					drawRouteOnMap(new Route(new GeoLocation(curLoc), loc));

					Log.i("GM", "proxpoint: " + loc + "|" + loc);

					for (GuessPoint gp : guessPoints) {
						CircleOverlay circleOverlay = new CircleOverlay(MapActivity.this, mapView, gp.getLocation_().toGeoPoint(), (int) gp.getGuessDistance_(), 55, false);
						circleOverlays.add(circleOverlay);
					}
					mapView.getOverlays().addAll(circleOverlays);

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

			if (routePath != null) {
				MapActivity.this.mapView.getOverlays().remove(routePath);
			}

			if (glList == null) {

			} else {

				routePath = new PathOverlay(Color.RED, MapActivity.this);
				for (int i = 0; i < glList.size(); i++) {
					routePath.addPoint(new GeoPoint(
							glList.get(i).getLatitude(), glList.get(i)
							.getLongitude()));
				}
				MapActivity.this.mapView.getOverlays().add(routePath);
			}
			MapActivity.this.mapView.invalidate();
		}
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

	@Override
	public void onBackPressed() {
	}

//	@Override
//	protected void onDestroy() {
//		Log.d("GM", "unregistered Proximity alert");
//		proximityAlert.unregisterReceiver();
//		super.onDestroy();
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.showAll:
				showAllPointsAndCurrentLocationOnMap();
				break;
			case R.id.giveUp:
				countDownTimer.cancel();
				Game.getInstance().giveUp();
				startActivity(new Intent(this, MainActivity.class));
				break;

			default:
				break;
		}

		optionsVisible = true;

		return super.onOptionsItemSelected(item);
	}
}
