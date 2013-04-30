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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MapActivity extends Activity implements LocationListener, MapViewConstants {

    private MapController mapController;
    private MapView mapView;
    private ItemizedOverlay<OverlayItem> mMyItemsOverlay;
    
    private ResourceProxy mResourceProxy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

		mapView.setMultiTouchControls(true);
		mapView.setBuiltInZoomControls(true);
		//mapView.set
		
        mapController = mapView.getController();
        
        
        //get current location
        
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //refresh location every 10sec or 100meter if we move
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50,
                this);
        Criteria crit = new Criteria();
        crit.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = lm.getBestProvider(crit, true);
        Location loc = lm.getLastKnownLocation(provider);
        
        Log.i("GM", "Current Location: "+loc.getLatitude()+" - "+loc.getLongitude());
        
        GeoPoint currentPosition = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        
        mapController.setCenter(currentPosition);
        mapController.setZoom(15);
        
        //create overlay icon
        
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        
        GeoPoint erbaInsel = new GeoPoint(49903259,10869727);
        items.add(new OverlayItem("Erba Insel", "Erba Insel Descr", erbaInsel));
        
        
        /* OnTapListener for the Markers, shows a simple Toast. */
        this.mMyItemsOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                        new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                                @Override
                                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                                	Toast.makeText(
                                			MapActivity.this,
                                            "Item '" + item.mTitle+"'", Toast.LENGTH_LONG).show();
                                        Log.i("GM", "Pressed Icon");
                                        return true; // We 'handled' this event.
                                }

                                @Override
                                public boolean onItemLongPress(final int index, final OverlayItem item) {
                                	Toast.makeText(
                                			MapActivity.this, 
                                            "Item '" + item.mTitle +"'",Toast.LENGTH_LONG).show();
                                	Log.i("GM", "Long Pressed Icon");
                                        return false;
                                }
                        }, mResourceProxy);
        
        this.mapView.getOverlays().add(this.mMyItemsOverlay);
        mapView.invalidate();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
    public void onLocationChanged(Location location) {
		Log.i("GM", "Locatino updated & redraw");
        int lat = (int) (location.getLatitude() * 1E6);
        int lng = (int) (location.getLongitude() * 1E6);
        GeoPoint gpt = new GeoPoint(lat, lng);
        mapController.setCenter(gpt);
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
	
}
