package edu.denishamann.guesstimate.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.database.SQLiteDatamanager;
import edu.denishamann.guesstimate.lateration.CircularLateration;
import edu.denishamann.guesstimate.lateration.PseudoLateration;

public class Game {

	private GeoLocation      calculatedLocation;
	private List<GuessPoint> pointsToGuess;
	private long             endTime;
	private int              successfulLocations;
	private int              difficulty_;
	private String           playerName_;
	private IGuessCollection currentGuessCollection;
	private SQLiteDatabase   dbConn;

	private boolean USE_CIRCULARLATERATION = true;
	private int     PLAYTIME               = 30;                            //Rundenzeit in Min

	private static Game instance;

	public static Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}

	public void startGame(int difficulty, String playerName, boolean useRealLateration) {
		this.playerName_ = playerName.trim();
		this.currentGuessCollection = new GuessCollection();
		this.difficulty_ = difficulty;
		this.endTime = System.currentTimeMillis() + 1000 * 60 * PLAYTIME;
		this.successfulLocations = 0;

		USE_CIRCULARLATERATION = useRealLateration;
	}

	public List<GuessPoint> getLocationsToBeGuessed() {
		if (pointsToGuess == null) {
			pointsToGuess = currentGuessCollection.getRandom(4);
		}
		return pointsToGuess;
	}

	public List<GuessPoint> getLocationsToBeGuessed(GeoLocation currentLocation) {
		if (pointsToGuess == null) {
			pointsToGuess = currentGuessCollection.getNearest(currentLocation, 4, 0);
		}
		return pointsToGuess;
	}

	public boolean evaluateGuesses() {
		if (!everyPointHasGuess()) {
			Log.i("GM", "not enough guesses");
			return false;
		}

		if (USE_CIRCULARLATERATION) {
			Log.i("GM", "using circular");
			calculatedLocation = new CircularLateration().getLateration(pointsToGuess);
		} else {
			Log.i("GM", "using pseudo lateration");
			calculatedLocation = new PseudoLateration().getLateration(pointsToGuess);
		}

		return true;
	}

	private boolean everyPointHasGuess() {
		for (GuessPoint gp : pointsToGuess) {
			if (!gp.hasBeenGuessed()) {
				return false;
			}
		}
		return true;
	}

	public void guessedLocationApproached() {
		Log.i("GM", "Guessed Lcoation approached");
		if (getTimeLeft() > 0) {
			Log.i("GM", "Still timeleft: " + (int) (getTimeLeft() / 1000));
			successfulLocations++;
		} else {
			Log.e("GM", "undefined State");
		}
		pointsToGuess = null;
	}

	public GeoLocation getCalculatedLocation() {
		return calculatedLocation;
	}

	public int getDifficulty() {
		return difficulty_;
	}

	public void setEndTime(int i) {
		endTime = i;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getPLAYTIME() {
		return PLAYTIME;
	}

	public long getTimeLeft() {
		return (endTime - System.currentTimeMillis());
	}

	public void gameEnded(Context context) {
		Log.i("GM", "HS: " + successfulLocations);
		//save highscore
		SQLiteDatamanager dbManager = new SQLiteDatamanager(context);
		dbConn = dbManager.getWritableDatabase();

		//sql qry
		dbConn.execSQL("INSERT INTO highscore (name, score, difficulty) VALUES ('" + this.playerName_ + "','"
				+ this.successfulLocations + "','" + this.difficulty_ + "');");

		dbManager.close();
		dbConn.close();

		playerName_ = "";
		difficulty_ = -1;
		endTime = -1;
		successfulLocations = 0;
		pointsToGuess = null;

		USE_CIRCULARLATERATION = true;
	}

	public void giveUp() {
		playerName_ = "";
		difficulty_ = -1;
		endTime = -1;
		successfulLocations = 0;
		pointsToGuess = null;

		USE_CIRCULARLATERATION = true;
	}

	public int getSuccessfulLocations() {
		return successfulLocations;
	}

	public String getPlayerName_() {
		return playerName_;
	}
}
