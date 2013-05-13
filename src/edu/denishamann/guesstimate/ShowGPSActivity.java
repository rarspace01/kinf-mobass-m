package edu.denishamann.guesstimate;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ShowGPSActivity extends Activity implements LocationListener, GpsStatus.Listener {

	private LocationManager lm_;
	private GpsStatus gpsStatus_;
	private long lastFixTime_=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_gps);
		
		lm_ = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		lm_.addGpsStatusListener(this);
		
	      
        //refresh location every 10sec or 100meter if we move
        lm_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0,
                this);
        
        Log.i("GM", "Finished loading activity");
        
		
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
		
		Log.i("GM", "onGpsStatusChanged");
		
		if (event == GpsStatus.GPS_EVENT_FIRST_FIX){
			Log.i("GM", "GPS_EVENT_FIRST_FIX");
		}else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS)
		{
			Log.i("GM", "GPS_EVENT_SATELLITE_STATUS1");
			gpsStatus_ = lm_.getGpsStatus(gpsStatus_);
			
			Log.i("GM", "GPS_EVENT_SATELLITE_STATUS2");
			
			TextView tvSatelites = (TextView) findViewById(R.id.satView);
			TextView tvSignalStrength = (TextView) findViewById( R.id.qualityView);
			//location.getProvider().toString()
			
			Log.i("GM", "GPS_EVENT_SATELLITE_STATUS2 - "+gpsStatus_.getMaxSatellites());
			
			int iSatUsedCount=0;
			
			float sumSnr=0.0f;
			
			Log.i("GM", "GPS_EVENT_SATELLITE_STATUS3");
			
			for (GpsSatellite sat : gpsStatus_.getSatellites()){
				//Log.i("GM", "GPS_EVENT_SATELLITE_STATUS3 - LOOPS - "+iSatCount);
				if(sat.usedInFix()){
				Log.i("GM", "DEBUG SNR: "+sat.getSnr());
				sumSnr+=sat.getSnr();
				iSatUsedCount++;
				}
				//Log.i("GM", "GPS_EVENT_SATELLITE_STATUS3 - LOOPF - "+iSatCount);
			}
			
			Log.i("GM", "GPS_EVENT_SATELLITE_STATUS4");
			
			tvSatelites.setText(""+iSatUsedCount);
			if(iSatUsedCount>0){
				lastFixTime_=System.currentTimeMillis();
				tvSignalStrength.setText(""+(sumSnr/iSatUsedCount)+" Last fix on: "+lastFixTime_);
			}else{
				tvSignalStrength.setText("0 Last fix on: "+lastFixTime_);
			}
			
			Log.i("GM", "GPS_EVENT_SATELLITE_STATUS5");
			
		}
		else if (event == GpsStatus.GPS_EVENT_STARTED)
		{
			Log.i("GM", "GPS_EVENT_STARTED");
		}
		else if (event == GpsStatus.GPS_EVENT_STOPPED){
			Log.i("GM", "GPS_EVENT_STOPPED");
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
