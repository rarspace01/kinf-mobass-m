package edu.denishamann.guesstimate.lateration;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import edu.denishamann.guesstimate.model.GuessPoint;

public interface ILateration {
	public GeoPoint getLateration(List<GuessPoint> guessPoints);
}
