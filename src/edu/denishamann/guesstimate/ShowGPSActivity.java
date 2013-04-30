package edu.denishamann.guesstimate;

import java.util.ArrayList;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.location.GpsSatellite;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ShowGPSActivity extends Activity implements LocationListener, GpsStatus.Listener {

	private LocationManager lm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_gps);
		
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		lm.addGpsStatusListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_g, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i("GM", "Location changed");
		//location.getProvider().
		
		TextView tvlat = (TextView) findViewById( R.id.latitudeView);
		tvlat.setText(""+location.getLatitude());
		
		TextView tvlong = (TextView) findViewById( R.id.longitudeView);
		tvlong.setText(""+location.getLongitude());
		
		TextView tvProvider = (TextView) findViewById( R.id.providerView);
		tvProvider.setText(location.getProvider().toString());
		

	}

	@Override
	public void onGpsStatusChanged(int event){
		
		if (event == GpsStatus.GPS_EVENT_FIRST_FIX){
		
		}else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS)
		{
			GpsStatus gpsstat = lm.getGpsStatus(null);
			
			TextView tvSatelites = (TextView) findViewById(R.id.satView);
			TextView tvSignalStrength = (TextView) findViewById( R.id.qualityView);
			//location.getProvider().toString()
			
			Iterable<GpsSatellite> gpssats= gpsstat.getSatellites();
			
			GpsSatellite curSat;
			int iSatCount=0;
			
			float sumSnr=0.0f;
			
			while((curSat = gpssats.iterator().next()) != null){
				
				sumSnr+=curSat.getSnr();
				iSatCount++;
			}
			
			
			
			tvSatelites.setText(""+iSatCount);
			tvSignalStrength.setText(""+(sumSnr/iSatCount));
			
			
		}
		else if (event == GpsStatus.GPS_EVENT_STARTED)
		{
		}
		else if (event == GpsStatus.GPS_EVENT_STOPPED){
			
		}
		
		
		
		
		//tvProvider.setText(location.getProvider().toString());
		
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
