package edu.denishamann.junit_guesstimate;

import java.util.LinkedList;
import java.util.List;

import android.test.ActivityUnitTestCase;
import android.util.Log;
import edu.denishamann.guesstimate.activitys.MainActivity;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.route.Route;

/**
 * testcase class for basic routing tests
 * 
 * @author denis
 * 
 */
public class RoutingTests extends ActivityUnitTestCase<MainActivity> {

	public RoutingTests() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * test method for the route test
	 */
	public void testRoute() {
		Log.i("JUNIT", "Routing test started");
		// create route with known points
		Route pr = new Route(new GeoLocation(49.904005, 10.859725),
				new GeoLocation(49.902637, 10.870646));
		List<GeoLocation> listLocations = new LinkedList<GeoLocation>();
		// retrieve path
		listLocations = pr.getPath();

		// check maneuver count == 7
		// assertEquals(listLocations.size(),7);
		assertTrue(listLocations.size() > 0);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
