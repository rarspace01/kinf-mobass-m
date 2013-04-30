package edu.denishamann.guesstimate;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class ShowGPSActivity extends Activity implements LocationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_gps);
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_g, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		//location.getProvider().
		
		TextView tvlat = (TextView) findViewById( R.id.latitudeView);
		tvlat.setText(""+location.getLatitude());
		
		TextView tvlong = (TextView) findViewById( R.id.longitudeView);
		tvlong.setText(""+location.getLongitude());
		
		TextView tvSatelites = (TextView) findViewById( R.id.satView);
		//location.getProvider().toString()
		TextView tvProvider = (TextView) findViewById( R.id.providerView);
		tvProvider.setText(location.getProvider().toString());
		
		TextView tvSignalStrength = (TextView) findViewById( R.id.qualityView);
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
