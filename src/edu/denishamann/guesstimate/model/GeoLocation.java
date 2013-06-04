package edu.denishamann.guesstimate.model;

public class GeoLocation {

	private double longitude_;
	private double latitude_;
	
	public GeoLocation(double latitude, double longitude) {
		this.longitude_=longitude;
		this.latitude_=latitude;
	}

	public double getLongitude() {
		return longitude_;
	}

	public void setLongitude(double longitude_) {
		this.longitude_ = longitude_;
	}

	public double getLatitude() {
		return latitude_;
	}

	public void setLatitude(double latitude_) {
		this.latitude_ = latitude_;
	}
	
	
	
	
}
