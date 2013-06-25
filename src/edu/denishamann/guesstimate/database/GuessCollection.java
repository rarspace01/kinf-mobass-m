package edu.denishamann.guesstimate.database;

import java.util.LinkedList;
import java.util.List;

import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

public class GuessCollection implements IGuessCollection {

	private List<GuessPoint> gpl_=new LinkedList<GuessPoint>();
	
	public GuessCollection() {
		this.add(new GuessPoint(new GeoLocation(49.903549,10.869554), "ERBA Campus"));
		this.add(new GuessPoint(new GeoLocation(49.900523,10.898606), "Bamberg Bahnhof"));
		this.add(new GuessPoint(new GeoLocation(49.891082,10.882707), "Bamberg Dom"));
		this.add(new GuessPoint(new GeoLocation(49.892734,10.88833), "Gabelmo"));
		this.add(new GuessPoint(new GeoLocation(49.891631,10.886887), "Altes Rathhaus"));
		this.add(new GuessPoint(new GeoLocation(49.891303,10.897361), "Wilhelmlspost"));
		this.add(new GuessPoint(new GeoLocation(49.893446,10.891505), "Bamberg ZOB"));
		this.add(new GuessPoint(new GeoLocation(49.8841,10.886698), "Wilde Rose Keller"));
	}
	
	@Override
	public List<GuessPoint> getAll() {
		return gpl_;
	}

	@Override
	public void add(GuessPoint guessPoint) {
		gpl_.add(guessPoint);
		
	}

	@Override
	public List<GuessPoint> getNearest(GeoLocation searchLocation,
			int numberOfPoints, int offset) {

		List<GuessPoint> gpl=new LinkedList<GuessPoint>();
		
		List<GuessPoint> gplTop=new LinkedList<GuessPoint>();
		
		
		double dx=0.0;
		double dy=0.0;
		double distance=0.0;
		double maxDistance=0.0;
		
		//get all Points
		gpl=getAll();
		
		//measure distance for everyone
		
		for(int i=0; i<gpl.size(); i++){
			
			dx=gpl.get(i).getLocation_().getLatitude()-searchLocation.getLatitude();
			dy=gpl.get(i).getLocation_().getLongitude()-searchLocation.getLongitude();
			distance=Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
			if(distance>maxDistance){
				maxDistance=distance;
			}
			gpl.get(i).setGuessCalcedDistance_(distance);
		}
		
		//ranksort
		
		int foundGuessPoints=0;
		double minDistance=maxDistance;
		
		while(foundGuessPoints<numberOfPoints){
			minDistance=maxDistance;
			//getMin Distance
			for(int i=0; i<gpl.size();i++){
				if(gpl.get(i).getGuessCalcedDistance_()<minDistance){
					minDistance=gpl.get(i).getGuessCalcedDistance_();
				}
			}
			//retrieve Guesspoint with min Distance
			for(int i=0; i<gpl.size();i++){
				if(gpl.get(i).getGuessCalcedDistance_()==minDistance){
					gplTop.add(gpl.get(i));
					gpl.remove(i);
					break;
				}
			}
			//transfer guesspoint with min distance
			foundGuessPoints++;
		}
		
		return gplTop;
	}

	@Override
	public GuessPoint getNext() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
