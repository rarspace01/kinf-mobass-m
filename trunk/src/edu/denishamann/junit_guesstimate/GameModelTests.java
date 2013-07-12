package edu.denishamann.junit_guesstimate;

import java.util.LinkedList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import edu.denishamann.guesstimate.activitys.MainActivity;
import edu.denishamann.guesstimate.lateration.LocationUtil;
import edu.denishamann.guesstimate.model.Game;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

/**
 * test class for testing an entire game session
 * 
 * @author denis
 * 
 */
public class GameModelTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public GameModelTests() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * test method for game model
	 */
	public void testGameSession() {
		List<GuessPoint> gpl = new LinkedList<GuessPoint>();

		// test for difficutly 0 and 1
		for (int i = 0; i < 2; i++) {
			Log.i("JUNIT", "Starting Game with difficutly: " + i);

			MainActivity mActivity = getActivity();
			// start game
			Game.getInstance().startGame(i, "JUNIT - Denis", true);

			// current location
			GeoLocation currentLocation = new GeoLocation(49.904448, 10.859274);

			// aproach 4 guessed locations
			for (int j = 0; j < 4; j++) {

				// get guesspoints
				gpl.addAll(Game.getInstance().getLocationsToBeGuessed());

				// make sure it retrieved >=4 guesspoints
				assertTrue(gpl.size() >= 4);

				// print out the places we are going to set
				for (int k = 0; k < gpl.size(); k++) {
					Log.i("JUNIT", gpl.get(k).getDescription_());
				}

				// setting the 4 distances
				for (int k = 0; k < gpl.size(); k++) {
					gpl.get(k).setGuessDistance_(500);
				}

				// evaluate guess
				Game.getInstance().evaluateGuesses();

				// make sure it calced a guessed location
				assertTrue(Game.getInstance().getCalculatedLocation() != null);

				if (Game.getInstance().getCalculatedLocation() != null) {
					Log.i("JUNIT", "Your guess: Lat: "
							+ Game.getInstance().getCalculatedLocation()
									.getLatitude()
							+ " Lng "
							+ Game.getInstance().getCalculatedLocation()
									.getLongitude());
					Log.i("JUNIT",
							"You are off by "
									+ LocationUtil.distance(currentLocation,
											Game.getInstance()
													.getCalculatedLocation())
									+ " meters");
				} else {
					Log.e("JUNIT", "error on eval");
				}
				// approach the location
				Game.getInstance().guessedLocationApproached();
				// clear local list
				gpl.clear();

			}

			// lower the time left for testing purposes
			Game.getInstance().gameEnded(mActivity);

		}

	}

}
