package edu.denishamann.guesstimate.database;

import java.util.List;

import edu.denishamann.guesstimate.model.GuessPoint;

/**
 * interface for retrieving the GuessPoints from the db and storeing them into the db
 *
 * @author denis
 */
public interface IGuessCollection {

	//private List<GuessPoint>  

	/**
	 * retrieves all GuessPoints
	 *
	 * @return
	 */
	public List<GuessPoint> getAll();

//	/**
//	 * retrieves the n. nearest locations, has the possibility to work with an offset
//	 *
//	 * @param searchLocation
//	 * @param numberOfPoints
//	 * @param offset
//	 * @return
//	 */
//	public List<GuessPoint> getNearest(GeoLocation searchLocation, int numberOfPoints, int offset);

	/**
	 * retrieves a given amount of guesspoints from the collection
	 * @param numberOfPoints
	 * @return {@link List}<{@link GuessPoint}> - the random points
	 */
	public List<GuessPoint> getRandom(int numberOfPoints);

	/**
	 * adds a guesspoint
	 *
	 * @param guessPoint
	 */
	public void addGuessPoint(GuessPoint guessPoint);

}
