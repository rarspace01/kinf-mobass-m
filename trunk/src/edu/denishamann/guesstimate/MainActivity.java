package edu.denishamann.guesstimate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		//getLCS
		
		// This verification should be done during onStart() because the system calls
	    // this method when the user returns to the activity, which ensures the desired
	    // location provider is enabled each time the activity resumes from the stopped state.
	    LocationManager locationManager =
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	    if (!gpsEnabled) {
	        // Build an alert dialog here that requests that the user enable
	        // the location services, then when the user clicks the "OK" button,
	        // call enableLocationSettings()
	    	enableLocationSettings();
	    }
	


		
		Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
		//myIntent.putExtra("key", value); //Optional parameters
		MainActivity.this.startActivity(myIntent);

	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		Log.i("GM", "Menu item: "+item.getTitle());
		
		if(item.getTitle().toString().contains(getString(R.string.show_gps))){
			Log.i("GM", "Got correct item");
			
			Intent myIntent = new Intent(MainActivity.this, ShowGPSActivity.class);
			//myIntent.putExtra("key", value); //Optional parameters
			MainActivity.this.startActivity(myIntent);
			
		}
		
		return true;
	}

	public void startWebIntent(View view){
		Log.i("GM", "Web Intent");
		Uri uri = Uri.parse("http://www.uni-bamberg.de"); 
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.setData(uri); 
		startActivity(intent); 
	}
	
	public void startGeoIntent(View view){
		Log.i("GM", "Geo Intent");
		Uri uri = Uri.parse("geo:49.906277,10.903387"); 
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.setData(uri); 
		startActivity(intent); 
	}
	
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}
	
}
