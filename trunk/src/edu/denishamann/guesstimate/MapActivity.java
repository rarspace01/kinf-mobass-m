package edu.denishamann.guesstimate;

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
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.util.constants.MapViewConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.ors_api.Route;

/**
 * 
 * @author denis
 * 
 */
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

		curLoc = new GeoPoint(currentPosition);
		Drawable newMarker = getResources().getDrawable(R.drawable.curloc);
		newMarker.setAlpha(155);
		OverlayItem curLocItem = new OverlayItem("ProxAlertPoint", "ProxAlertPoint Decsription", curLoc);
		curLocItem.setMarker(newMarker);
		curLocItem.setMarkerHotspot(HotspotPlace.CENTER);
		items.add(curLocItem);

		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
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

		proximityAlert = new ProximityAlert(this, new GeoPoint(49907035, 10901013));

		mapView.getOverlays().add(mMyItemsOverlay);
		mapView.invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("GM", "Menu item: " + item.getTitle());

		if (item.getTitle().toString().contains(getString(R.string.show_gps))) {
			Intent myIntent = new Intent(MapActivity.this, ShowGPSActivity.class);
			MapActivity.this.startActivity(myIntent);
		}
		
		if(item.getTitle().toString().contains(getString(R.string.enter_guesstimate))){
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			String provider = lm.getBestProvider(crit, true);
			Location loc = lm.getLastKnownLocation(provider);
			drawRouteOnMap(new Route(new GeoLocation(49.904005,10.859725), new GeoLocation(loc.getLatitude(),loc.getLongitude())));
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

	public MapView getMapView() {
		return mapView;
	}
	
	private void inputDialog()
	{
	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	alert.setTitle("Please enter guessed Distance");
	final EditText input = new EditText(this);
	alert.setView(input);
	
	alert.setPositiveButton("Ok", null);

	alert.setNegativeButton("Cancel", null);

	alert.show();
	}
	
	public void drawRouteOnMap(Route route){

		new RetrieveRouteTask().execute(route);
		
	}
	
	private  class RetrieveRouteTask extends AsyncTask<Route, Void, List<GeoLocation> > {

		@Override
		protected List<GeoLocation> doInBackground(Route... params) {
			Route route = params[0];
			return route.getPath();
		}

	    protected void onPostExecute(List<GeoLocation> route) {

	    	List <GeoLocation> glList=new LinkedList<GeoLocation>();
			glList = route;	
			
			PathOverlay routePath = new PathOverlay(Color.RED, MapActivity.this);
			for(int i=0; i<glList.size(); i++){
				routePath.addPoint(new GeoPoint(glList.get(i).getLatitude(),glList.get(i).getLongitude()));
			}
			MapActivity.this.mapView.getOverlays().add(routePath);
			MapActivity.this.mapView.invalidate();
	    	
	    }
		
	}
}
