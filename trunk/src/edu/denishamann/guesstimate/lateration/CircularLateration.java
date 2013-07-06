package edu.denishamann.guesstimate.lateration;

import java.util.List;


import edu.denishamann.guesstimate.lateration.LocationUtil.Cartesian;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

/**
 * Circular Lateration class
 * 
 * calculates a circular lateration based on distance estimations
 * 
 * @author lukas
 */
public class CircularLateration implements ILateration
{
	
	private double				DELTA = 0.09;	/* delta value to stop calculations */ 
	private int					MAXITERATIONS = 500;
	
	private List<GuessPoint>	locations;			/* locations of the objects */
	//private Cartesian			ownCoordinates; 	/* own location in cartesian */
	
	private Cartesian			currentEstimation;	/* current best estimation */
	
	public GeoLocation getLateration(List<GuessPoint> guessPoints) {
		this.locations		= guessPoints;
		this.currentEstimation = new Cartesian(0.0, 0.0, 0.0); // our first approximation, centre of earth
		// TODO: get maybe a better estimation? -> use arethemtic AVG?
		
		//setting Estimation on the first Guesspoint for faster interations
		if(guessPoints.size()>0){
			
			for(GuessPoint currentGP : guessPoints) {
				
				Cartesian newDelta = LocationUtil.convertLocationToCarthesian(currentGP.getLocation_());	
				this.currentEstimation.addDelta(newDelta);
			}
			currentEstimation.setX(Math.abs(currentEstimation.getX() / guessPoints.size()));		
			currentEstimation.setY(Math.abs(currentEstimation.getY() / guessPoints.size()));	
			currentEstimation.setZ(Math.abs(currentEstimation.getZ() / guessPoints.size()));
		}
		
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
		double delta=0.0;
		while(i<MAXITERATIONS)
		{
			i++;
			Cartesian newDelta = calculateDeltaVector();
			
			//System.out.println("Delta: " + newDelta);
			currentEstimation.addDelta(newDelta);

			if(i % 10 == 0)
			{
				System.out.println("Iteration " + i);
				System.out.println(currentEstimation.toString() + " - " + LocationUtil.convertCartesianToLocation(currentEstimation));
			}
			delta = newDelta.delta();
			if(delta <= DELTA){
				break;
			}else{
				System.out.println("Delta @"+delta);
			}
			
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
					((currentLocation.getGuessDistance_()) -	
					pseudoRange(currentLocationCartesian, currentEstimation)));
			
			vectorB.setValueAt(i, 0, vectorValue);
		}
		
		return vectorB;
	}
}
