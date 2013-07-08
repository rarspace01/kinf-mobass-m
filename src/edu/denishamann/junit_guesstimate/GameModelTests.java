package edu.denishamann.junit_guesstimate;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import edu.denishamann.guesstimate.activitys.MainActivity;
import edu.denishamann.guesstimate.lateration.LocationUtil;
import edu.denishamann.guesstimate.model.Game;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

public class GameModelTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public GameModelTests() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testStart() {
		MainActivity mActivity = getActivity();
		// start game
		Game.getInstance().startGame(0, "Denis");

		// current location
		GeoLocation currentLocation = new GeoLocation(49.904448, 10.859274);

		// get 4 guesspoints
		List<GuessPoint> gpl = Game.getInstance().getLocationsToBeGuessed(currentLocation);

		//print out the places we are going to set
		for (int i = 0; i < gpl.size(); i++) {
			Log.i("GM",gpl.get(i).getDescription_());
		}

		gpl.get(0).setGuessDistance_(500); //erba
		gpl.get(1).setGuessDistance_(500); //dom
		gpl.get(2).setGuessDistance_(500); //altes rathaus
		gpl.get(3).setGuessDistance_(500); //gabelmo

		// evaluate guess
		Game.getInstance().evaluateGuesses();

		if (Game.getInstance().getCalculatedLocation() != null) {
			Log.i("GM","Your guess: Lat: "
					+ Game.getInstance().getCalculatedLocation().getLatitude() + " Lng "
					+ Game.getInstance().getCalculatedLocation().getLongitude());
			Log.i("GM","You are off by " + LocationUtil.distance(currentLocation, Game.getInstance().getCalculatedLocation()) + " meters");
		} else {
			Log.i("GM","error on eval");
		}

		// get 2 locations
		Game.getInstance().guessedLocationApproached(getActivity());

		Game.getInstance().guessedLocationApproached(getActivity());

		Game.getInstance().guessedLocationApproached(getActivity());

		//lower the time left for testing purposes
		Game.getInstance().setEndTime(0);

		//trigger the sql save action
		Game.getInstance().guessedLocationApproached(getActivity());
	}

}
