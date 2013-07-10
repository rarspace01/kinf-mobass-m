package edu.denishamann.junit_guesstimate;

import android.test.ActivityUnitTestCase;
import android.util.Log;
import edu.denishamann.guesstimate.activitys.MainActivity;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.route.Route;
import junit.framework.TestCase;

public class RoutingTests extends ActivityUnitTestCase<MainActivity>  {

	public RoutingTests() {
		super(MainActivity.class);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRoute(){
		Route pr=new Route(new GeoLocation(49.904005,10.859725), new GeoLocation(49.902637,10.870646));
		pr.getPath();
		Log.i("GM", "testcase1");
		assertEquals("bla","bla");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
