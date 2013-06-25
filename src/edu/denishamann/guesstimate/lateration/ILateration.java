package edu.denishamann.guesstimate.lateration;

import java.util.List;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

public interface ILateration {
	public GeoLocation getLateration(List<GuessPoint> guessPoints);
}
