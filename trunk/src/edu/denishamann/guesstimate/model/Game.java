package edu.denishamann.guesstimate.model;

import java.util.LinkedList;
import java.util.List;

import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.lateration.CircularLateration;
import edu.denishamann.guesstimate.lateration.PseudoLateration;

public class Game {

	private GeoLocation guessedLocation_;
	private List<GuessPoint> guessedRanges_ = new LinkedList<GuessPoint>();
	private int gamestate_=0;
	private int difficulty_=0;
	private boolean USE_CIRCULARLATERATION=true;
	
	public void startGame(int difficulty){
		this.difficulty_ = difficulty;
		gamestate_=1;
	}
	
	public void addGuess(GuessPoint gp){
		guessedRanges_.add(gp);
	}
	
	public List<GeoLocation> getNextGuesspoints(){
		//IGuessCollection 
		return null;
	}
	
	public int evaluateGuesses(){
		int result=0;
				
		if(guessedRanges_.size()<4){
			result = 1;
			System.out.println("to few guesses");
		}else{
			if(USE_CIRCULARLATERATION){
				System.out.println("using circular");
				guessedLocation_ = new CircularLateration().getLateration(guessedRanges_); 
			}else{
				System.out.println("using pseudo lateration");
				guessedLocation_ = new PseudoLateration().getLateration(guessedRanges_);
			}
		}
		
		return result;
	}

	public GeoLocation getGuessedLocation_() {
		return guessedLocation_;
	}

	
	
}
