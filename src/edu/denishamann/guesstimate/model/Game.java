package edu.denishamann.guesstimate.model;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.denishamann.guesstimate.database.GuessCollection;
import edu.denishamann.guesstimate.database.IGuessCollection;
import edu.denishamann.guesstimate.database.SQLiteDatamanager;
import edu.denishamann.guesstimate.lateration.CircularLateration;
import edu.denishamann.guesstimate.lateration.LocationUtil;
import edu.denishamann.guesstimate.lateration.PseudoLateration;
import edu.denishamann.io.HashUtil;

/**
 * Class for managing the game states/model
 * 
 * @author denis
 * 
 */
public class Game {

	private static final String TAG = "Game";

	private GeoLocation calculatedLocation_;
	private List<GuessPoint> pointsToGuess_;
	private long endTime_;
	private int successfulLocations_;
	private int difficulty_;
	private String playerName_ = "";
	private IGuessCollection currentGuessCollection_;
	private String lastHash_ = "";
	private float alertRadius_ = 50; // Distance of approach radius in meters

	private boolean useCircularLateration_ = true;
	private int maxRoundTime_ = 30; // Rundenzeit in Min

	private static Game instance;

	public static Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}

	/**
	 * Starts a game session
	 * 
	 * @param difficulty
	 *            - 0 = easy, 1= normal
	 * @param playerName
	 *            - Name of the Player
	 * @param useRealLateration
	 *            - true = use circular, false = use pseudoLateration
	 */
	public void startGame(int difficulty, String playerName,
			boolean useRealLateration) {
		this.playerName_ = playerName.trim();
		this.currentGuessCollection_ = new GuessCollection();
		this.difficulty_ = difficulty;
		this.endTime_ = System.currentTimeMillis() + 1000 * 60 * maxRoundTime_;
		this.successfulLocations_ = 0;

		useCircularLateration_ = useRealLateration;
	}

	/**
	 * retrieves the Locations to be gussed
	 * 
	 * @return {@link List}<{@link GuessPoint}> - Locations to be gussed
	 */
	public List<GuessPoint> getLocationsToBeGuessed() {
		if (pointsToGuess_ == null) {
			pointsToGuess_ = currentGuessCollection_.getRandom(4);
		}
		return pointsToGuess_;
	}

	// /**
	// * retrieves the Locations to be guessed, based on a current location
	// * @param currentLocation - The current Location
	// * @return {@link List}<{@link GuessPoint}> - Locations to be guessed
	// */
	// public List<GuessPoint> getLocationsToBeGuessed(GeoLocation
	// currentLocation) {
	// if (pointsToGuess == null) {
	// pointsToGuess = currentGuessCollection.getNearest(currentLocation,
	// 4, 0);
	// }
	// return pointsToGuess;
	// }

	/**
	 * uses a lateration algorithm to compute the gussedLocation
	 * 
	 * @return {@link boolean} - {@link true} if computation was successful,
	 *         {@link false} if there a still guesses missing
	 */
	public boolean evaluateGuesses() {
		boolean isComputed = false;
		// check if enough points are available
		if (!hasEnoughPointsGuessed()) {
			Log.i(TAG, "not enough guesses");
		} else {
			// hash value for proximity timeout
			lastHash_ = HashUtil.sha1("" + System.currentTimeMillis());

			// choose the lateration method
			if (useCircularLateration_) {
				Log.i(TAG, "using circular");
				calculatedLocation_ = new CircularLateration()
						.getLateration(pointsToGuess_);
			} else {
				Log.i(TAG, "using pseudo lateration");
				calculatedLocation_ = new PseudoLateration()
						.getLateration(pointsToGuess_);
			}
			isComputed = true;
		}
		return isComputed;
	}

	/**
	 * checks if enough distances has been guessed
	 * 
	 * @return {@link boolean} - true if enough points gussed
	 */
	private boolean hasEnoughPointsGuessed() {
		int guessCount = 0;
		boolean hasEnoughPointsGuessed = false;

		// Iterate trough every guesspoint
		for (GuessPoint gp : pointsToGuess_) {
			if (gp.hasBeenGuessed()) {
				guessCount++;
			}
		}

		// check for count
		if (guessCount >= 4) {
			hasEnoughPointsGuessed = true;
		}

		return hasEnoughPointsGuessed;
	}

	/**
	 * evaluates if a given Location is near the calculate Location
	 * 
	 * @param currentLocation
	 *            - as {@link GeoLocation}
	 * @return {@link boolean} - {@linkplain true} or {@link false} if the givne
	 *         location is near the calced location
	 */
	public boolean isNearGuessedLocation(GeoLocation currentLocation) {
		boolean isNear = false;

		// use the LocationUtil for distance meassurements, compare distance to
		// alertRadius
		if (LocationUtil.distance(currentLocation, this.calculatedLocation_) < alertRadius_) {
			isNear = true;
		}

		return isNear;
	}

	/**
	 * evaluates if a given Location is near the calculate Location
	 * 
	 * @param currentLocation
	 *            - as {@link GeoPoint}
	 * @return {@link boolean} - {@linkplain true} or {@link false} if the given
	 *         location is near the calced location
	 */
	public boolean isNearGuessedLocation(GeoPoint curLoc) {
		return isNearGuessedLocation(new GeoLocation(curLoc));
	}

	/**
	 * method to be called only on successful location approach, resets the current points to
	 * be guessed
	 */
	public void guessedLocationApproached() {
		Log.i(TAG, "Guessed Lcoation approached");
		//add succsessfull location guess
		successfulLocations_++;
		pointsToGuess_ = null;
		calculatedLocation_ = null;
	}

	public GeoLocation getCalculatedLocation() {
		return calculatedLocation_;
	}

	public int getDifficulty() {
		return difficulty_;
	}

	public void setEndTime(int i) {
		endTime_ = i;
	}

	public long getEndTime() {
		return endTime_;
	}

	public int getPLAYTIME() {
		return maxRoundTime_;
	}

	public long getTimeLeft() {
		return (endTime_ - System.currentTimeMillis());
	}

	/**
	 * ends a currend game session. saves highscore to the database
	 * 
	 * @param context
	 */
	public void gameEnded(Context context) {
		Log.i(TAG, "HighScores: " + successfulLocations_);
		// sqllite connection
		SQLiteDatamanager dbManager = new SQLiteDatamanager(context);
		SQLiteDatabase dbConn = dbManager.getWritableDatabase();

		// cleanup playername
		this.playerName_.replaceAll("", "");

		// sql statement preparement
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", this.playerName_);
		contentValues.put("score", this.successfulLocations_);
		contentValues.put("difficulty", this.difficulty_);

		// insert qry
		dbConn.insert("highscore", null, contentValues);

		// close connection and manager properly
		dbConn.close();
		dbManager.close();

		difficulty_ = -1;
		endTime_ = -1;
		successfulLocations_ = 0;
		pointsToGuess_ = null;

		useCircularLateration_ = true;
	}

	/**
	 * give up a current game. highscore wont be saved.
	 */
	public void giveUp() {
		difficulty_ = -1;
		endTime_ = -1;
		successfulLocations_ = 0;
		pointsToGuess_ = null;

		useCircularLateration_ = true;
	}

	public int getSuccessfulLocations() {
		return successfulLocations_;
	}

	public String getPlayerName_() {
		return playerName_;
	}

	public float getAlertRadius() {
		return alertRadius_;
	}

	public String getLastHash() {
		return lastHash_;
	}

}
