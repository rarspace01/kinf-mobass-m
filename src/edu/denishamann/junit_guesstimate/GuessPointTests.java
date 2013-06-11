package edu.denishamann.junit_guesstimate;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import edu.denishamann.guesstimate.MainActivity;
import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

public class GuessPointTests extends ActivityInstrumentationTestCase2<MainActivity> {

	public GuessPointTests() {
		super(MainActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGuessPoints(){

		IGuessCollection guessc=new GuessCollection();
		guessc.add(new GuessPoint(new GeoLocation(49.903549,10.869554), "ERBA Campus"));
		guessc.add(new GuessPoint(new GeoLocation(49.900523,10.898606), "Bamberg Bahnhof"));
		guessc.add(new GuessPoint(new GeoLocation(49.891082,10.882707), "Bamberg Dom"));
		guessc.add(new GuessPoint(new GeoLocation(49.892734,10.88833), "Gabelmo"));
		guessc.add(new GuessPoint(new GeoLocation(49.891631,10.886887), "Altes Rathhaus"));
		guessc.add(new GuessPoint(new GeoLocation(49.891303,10.897361), "Wilhelmlspost"));
		guessc.add(new GuessPoint(new GeoLocation(49.893446,10.891505), "Bamberg ZOB"));
		guessc.add(new GuessPoint(new GeoLocation(49.8841,10.886698), "Wilde Rose Keller"));
		
		
		
		List<GuessPoint> gpl=new LinkedList<GuessPoint>();
		
		gpl=guessc.getNearest(new GeoLocation(49.904448,10.859274), 4, 0);
		
		for(int i=0; i<gpl.size(); i++){
			System.out.println(gpl.get(i).getDescription_());
		}
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
}
