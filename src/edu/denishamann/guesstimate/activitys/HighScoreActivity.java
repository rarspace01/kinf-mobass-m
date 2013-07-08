package edu.denishamann.guesstimate.activitys;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.denishamann.guesstimate.R;
import edu.denishamann.guesstimate.database.SQLiteDatamanager;

public class HighScoreActivity extends Activity {

	private SQLiteDatabase dbConn_;
	private ListView       lv_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.out.println("starting HS activity");

		setContentView(R.layout.activity_high_score);

		//retrieve highscores
		getHighscores();

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

	public void getHighscores() {

		SQLiteDatamanager dbManager = new SQLiteDatamanager(this);
		dbConn_ = dbManager.getReadableDatabase();
		ArrayList<String> highScoreList = new ArrayList<String>();
		String tmpHighscore = "";

		//sql qry
		//dbConn_.rawQuery(""

		//create cursor based on qry
		Cursor currentCursor = dbConn_.rawQuery("SELECT name, score, difficulty FROM highscore ORDER BY score DESC;",
				null);

		//1. pos on cursor
		currentCursor.moveToFirst();

		//über alle cursor inhalte
		while (!currentCursor.isAfterLast()) {
			currentCursor.getString(0);
			currentCursor.getInt(1);
			currentCursor.getInt(2);
			tmpHighscore = currentCursor.getString(0) + " - " + currentCursor.getInt(1);

			if (currentCursor.getInt(2) == 0) {
				tmpHighscore = tmpHighscore + " - Easy";
			} else {
				tmpHighscore = tmpHighscore + " - Hard";
			}

			System.out.println(tmpHighscore);

			highScoreList.add(tmpHighscore);

			currentCursor.moveToNext();

		}

		currentCursor.close();
		dbConn_.close();
		dbManager.close();


		lv_ = (ListView) findViewById(R.id.listHighscore);
		// Instanciating an array list (you don't need to do this, you already have yours)
		// This is the array adapter, it takes the context of the activity as a first // parameter, the type of list view as a second parameter and your array as a third parameter
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				highScoreList);
		lv_.setAdapter(arrayAdapter);

	}


	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
