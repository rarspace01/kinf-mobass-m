package edu.denishamann.guesstimate.model;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.database.SQLiteDatamanager;
import edu.denishamann.guesstimate.lateration.CircularLateration;
import edu.denishamann.guesstimate.lateration.LocationUtil;
import edu.denishamann.guesstimate.lateration.PseudoLateration;

public class Game {

	private static final String TAG = "Game";

	private GeoLocation calculatedLocation;
	private List<GuessPoint> pointsToGuess;
	private long endTime;
	private int successfulLocations;
	private int difficulty_;
	private String playerName_ = "";
	private IGuessCollection currentGuessCollection;
	private SQLiteDatabase dbConn;
	private float alertRadius_ = 50; // Distance when location was
										// successfully approached

	private boolean USE_CIRCULARLATERATION = true;
	private int PLAYTIME = 10; // Rundenzeit in Min

	private static Game instance;

	public static Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}

	public void startGame(int difficulty, String playerName,
			boolean useRealLateration) {
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
			pointsToGuess = currentGuessCollection.getNearest(currentLocation,
					4, 0);
		}
		return pointsToGuess;
	}

	public boolean evaluateGuesses() {
		if (!everyPointHasGuess()) {
			Log.i(TAG, "not enough guesses");
			return false;
		}

		if (USE_CIRCULARLATERATION) {
			Log.i(TAG, "using circular");
			calculatedLocation = new CircularLateration()
					.getLateration(pointsToGuess);
		} else {
			Log.i(TAG, "using pseudo lateration");
			calculatedLocation = new PseudoLateration()
					.getLateration(pointsToGuess);
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

	public boolean isNearGuessedLocation(GeoLocation currentLocation) {
		boolean isNear = false;

		if (LocationUtil.distance(currentLocation, this.calculatedLocation) < this.alertRadius_) {
			isNear = true;
		}

		return isNear;
	}

	public boolean isNearGuessedLocation(GeoPoint curLoc) {
		return isNearGuessedLocation(new GeoLocation(curLoc));
	}

	public void guessedLocationApproached() {
		Log.i(TAG, "Guessed Lcoation approached");
		successfulLocations++;
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
		Log.i(TAG, "HighScores: " + successfulLocations);
		// save highscore
		SQLiteDatamanager dbManager = new SQLiteDatamanager(context);
		dbConn = dbManager.getWritableDatabase();

		// sql qry
		dbConn.execSQL("INSERT INTO highscore (name, score, difficulty) VALUES ('"
				+ this.playerName_
				+ "','"
				+ this.successfulLocations
				+ "','"
				+ this.difficulty_ + "');");

		// close connection and manager properly
		dbConn.close();
		dbManager.close();

		difficulty_ = -1;
		endTime = -1;
		successfulLocations = 0;
		pointsToGuess = null;

		USE_CIRCULARLATERATION = true;
	}

	public void giveUp() {
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

	public float getAlertRadius() {
		return alertRadius_;
	}

}
