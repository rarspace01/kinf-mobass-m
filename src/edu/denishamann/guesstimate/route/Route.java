package edu.denishamann.guesstimate.route;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.io.HttpHelper;

/**
 * A class for retrieving the Route over MapQuest
 * 
 * @author denis
 * @version 0.3
 */
public class Route implements IRoute {

	private static final String TAG = "Route";

	private GeoLocation start_;
	private GeoLocation stop_;

	private String apiKey_ = "Fmjtd%7Cluub2d0y2q%2C7g%3Do5-9u2w00";

	public Route(GeoLocation start, GeoLocation stop) {

		start_ = start;
		stop_ = stop;

	}

	@Override
	public List<GeoLocation> getPath() {
		List<GeoLocation> gl = new LinkedList<GeoLocation>();

		// Adding the Start Point to the Route as Mapquest can only direct on
		// given Path not on Points in the wild
		gl.add(start_);

		String sPuffer = "";

		// Build the access URL
		String sURL = "http://open.mapquestapi.com/directions/v1/route?key="
				+ apiKey_
				+ "&ambiguities=ignore&from="
				+ start_.getLatitude()
				+ ","
				+ start_.getLongitude()
				+ "&to="
				+ stop_.getLatitude()
				+ ","
				+ stop_.getLongitude()
				+ "&callback=renderNarrative&outFormat=xml&routeType=pedestrian&unit=k&locale=de_DE";
		Log.i(TAG, "Access URL: " + sURL);
		sPuffer = HttpHelper.getPage(sURL);

		// check if Puffer is empty. When empty there is no internet connection
		// or the service is down.
		if (!sPuffer.isEmpty()) {

			XMLParser parser = new XMLParser();
			// Build the DOM based on the output
			Document doc = parser.getDomElement(sPuffer); // getting DOM element

			// mapquest give a maneuver for each action of the route. We'll
			// iterate over this
			NodeList nl = doc.getElementsByTagName("maneuvers");
			NodeList nlm = null;

			// check if we retrieved a route
			if (nl.getLength() > 0) {
				nlm = nl.item(0).getChildNodes();

				Log.i(TAG, "Found Maneuvers: " + nlm.getLength());
				// iterate through all maneuvers
				for (int i = 0; i < nlm.getLength(); i++) {
					Element e = (Element) nlm.item(i);
					// check if the String matches the pattern of a Lat or Long
					// Vlaue based on a regex
					if (parser.getValue(e, "lat").matches(
							"^[\\d]{1,3}[.][\\d]*$")
							&& parser.getValue(e, "lng").matches(
									"^[\\d]{1,3}[.][\\d]*$")) {
						// parse the values
						Double lat = Double.parseDouble(parser.getValue(e,
								"lat"));
						Double lng = Double.parseDouble(parser.getValue(e,
								"lng"));
						Log.i("Route", "Added Routepoint: " + lat + "/" + lng);
						gl.add(new GeoLocation(lat, lng));
					} else {
						Log.i(TAG,
								"ignored Input : [" + parser.getValue(e, "lat")
										+ "] - [" + parser.getValue(e, "lng")
										+ "]");
					}
				}
			} else {
				Log.e(TAG, "couldn't retrieve path");
			}

			// add the aim of the route to the end, because of the limitation of
			// direction end on streets not in the open field.
			gl.add(stop_);

			return gl;
		} else {
			Log.e(TAG, "no route, when offline");
			return null;
		}

	}

}
