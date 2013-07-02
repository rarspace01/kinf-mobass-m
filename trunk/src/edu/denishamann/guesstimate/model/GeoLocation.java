package edu.denishamann.guesstimate.model;

import org.osmdroid.util.GeoPoint;

public class GeoLocation {

	private double	longitude_;
	private double	latitude_;

	public GeoLocation(double latitude, double longitude) {
		this.longitude_ = longitude;
		this.latitude_ = latitude;
	}

	public GeoLocation(GeoPoint point) {
		this.longitude_ = point.getLongitudeE6() / 1e6;
		this.latitude_ = point.getLatitudeE6() / 1e6;
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

	public GeoPoint toGeoPoint() {
		return new GeoPoint(latitude_, longitude_);
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("");
		s.append(latitude_);
		s.append(", ");
		s.append(longitude_);

		return s.toString();
	}

}
