package edu.denishamann.guesstimate.lateration;

import java.util.List;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

/**
 * 
 * @author paul
 *
 */
public class PseudoLateration implements ILateration {

	@Override
	public GeoLocation getLateration(List<GuessPoint> guessPoints) {
		double tmpLat = 0;
		double tmpLong = 0;
		for (GuessPoint guessPoint : guessPoints) {
			tmpLat += guessPoint.getLocation_().getLatitude();
			tmpLong = guessPoint.getLocation_().getLongitude();
		}

		return new GeoLocation(tmpLat / guessPoints.size(), tmpLong / guessPoints.size());
	}
}
