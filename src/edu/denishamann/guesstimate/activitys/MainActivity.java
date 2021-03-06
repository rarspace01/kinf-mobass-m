package edu.denishamann.guesstimate.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import edu.denishamann.guesstimate.R;

/**
 * @author PaulB
 */
public class MainActivity extends Activity {

	private static final String TAG = "Main";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i(TAG, "MainActivity created");
	}

	public void startGame(View view) {
		Intent i = new Intent(this, StartActivity.class);
		this.startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void startHighScore(View view) {
		Intent i = new Intent(this, HighScoreActivity.class);
		this.startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void startHowTo(View view) {
		Intent i = new Intent(this, HowToActivity.class);
		this.startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void startAbout(View view) {
		Intent i = new Intent(this, AboutActivity.class);
		this.startActivity(i);
		overridePendingTransition(0, 0);
	}
}
