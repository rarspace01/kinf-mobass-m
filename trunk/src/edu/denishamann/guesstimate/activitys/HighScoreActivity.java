package edu.denishamann.guesstimate.activitys;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.database.SQLiteDatamanager;
import edu.denishamann.guesstimate.model.Game;

/**
 * @author PaulB
 */
public class HighScoreActivity extends Activity {

	private static final String TAG = "Highscore";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);

		TextView congrats = (TextView) findViewById(R.id.congrats);
		Intent intent = getIntent();
		// if you got here from the map e.g. the game has ended
		// set a congratulations text
		if (intent.hasExtra("gameEnded")) {
			congrats.setText("Congratulations");
			getHighscores(Game.getInstance().getPlayerName_(), Game
					.getInstance().getSuccessfulLocations(), Game.getInstance()
					.getDifficulty());
		} else {
			congrats.setText("");
			getHighscores();
		}

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				startActivity(new Intent(HighScoreActivity.this, MainActivity.class));
				break;
			default:
				break;
		}

		return true;
	}

	/**
	 * Retrieves the highscore from the device when a player finished a game.
	 *
	 * @param playername          Name of the player
	 * @param successfulLocations Scored locations
	 * @param difficulty          Played difficulty
	 */
	private void getHighscores(String playername, int successfulLocations, int difficulty) {
		SQLiteDatamanager dbManager = new SQLiteDatamanager(this);
		SQLiteDatabase dbConn_ = dbManager.getReadableDatabase();

		Cursor currentCursor = dbConn_.rawQuery(
				"SELECT name, score, difficulty FROM highscore ORDER BY score DESC, difficulty DESC;",
				null);
		currentCursor.moveToFirst();

		ArrayList<String> highScoreList = new ArrayList<String>();
		int i = 0;
		int selector = -1;
		// get maximum 10 highscores
		while (!currentCursor.isAfterLast() && i < 9) {
			String tmpHighscore = currentCursor.getString(0) + " - " + currentCursor.getInt(1);
			if (currentCursor.getInt(2) == 0) {
				tmpHighscore = tmpHighscore + " - Easy";
			} else {
				tmpHighscore = tmpHighscore + " - Normal";
			}

			Log.i(TAG, "TMP Highscore" + tmpHighscore);
			highScoreList.add(tmpHighscore);

			// check if the currentCursor is the same as the player that finished the game
			// set the selector on this position
			if (currentCursor.getString(0).equals(playername)
					&& currentCursor.getInt(1) == successfulLocations
					&& currentCursor.getInt(2) == difficulty && selector == -1) {
				selector = i;
			}

			currentCursor.moveToNext();
			i++;
		}

		currentCursor.close();
		dbConn_.close();
		dbManager.close();

		ListView lv = (ListView) findViewById(R.id.listHighscore);
		lv.setSelector(R.drawable.item_background);

		//fill the ListView
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.listview, highScoreList);
		lv.setAdapter(arrayAdapter);

		lv.setItemChecked(selector, true);
	}

	/**
	 * Retrieves the highscore from the device when called from the main menu.
	 */
	private void getHighscores() {
		getHighscores("", -1, -1);
	}

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}
}
