package edu.denishamann.ors_api;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.io.HttpHelper;

/**
 * 
 * @author denis
 *
 */
public class Route {
	
	private GeoLocation start_;
	private GeoLocation stop_;
	
	public Route(GeoLocation start, GeoLocation stop) {
	
		start_=start;
		stop_=stop;
		
	}
	
	public List<GeoLocation> getPath(){
		List <GeoLocation> gl=new LinkedList<GeoLocation>();
		
		/* Draw Route
		 * http://stackoverflow.com/questions/10104581/osmdroid-pathoverlay 
		 */
		String sPuffer="";
		
		//String sURL="http://openrouteservice.org/php/OpenLSRS_DetermineRoute.php";
		
		
		String sURL="http://open.mapquestapi.com/directions/v1/route?key=Fmjtd%7Cluub2d0y2q%2C7g%3Do5-9u2w00&ambiguities=ignore&from="+start_.getLatitude()+","+start_.getLongitude()+"&to="+stop_.getLatitude()+","+stop_.getLongitude()+"&callback=renderNarrative&outFormat=xml&routeType=pedestrian&unit=k&locale=de_DE";
		System.out.println(sURL);
		sPuffer=HttpHelper.getPage(sURL);
		
		Log.v("GM",sPuffer);
		
		XMLParser parser = new XMLParser();
		
		Document doc = parser.getDomElement(sPuffer); // getting DOM element
		
		NodeList nl = doc.getElementsByTagName("maneuvers");
		NodeList nlm = null;
		
		
		if(nl.getLength()>0){
			nlm = nl.item(0).getChildNodes();
			
			System.out.println("Count: "+nlm.getLength());
			// looping through all item nodes <item>      
			for (int i = 0; i < nlm.getLength(); i++) {
				Element e = (Element) nlm.item(i);
				if(parser.getValue(e, "lat").matches("^[\\d]{1,3}[.][\\d]*$")&&parser.getValue(e, "lng").matches("^[\\d]{1,3}[.][\\d]*$")){
					Double lat = Double.parseDouble(parser.getValue(e, "lat"));
					Double lng = Double.parseDouble(parser.getValue(e, "lng"));
					gl.add(new GeoLocation(lat, lng));
				}else{
					System.out.println("input error on : "+parser.getValue(e, "lat")+ " - "+ parser.getValue(e, "lng"));
				}
				//add new 
			}
		}else{
			Log.e("GM", "couldn't retrieve path");
		}
		
		
		
		return gl;
		
	}
	
}
