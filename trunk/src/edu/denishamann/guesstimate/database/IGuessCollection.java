package edu.denishamann.guesstimate.database;

import java.util.List;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

public interface IGuessCollection {

	//private List<GuessPoint>  
	
	public List<GuessPoint> getAll();
	
	public GuessPoint getNearest(GeoLocation searchLocation, int numberOfPoints);
	
	public void add(GuessPoint guessPoint);
	
	//public remove(GuessPoint guessPoint);
	
	
}
