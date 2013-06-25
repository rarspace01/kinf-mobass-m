package edu.denishamann.guesstimate.lateration;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import edu.denishamann.guesstimate.model.GuessPoint;

public class PseudoLateration implements ILateration {

	@Override
	public GeoPoint getLateration(List<GuessPoint> guessPoints) {
		double tmpLat = 0;
		double tmpLong = 0;
		for (GuessPoint guessPoint : guessPoints) {
			tmpLat += guessPoint.getLocation_().getLatitude();
			tmpLong = guessPoint.getLocation_().getLongitude();
		}

		return new GeoPoint(tmpLat / guessPoints.size(), tmpLong / guessPoints.size());
	}
}
