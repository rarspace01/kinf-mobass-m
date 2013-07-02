package edu.denishamann.junit_guesstimate;

import java.util.LinkedList;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import edu.denishamann.guesstimate.activitys.MainActivity;
import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.lateration.LocationUtil;
import edu.denishamann.guesstimate.model.Game;
import edu.denishamann.guesstimate.model.GeoLocation;
import edu.denishamann.guesstimate.model.GuessPoint;

public class GameModelTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public GameModelTests() {
		super(MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testStart() {
		// start game
		Game game = new Game();
		game.startGame(0, "Denis");

		// current location
		GeoLocation currentLocation = new GeoLocation(49.904448, 10.859274);

		// get 4 guesspoints
		IGuessCollection gc = new GuessCollection();

		List<GuessPoint> gpl = new LinkedList<GuessPoint>();

		gpl.addAll(gc.getNearest(currentLocation, 4, 0));
		
		/*
		 * manual set guessed distance
		 */
		
		//print out the places we are going to set
		for(int i=0; i<gpl.size();i++){
			System.out.println(gpl.get(i).getDescription_());
		}
		
		gpl.get(0).setGuessDistance_(500); //erba
		gpl.get(1).setGuessDistance_(500); //dom
		gpl.get(2).setGuessDistance_(500); //altes rathaus
		gpl.get(3).setGuessDistance_(500); //gabelmo

		
		
		for(int i=0; i<gpl.size();i++){
			game.addGuess(gpl.get(i));
		}
		
		// evlaute guess
		game.evaluateGuesses();

		if (game.getGuessedLocation_() != null) {
			System.out.println("Your guess: Lat: "
					+ game.getGuessedLocation_().getLatitude() + " Lng "
					+ game.getGuessedLocation_().getLongitude());
			System.out.println("You are off by "+LocationUtil.distance(currentLocation, game.getGuessedLocation_())+" meters");
		}else{
			System.out.println("error on eval");
		}

		game.guessedLocationApproached(getActivity());
		
		//assert
		
	}

}
