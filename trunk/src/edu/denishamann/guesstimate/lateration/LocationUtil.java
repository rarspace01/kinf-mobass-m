package edu.denishamann.guesstimate.lateration;

import edu.denishamann.guesstimate.model.GeoLocation;

public class LocationUtil
{
	private static double EARTH_RADIUS = 6367.450;
	
	public static Cartesian convertLocationToCarthesian(GeoLocation location)
	{
		double latRad = Math.toRadians(location.getLatitude());
		double lonRad = Math.toRadians(location.getLongitude());
		
		double x = EARTH_RADIUS * Math.cos(latRad) * Math.cos(lonRad);
		double y = EARTH_RADIUS * Math.cos(latRad) * Math.sin(lonRad);
		double z = EARTH_RADIUS * Math.sin(latRad);
		return new LocationUtil.Cartesian(x, y, z);
	}
	
	public static GeoLocation convertCartesianToLocation(Cartesian location)
	{
		double lat = Math.asin((location.getZ() / EARTH_RADIUS));
		double lon = Math.atan2(location.getY(), location.getX());
		
		GeoLocation converted = new GeoLocation(0.0, 0.0);
		converted.setLatitude(Math.toDegrees(lat));
		converted.setLongitude(Math.toDegrees(lon));
		return converted;
	}

	public static class Cartesian
	{
		private Double X, Y, Z;

		public Cartesian(Double X, Double Y, Double Z)
		{
			this.X = X;
			this.Y = Y;
			this.Z = Z;
		}
		
		public Double delta()
		{
			return Math.abs(X) + Math.abs(Y) + Math.abs(Z);
		}
		
		public Double getX() {
			return X;
		}

		public void setX(Double x) {
			X = x;
		}

		public Double getY() {
			return Y;
		}

		public void setY(Double y) {
			Y = y;
		}

		public Double getZ() {
			return Z;
		}

		public void setZ(Double z) {
			Z = z;
		}
		
		public void addDelta(Cartesian delta)
		{
			this.X -= delta.getX();
			this.Y -= delta.getY();
			this.Z -= delta.getZ();
		}
		
		public String toString()
		{
			StringBuilder s = new StringBuilder();
			s.append("X: ");
			s.append(X);
			s.append(", Y: ");
			s.append(Y);
			s.append(", Z: ");
			s.append(Z);
			
			return s.toString();
		}
	}
}
