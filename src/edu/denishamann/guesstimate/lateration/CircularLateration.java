package edu.denishamann.guesstimate.lateration;

import java.util.LinkedList;
import java.util.List;

import com.codeproject.math.Matrix;
import com.codeproject.math.MatrixMathematics;
import com.codeproject.math.NoSquareException;

import edu.denishamann.guesstimate.lateration.LocationUtil.Cartesian;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

/**
 * Circular Lateration class
 * 
 * calculates a circular lateration based on distance estimations
 */
public class CircularLateration implements ILateration
{
	
	private double				DELTA = 0.0001;	/* delta value to stop calculations */ 
	
	private List<GuessPoint>	locations;			/* locations of the objects */
	private Cartesian			ownCoordinates; 	/* own location in cartesian */
	
	private Cartesian			currentEstimation;	/* current best estimation */
	
	public static void main(String[] args)
	{
		
		GeoLocation testBahnhofCoor = new GeoLocation(0.0, 0.0);
		testBahnhofCoor.setLatitude(49.902209);
		testBahnhofCoor.setLongitude(10.900826);
		GuessPoint testBahnhof = new GuessPoint(testBahnhofCoor, "Bahnhof");
		testBahnhof.setGuessDistance_(2.5);
		//testBahnhof.setGuessDistance_(2.220432);

		GeoLocation testMaccasCoor = new GeoLocation(0.0, 0.0);
		testMaccasCoor.setLatitude(49.911495);
		testMaccasCoor.setLongitude(10.872416);
		GuessPoint testMaccas = new GuessPoint(testMaccasCoor, "Maccas");
		testMaccas.setGuessDistance_(1.0);
		//testMaccas.setGuessDistance_(1.039676);

		GeoLocation testFekiCoor = new GeoLocation(0.0, 0.0);
		testFekiCoor.setLatitude(49.907681);
		testFekiCoor.setLongitude(10.904603);
		GuessPoint testFeki = new GuessPoint(testFekiCoor, "Feki");
		testFeki.setGuessDistance_(3.0);
		//testFeki.setGuessDistance_(2.56152233);
		
		GeoLocation testDomCoor = new GeoLocation(0.0, 0.0);
		testDomCoor.setLatitude(49.890823);
		testDomCoor.setLongitude(10.882794);
		GuessPoint testDom = new GuessPoint(testFekiCoor, "Dom");
		testDom.setGuessDistance_(2.5);
		//testDom.setGuessDistance_(2.56162233);

		GeoLocation ownLocation = new GeoLocation(0.0, 0.0);
		ownLocation.setLatitude(49.902292);
		ownLocation.setLongitude(10.869806);
		
		List<GuessPoint> testLocations = new LinkedList<GuessPoint>();
		testLocations.add(testBahnhof);
		testLocations.add(testMaccas);
		testLocations.add(testFeki);
		testLocations.add(testDom);
		
		CircularLateration testLateration = new CircularLateration();
		testLateration.getLateration(testLocations);
		
	}
	
	public CircularLateration()
	{
		
	}
	
	public CircularLateration(List<GuessPoint> locations, GeoLocation ownLocation)
	{
		this.locations		= locations;
		this.ownCoordinates	= LocationUtil.convertLocationToCarthesian(ownLocation);
		
		this.currentEstimation = ownCoordinates; // our first approximation is the real position
		// maybe this is a bit "dirty" considering that usually we want to calculate our
		// position. But whoreever, live with it.
		// TODO: make estimation accessible, or rather the location-object
	}
	
	public GeoLocation getLateration(List<GuessPoint> guessPoints) {
		this.locations		= guessPoints;
		this.currentEstimation = new Cartesian(0.0, 0.0, 0.0); // our first approximation, centre of earth
		// TODO: get maybe a better estimation?
		
		calulcateCircularLateration();
		
		return LocationUtil.convertCartesianToLocation(currentEstimation);
	}
	
	/**
	 * method which calculates a position approximation using
	 * circular lateration. Will calculate delta/correction vectors
	 * which are applied to current best estimation until the cumulated
	 * delta values fall below the given threshold DELTA
	 */
	private void calulcateCircularLateration()
	{
		int i = 0;
		while(true)
		{
			i++;
			Cartesian newDelta = calculateDeltaVector();
			
			//System.out.println("Delta: " + newDelta);
			currentEstimation.addDelta(newDelta);

			System.out.println("Iteration " + i);
			System.out.println(LocationUtil.convertCartesianToLocation(currentEstimation));
			
			if(newDelta.delta() <= DELTA)
				break;
		}
	}
	
