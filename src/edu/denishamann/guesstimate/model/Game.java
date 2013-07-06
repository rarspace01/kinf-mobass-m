package edu.denishamann.guesstimate.model;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.database.SQLiteDatamanager;
import edu.denishamann.guesstimate.lateration.CircularLateration;
import edu.denishamann.guesstimate.lateration.PseudoLateration;

public class Game {

	private GeoLocation			guessedLocation_;
	private List<GuessPoint>	guessedRanges_			= new LinkedList<GuessPoint>();
	private int					gamestate_				= 0;
	private long				timeLeft_				= 0;
	private int					successfullRounds_		= 0;
	private int					difficulty_				= 0;
	private String playerName_;
	private IGuessCollection	currentGuessCollection	= null;
	private SQLiteDatabase dbConn;

	private boolean				USE_CIRCULARLATERATION	= true;
	private int					PLAYTIME				= 30;							//Rundenzeit in Min

	private static Game			uniqueInstance_;

	public static Game getUniqueInstance() {
		if (uniqueInstance_ == null) {
			uniqueInstance_ = new Game();
		}
		return uniqueInstance_;
	}

	
	
	public long getTimeLeft_() {
		return timeLeft_;
	}



	public void setTimeLeft_(long timeLeft_) {
		this.timeLeft_ = timeLeft_;
	}



	public void startGame(int difficulty, String playerName) {
		this.playerName_=playerName;
		this.currentGuessCollection = new GuessCollection();
		this.difficulty_ = difficulty;
		gamestate_ = 1;
		timeLeft_ = System.currentTimeMillis() + 1000 * 60 * PLAYTIME;
		
		//clear guessed values
		guessedRanges_.clear();
		
		//clear highscore
		this.successfullRounds_=0;
		
	}

	public List<GuessPoint> getLocationsToBeGuessed(GeoLocation currentLocation) {
		return this.currentGuessCollection.getNearest(currentLocation, 4, 0);
	}

	public void addGuess(GuessPoint gp) {
		guessedRanges_.add(gp);
	}

	public int evaluateGuesses() {
		int result = 0;

		if (guessedRanges_.size() < 4) {
			result = 1;
			Log.i("GM", "not enough guesses");
		} else {
			if (USE_CIRCULARLATERATION) {
				System.out.println("using circular");
				guessedLocation_ = new CircularLateration().getLateration(guessedRanges_);
			} else {
				System.out.println("using pseudo lateration");
				guessedLocation_ = new PseudoLateration().getLateration(guessedRanges_);
			}
		}

		return result;
	}

	public boolean guessedLocationApproached(Context context){
		boolean hasRoundsLeft=false;
		if(System.currentTimeMillis()<timeLeft_){
			this.successfullRounds_++;
			hasRoundsLeft = true;
		} else {
			System.out.println("HS: "+successfullRounds_);
			//save highscore
			SQLiteDatamanager dbManager = new SQLiteDatamanager(context);
			dbConn = dbManager.getWritableDatabase();
			
			//sql qry
			dbConn.execSQL("INSERT INTO highscore (name, score, difficulty) VALUES ('"+this.playerName_+"','"+this.successfullRounds_+"','"+this.difficulty_+"');");
			
			//clear guessed values
			guessedRanges_.clear();
			
			//clear highscore
			this.successfullRounds_=0;
			
		}
		return hasRoundsLeft;
	}

	public GeoLocation getGuessedLocation() {

		if (guessedLocation_ == null) {
			evaluateGuesses();
		}

		return guessedLocation_;
	}

	public int getDifficulty() {
		return difficulty_;
	}

}
