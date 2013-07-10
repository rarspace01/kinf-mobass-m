package edu.denishamann.guesstimate.route;

import java.util.List;

import edu.denishamann.guesstimate.model.GeoLocation;

/**
 * interface for the route
 * @author denis
 *
 */
public interface IRoute {

	/**
	 * Return the Route. If no Route could be retrived it's {@link null}
	 * 
	 * @return {@link List}<{@link GeoLocation}> - The Route represented as
	 *         GeoLocations
	 */
	public List<GeoLocation> getPath();

}