	/**
	 * calculate one step, hence a correction vector
	 * for the current best estimation
	 * calculates the matrix A and vector b and solves
	 * the equation system.
	 * Returns a correction vector as Cartesian coordinates.
	 */
	private Cartesian calculateDeltaVector()
	{
		Matrix matrixA = calculateMatrixValues();
		Matrix vectorB = calculateVectorValues();
		
		Cartesian deltaVector = new Cartesian(0.0, 0.0, 0.0);
		
		try {
			// first transpose matrix A for later calculations
			Matrix matrixAT = MatrixMathematics.transpose(matrixA);
			
			// calculate (A^T * A)^-1 * A^T
			Matrix matrixCalculations = 
				MatrixMathematics.multiply(
					MatrixMathematics.inverse(
							MatrixMathematics.multiply(matrixAT, matrixA)
					),
					matrixAT);
			
			// multiply resulting matrix with vector b
			Matrix result = MatrixMathematics.multiply(matrixCalculations, vectorB);
			
			deltaVector = new Cartesian(result.getValueAt(0,0), result.getValueAt(1, 0), result.getValueAt(2, 0));
		} catch (NoSquareException e) {
			e.printStackTrace();
		}
		
		return deltaVector;
	}
	
	/**
	 * calculates the Pythagoras pseudo-range
	 * 
	 * @param baseStation	- position of the base-station / object
	 * @param terminal		- own position
	 * @return				- Pythagoras distance
	 */
	private double pseudoRange(Cartesian baseStation, Cartesian terminal)
	{
		return Math.sqrt(
				Math.pow((baseStation.getX() - terminal.getX()), 2) +
				Math.pow((baseStation.getY() - terminal.getY()), 2) +
				Math.pow((baseStation.getZ() - terminal.getZ()), 2)
				);
	}
	
	/**
	 * calculates the values for the matrix A
	 */
	private Matrix calculateMatrixValues()
	{
		// get a Nx3 matrix
		Matrix matrixA = new Matrix(locations.size(), 3);
		
		for(int i = 0; i < locations.size(); i++)
		{
			// some mathematical stuff
			GuessPoint currentLocation = locations.get(i);
			
			Cartesian currentLocationCartesian = LocationUtil.convertLocationToCarthesian(currentLocation.getLocation_());
			
			// calculate matrix values a, b, and c for each location
			// see  Küpper, LBS, Page 134 / Chapter 6, Circular Lateration
			Double valueA =
					(currentLocationCartesian.getX() - currentEstimation.getX()) /
					pseudoRange(currentLocationCartesian, currentEstimation);

			Double valueB =
					(currentLocationCartesian.getY() - currentEstimation.getY()) /
					pseudoRange(currentLocationCartesian, currentEstimation);
			
			Double valueC =
					(currentLocationCartesian.getZ() + currentEstimation.getZ()) /
					pseudoRange(currentLocationCartesian, currentEstimation);
			
			matrixA.setValueAt(i, 0, valueA);
			matrixA.setValueAt(i, 1, valueB);
			matrixA.setValueAt(i, 2, valueC);
		}
		
		return matrixA;
	}
	
	/**
	 * calculates the values for the vector b
	 */
	private Matrix calculateVectorValues()
	{
		// get a vector with N dimensions
		Matrix vectorB = new Matrix(locations.size(), 1);
		
		// for all location objects
		for(int i = 0; i < locations.size(); i++)
		{
			GuessPoint currentLocation = locations.get(i);
			Cartesian currentLocationCartesian = LocationUtil.convertLocationToCarthesian(currentLocation.getLocation_());
			// get difference between guessed distance and the pseudo-range for current approximation
			Double vectorValue = (
					(currentLocation.getGuessDistance_() -	
					pseudoRange(currentLocationCartesian, currentEstimation)));
			
			vectorB.setValueAt(i, 0, vectorValue);
		}
		
		return vectorB;
	}
}
