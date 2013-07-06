package edu.denishamann.guesstimate.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import edu.denishamann.guesstimate.R;

public class GuessActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//hier müssen die Guess Locations geladen werden
		
		setContentView(R.layout.activity_guess);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guess, menu);
		return true;
	}

	public void startGame(View view) {
		
		//hier muss per data die geratenen guesslocations übergeben werden
		Intent i = new Intent(this, MapActivity.class);

		this.startActivity(i);
		overridePendingTransition(0, 0);
	}

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}
}
