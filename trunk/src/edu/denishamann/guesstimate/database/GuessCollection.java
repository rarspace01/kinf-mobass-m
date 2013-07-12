package edu.denishamann.guesstimate.database;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

/**
 * guesscolleciton class which delivers the GuessPoints to the Game
 * 
 * @author denis
 * 
 */
public class GuessCollection implements IGuessCollection {

	private List<GuessPoint> guessPointList = new LinkedList<GuessPoint>();
	private List<GuessPoint> guessPointListBase = new LinkedList<GuessPoint>();

	Random generator;

	public GuessCollection() {
		// set Random generator
		generator = new Random();

		// add the known points manual., could be extended later for web URL
		// access etc.
		addGuessPoint(new GuessPoint(new GeoLocation(49.903549, 10.869554),
				"ERBA Campus"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.900523, 10.898606),
				"Bamberg Bahnhof"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.891082, 10.882707),
				"Bamberg Dom"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.892734, 10.888330),
				"Gabelmo"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.891631, 10.886887),
				"Altes Rathaus"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.891303, 10.897361),
				"Wilhelmspost"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.893446, 10.891505),
				"Bamberg ZOB"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.884100, 10.886698),
				"Wilde Rose Keller"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.90782, 10.905083),
				"Feki"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.897109, 10.892739),
				"Brauerei Fäßla"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.904987, 10.866522),
				"ERBA Stein der Religionen"));
		addGuessPoint(new GuessPoint(new GeoLocation(49.901334, 10.888691),
				"Ottokirche"));

		guessPointList.addAll(getAll());
	}

	@Override
	public List<GuessPoint> getAll() {
		List<GuessPoint> clone = new LinkedList<GuessPoint>();
		for (GuessPoint gp : guessPointListBase) {
			clone.add(new GuessPoint(gp));
		}
		return clone;
	}

	@Override
	public void addGuessPoint(GuessPoint guessPoint) {
		guessPointListBase.add(guessPoint);
	}

	// //code disabled because of switching to random mode
	// @Override
	// public List<GuessPoint> getNearest(GeoLocation searchLocation,
	// int numberOfPoints, int offset) {
	//
	// if (guessPointList.size() < numberOfPoints) {
	// guessPointList.clear();
	// guessPointList.addAll(getAll());
	// }
	//
	// // List<GuessPoint> gpl=new LinkedList<GuessPoint>();
	//
	// List<GuessPoint> gplTop = new LinkedList<GuessPoint>();
	//
	// double distance = 0.0;
	// double maxDistance = 0.0;
	//
	// // measure distance for everyone
	//
	// for (int i = 0; i < guessPointList.size(); i++) {
	//
	// distance = LocationUtil.distance(searchLocation, guessPointList
	// .get(i).getLocation_());
	// if (distance > maxDistance) {
	// maxDistance = distance;
	// }
	// guessPointList.get(i).setGuessCalcedDistance_(distance);
	// }
	//
	// // ranksort
	//
	// int foundGuessPoints = 0;
	// double minDistance = maxDistance;
	//
	// while (foundGuessPoints < numberOfPoints) {
	// minDistance = maxDistance;
	// // getMin Distance
	// for (int i = 0; i < guessPointList.size(); i++) {
	// if (guessPointList.get(i).getGuessCalcedDistance_() < minDistance) {
	// minDistance = guessPointList.get(i)
	// .getGuessCalcedDistance_();
	// }
	// }
	// // retrieve Guesspoint with min Distance
	// for (int i = 0; i < guessPointList.size(); i++) {
	// if (guessPointList.get(i).getGuessCalcedDistance_() == minDistance) {
	// gplTop.add(guessPointList.get(i));
	// guessPointList.remove(i);
	// break;
	// }
	// }
	// // transfer guesspoint with min distance
	// foundGuessPoints++;
	// }
	// return gplTop;
	// }

	@Override
	public List<GuessPoint> getRandom(int numberOfPoints) {
		List<GuessPoint> gplTop = new ArrayList<GuessPoint>();

		// iterator on number of points to be retrieved
		for (int i = 0; i < numberOfPoints; i++) {
			int element = generator.nextInt(guessPointList.size());
			// get random guesspoint from basetable
			GuessPoint gp = guessPointList.get(element);
			int tmp = i;
			// check if point is already in returnlist, if so, iteratoe one more time
			for (GuessPoint gpl : gplTop) {
				if (gp.getDescription_().equals(gpl.getDescription_())) {
					i--;
					break;
				}
			}
			// when gp was not in returnlist - add to list, remove from tempList
			if (tmp == i) {
				gplTop.add(gp);
				guessPointList.remove(element);

				// when templist is empty refill it
				if (guessPointList.isEmpty()) {
					guessPointList.addAll(getAll());
				}
			}
		}
		return gplTop;
	}

}
