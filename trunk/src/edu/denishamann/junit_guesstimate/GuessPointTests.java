package edu.denishamann.junit_guesstimate;

import java.util.LinkedList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import edu.denishamann.guesstimate.activitys.MainActivity;
import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

/**
 * class for testing the getNearestAlgorithm
 * 
 * @author denis
 * 
 */
public class GuessPointTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public GuessPointTests() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * test method testGetNearestGuessPoints
	 */
	public void testGetNearestGuessPoints() {

		IGuessCollection guessc = new GuessCollection();

		List<GuessPoint> gpl = new LinkedList<GuessPoint>();

		// getting try 4 retrievals, which will empty the temp List of the guess
		// collection
		for (int i = 0; i < 4; i++) {

			Log.i("JUNIT", "try" + (i + 1));

			// retrieve the 4 nearest locations
			gpl = guessc
					.getNearest(new GeoLocation(49.904448, 10.859274), 4, 0);

			assertTrue(gpl.size() > 0);

			for (int j = 0; j < gpl.size(); j++) {
				assertTrue(gpl.get(i).getDescription_().length() > 0);
				Log.i("JUNIT", gpl.get(i).getDescription_());
			}

		}

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
