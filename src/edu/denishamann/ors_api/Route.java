package edu.denishamann.ors_api;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.io.HttpHelper;
import android.location.Location;

public class Route {

	private GeoLocation startPoint;
	private GeoLocation stopPoint;
	
	public Route(GeoLocation start, GeoLocation stop) {
	
		HttpHelper.getPage("http://openrouteservice.org/index.php?start=7.0892567,50.7265543&end=7.0986258,50.7323634&pref=Pedestrian&lang=de");
		
	}
	
	
}
