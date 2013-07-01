package edu.denishamann.guesstimate.model;

import java.util.LinkedList;
import java.util.List;

import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.lateration.CircularLateration;
import edu.denishamann.guesstimate.lateration.PseudoLateration;

public class Game {

	private GeoLocation guessedLocation_;
	private List<GuessPoint> guessedRanges_ = new LinkedList<GuessPoint>();
	private int gamestate_ = 0;
	private long timeLeft_=0;
	private int successfullRounds_=0;
	private int difficulty_ = 0;
	private IGuessCollection currentGuessCollection=null;
	
	private boolean USE_CIRCULARLATERATION = true;
	private int PLAYTIME = 60; //Rundenzeit in Min
	

	private Game uniqueInstance_;

	public Game getUniqueInstance() {
		if (uniqueInstance_ == null) {
			this.uniqueInstance_ = new Game();
		}
		return uniqueInstance_;
	}

	public void startGame(int difficulty) {
		this.currentGuessCollection = new GuessCollection();
		this.difficulty_ = difficulty;
		gamestate_ = 1;
		timeLeft_ = System.currentTimeMillis() + 1000*60*PLAYTIME;
	}

	public List<GuessPoint> getLocationsToBeGuessed(GeoLocation currentLocation){
		return this.currentGuessCollection.getNearest(currentLocation, 4, 0);
	}
	
	public void addGuess(GuessPoint gp) {
		guessedRanges_.add(gp);
	}

	public int evaluateGuesses() {
		int result = 0;

		if (guessedRanges_.size() < 4) {
			result = 1;
			System.out.println("to few guesses");
		} else {
			if (USE_CIRCULARLATERATION) {
				System.out.println("using circular");
				guessedLocation_ = new CircularLateration()
						.getLateration(guessedRanges_);
			} else {
				System.out.println("using pseudo lateration");
				guessedLocation_ = new PseudoLateration()
						.getLateration(guessedRanges_);
			}
		}

		return result;
	}

	public boolean guessedLocationApproached(){
		boolean hasRoundsLeft=false;
		if(System.currentTimeMillis()<timeLeft_){
			this.successfullRounds_++;
			hasRoundsLeft=true;
		}else{
			//save highscore
			System.out.println("HS: "+successfullRounds_);
		}
		return hasRoundsLeft;
	}
	
	public GeoLocation getGuessedLocation_() {
		
		if(guessedLocation_==null){
			evaluateGuesses();
		}
		
		return guessedLocation_;
	}

}
