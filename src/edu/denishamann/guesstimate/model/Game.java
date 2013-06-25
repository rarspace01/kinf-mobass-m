package edu.denishamann.guesstimate.model;

import java.util.List;

public class Game {

	private GeoLocation guessedLocation_;
	private List<GuessPoint> guessedRanges_;
	private int gamestate_=0;
	private int difficulty_=0;
	
	public void startGame(int difficulty){
		this.difficulty_ = difficulty;
		gamestate_=1;
	}
	
	public void addGuess(GuessPoint gp){
		guessedRanges_.add(gp);
	}
	
	public List<GeoLocation> getNextGuesspoints(){
		
		return null;
	}
	
	public int evalueGuesses(){
		int result=0;
		
		if(guessedRanges_.size()<4){
			result = 1;
		}else{
			guessedLocation_ = null;
		}
		
		return result;
	}

	
	
}
