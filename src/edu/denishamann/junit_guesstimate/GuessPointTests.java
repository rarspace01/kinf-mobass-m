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

public class GuessPointTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public GuessPointTests() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGuessPoints() {

		IGuessCollection guessc = new GuessCollection();

		List<GuessPoint> gpl = new LinkedList<GuessPoint>();

		Log.i("GM","try1");

		gpl = guessc.getNearest(new GeoLocation(49.904448, 10.859274), 4, 0);

		for (int i = 0; i < gpl.size(); i++) {
			Log.i("GM",gpl.get(i).getDescription_());
		}

		// new
		Log.i("GM","try2");

		gpl = guessc.getNearest(new GeoLocation(49.904448, 10.859274), 4, 0);

		for (int i = 0; i < gpl.size(); i++) {
			Log.i("GM",gpl.get(i).getDescription_());
		}

		// new
		Log.i("GM","try3");

		gpl = guessc.getNearest(new GeoLocation(49.904448, 10.859274), 4, 0);

		for (int i = 0; i < gpl.size(); i++) {
			Log.i("GM",gpl.get(i).getDescription_());
		}

		// new
		Log.i("GM","try4");

		gpl = guessc.getNearest(new GeoLocation(49.904448, 10.859274), 4, 0);

		for (int i = 0; i < gpl.size(); i++) {
			Log.i("GM",gpl.get(i).getDescription_());
		}

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
