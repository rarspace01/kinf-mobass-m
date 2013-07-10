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

public class HighScoreActivity extends Activity {

	private SQLiteDatabase dbConn_;
	private ListView lv_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);

		TextView congrats = (TextView) findViewById(R.id.congrats);
		Intent intent = getIntent();
		if (intent.hasExtra("gameEnded")) {
			congrats.setText("Congratulations");
			getHighscores(Game.getInstance().getPlayerName_(), Game
					.getInstance().getSuccessfulLocations(), Game.getInstance()
					.getDifficulty());
		} else {
			congrats.setText("");
			getHighscores("", -1, -1);
		}

		// retrieve highscores

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

	public void getHighscores(String playername, int successfulLocations,
			int difficulty) {
		int selector = -1;
		SQLiteDatamanager dbManager = new SQLiteDatamanager(this);
		dbConn_ = dbManager.getReadableDatabase();
		ArrayList<String> highScoreList = new ArrayList<String>();
		String tmpHighscore = "";

		// sql qry
		// dbConn_.rawQuery(""

		// create cursor based on qry
		Cursor currentCursor = dbConn_
				.rawQuery(
						"SELECT name, score, difficulty FROM highscore ORDER BY score DESC, difficulty DESC;",
						null);

		// 1. pos on cursor
		currentCursor.moveToFirst();

		// über alle cursor inhalte
		int i = 0;
		while (!currentCursor.isAfterLast() && i < 9) {
			tmpHighscore = currentCursor.getString(0) + " - "
					+ currentCursor.getInt(1);
			if (currentCursor.getInt(2) == 0) {
				tmpHighscore = tmpHighscore + " - Easy";
			} else {
				tmpHighscore = tmpHighscore + " - Hard";
			}

			Log.i("HighScore", "TMP Highscore"+tmpHighscore);
			highScoreList.add(tmpHighscore);

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

		lv_ = (ListView) findViewById(R.id.listHighscore);
		lv_.setSelector(R.drawable.item_background);
		// Instanciating an array list (you don't need to do this, you already
		// have yours)
		// This is the array adapter, it takes the context of the activity as a
		// first
		// parameter, the type of list view as a second parameter and your array
		// as a third parameter
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				R.layout.listview, highScoreList);
		lv_.setAdapter(arrayAdapter);

		lv_.setItemChecked(selector, true);
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
